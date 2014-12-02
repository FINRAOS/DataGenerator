/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.samples.distributor.hdfs;

import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.engine.scxml.SCXMLGapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 3/24/14.
 */
public class HDFSDistributor implements SearchDistributor {

    private static final Logger log = Logger.getLogger(HDFSDistributor.class);

    private static final String ENCODING = "UTF-8";
    private static final Gson GSON = new Gson();

    private String stateMachineText;
    private String hdfsFileRoot;
    private JobConf configuration;
    private Path mapperInputFilePath;
    private Path mapperOutputFilePath;
    private String mapperOutputFileName;
    private long maxNumberOfLines;
    private String reportingHost;

    // TODO: This method is not actually doing anything?

    /**
     * Set the DataConsumer object
     *
     * @param dataConsumer the DataConsumer which should be used by this Distributor (Writers, Transformers, and Reporters
     *                     should be configured)
     * @return the current object, with DataConsumer set
     */
    public SearchDistributor setDataConsumer(DataConsumer dataConsumer) {
        return this;
    }

    /**
     * Set the file root on HDFS where files (temp and final) can be written
     *
     * @param fileRoot A valid HDFS location with space for the output of the data generation
     * @return An updated HDFSDistributor with hdfsFileRoot set
     */
    public HDFSDistributor setFileRoot(String fileRoot) {
        this.hdfsFileRoot = fileRoot;
        this.mapperInputFilePath = new Path(hdfsFileRoot + "/input.dat");
        if (mapperOutputFileName != null) {
            this.mapperOutputFilePath = new Path(hdfsFileRoot + "/" + mapperOutputFileName);
        }
        return this;
    }

    /**
     * Set the reporting host port for this Distributor
     *
     * @param hostPort The port number to use when reporting
     * @return An updated HDFSDistributor with the host port set
     */
    public HDFSDistributor setReportingHost(String hostPort) {
        this.reportingHost = hostPort;
        return this;
    }

    /**
     * Set the output file directory (to be appended to hdfsFileRoot)
     *
     * @param fileName Path from hdfsFileRoot
     * @return an updated HDFSDistributor with output file name set
     */
    public HDFSDistributor setOutputFileDir(String fileName) {
        this.mapperOutputFileName = fileName;
        if (hdfsFileRoot != null) {
            this.mapperOutputFilePath = new Path(hdfsFileRoot + "/" + mapperOutputFileName);
        }
        return this;
    }

    /**
     * Set the HDFS Configuration for this distributor (should be the same instance configured for the
     * MapReduce job by ToolRunner)
     *
     * @param configuration A configuration instance to use for Mapper tasks
     * @return An updated HDFSDistributor with Configuration object set
     */
    public HDFSDistributor setConfiguration(Configuration configuration) {
        this.configuration = new JobConf(configuration);
        return this;
    }

    /**
     * Set the XML text for the state machine serving as an input model for data generation
     *
     * @param stateMachineText a String containing the state machine XML
     * @return An updated SearchDistributor with state machine text set
     */
    public SearchDistributor setStateMachineText(String stateMachineText) {
        this.stateMachineText = stateMachineText;
        return this;
    }

    /**
     * Set the max number of lines which should be written by this Distributor
     *
     * @param maxNumberOfLines Maximum number of lines to be written by this Distributor
     * @return An updated SearchDistributor with a maximum line count set
     */

    public SearchDistributor setMaxNumberOfLines(long maxNumberOfLines) {
        this.maxNumberOfLines = maxNumberOfLines;
        return this;
    }

    @Override
    public void distribute(List<Frontier> searchProblemList) {
        // We need to write the List out to a file on HDFS
        // That file will be input into the MR job

        // Add variables job configuration
        configuration.set("stateMachineText", stateMachineText);
        configuration.setLong("maxNumberOfLines", maxNumberOfLines);

        // Write input problems
        try {
            writeProblemsToHDFS(searchProblemList);
        } catch (IOException e) {
            log.error("Problem writing " + mapperInputFilePath + " prior to MR job execution");
            return;
        }

        // Prepare and submit job
        try {
            Job job = prepareJob();
            job.waitForCompletion(true);
            log.info("DataGen MR job can be tracked at " + job.getTrackingURL());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Cleanup
    }

    private Job prepareJob() throws IOException {
        // Basic configuration

        configuration.setInt("mapreduce.input.lineinputformat.linespermap", 1);
        configuration.set("reportingHost", this.reportingHost);

        configuration.setBoolean("mapreduce.map.output.compress", true);
        configuration.setBoolean("mapred.compress.map.output", true);
        configuration.setBoolean("mapred.output.compress", true);
        configuration.setClass("mapred.map.output.compression.codec", GzipCodec.class, CompressionCodec.class);
        configuration.setClass("mapred.output.compression.codec", GzipCodec.class, CompressionCodec.class);

        /*        configuration.setBoolean("mapreduce.output.fileoutputformat.compress", true);
         configuration.setClass("mapreduce.output.fileoutputformat.compress.codec", GzipCodec.class, CompressionCodec.class);
         configuration.setCompressMapOutput(true);
         */
//        configuration.set("mapreduce.output.fileoutputformat.compress", "true");
//        configuration.set("mapreduce.output.fileoutputformat.compress.codec", "org.apache.hadoop.io.compress.GzipCodec");
//        configuration.set("mapreduce.output.fileoutputformat.compress.type", "BLOCK");
//        Job ret = new Job(configuration);
        Job ret = org.apache.hadoop.mapreduce.Job.getInstance(configuration);
        ret.setJarByClass(HDFSDistributor.class);
        ret.setJobName("PATH Test Data Generation");

        // Mapper
        ret.setMapperClass(DataGeneratorMapper.class);

        // Reducer (none)
        ret.setNumReduceTasks(0);

        // Input
        ret.setInputFormatClass(NLineInputFormat.class);
        NLineInputFormat.addInputPath(ret, mapperInputFilePath);

        // Output
        // [BTR] Saw this used in an example w/NLineInputFormatter
        // but not sure what it actually does ...
//        LazyOutputFormat.setOutputFormatClass(ret, TextOutputFormat.class);
        FileOutputFormat.setOutputPath(ret, mapperOutputFilePath);
        //ret.getConfiguration().setBoolean("mapred.output.compress", false);

        return ret;
    }

    /**
     * Convert a set of SearchProblem objects to Strings of JSON text, writing the array to
     * the HDFS location given by the HDFS file root. The written file serves as input to the
     * Mapper tasks (one Mapper per line in the file, which is also one SearchProblem)
     *
     * @param problems A List of Search Problems to write
     * @throws IOException if the file cannot be written to HDFS
     */
    public void writeProblemsToHDFS(List<Frontier> problems) throws IOException {
        FileSystem fs = FileSystem.get(configuration);
        log.info("hdfsFileRoot = " + hdfsFileRoot);

        StringBuilder sb = new StringBuilder();
        for (Frontier problem : problems) {
            SCXMLGapper gapper = new SCXMLGapper();
            Map<String, String> decomposition = gapper.decompose(problem, stateMachineText);
            String problemString = decomposition.get("target") + "|" + decomposition.get("variables") + "|";

            sb.append(problemString.replace("\n", "").replace("\t", "").replace("\r", ""));
            sb.append("\n");
        }

        try (FSDataOutputStream out = fs.create(mapperInputFilePath)) {
            out.write(sb.toString().getBytes());
        } catch (IOException e) {
            log.error("Problem writing " + mapperInputFilePath + " prior to MR job execution");
        }
    }

}
