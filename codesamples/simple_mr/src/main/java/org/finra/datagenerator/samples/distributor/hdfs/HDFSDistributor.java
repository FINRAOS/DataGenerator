package org.finra.datagenerator.samples.distributor.hdfs;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;

/**
 * Created by robbinbr on 3/24/14.
 */
public class HDFSDistributor implements SearchDistributor {

    protected static final Logger log = Logger.getLogger(HDFSDistributor.class);

    private static final String ENCODING = "UTF-8";
    private static final Gson gson = new Gson();

    private String stateMachineText;
    private String hdfsFileRoot;
    private JobConf configuration;
    private Path mapperInputFilePath;
    private Path mapperOutputFilePath;
    private String mapperOutputFileName;
    private long maxNumberOfLines;
    private String reportingHost;

    public SearchDistributor setDataConsumer(DataConsumer dataConsumer) {
        return this;
    }

    public void setFlag(String name, AtomicBoolean flag) {
    }

    public HDFSDistributor setFileRoot(String fileRoot) {
        this.hdfsFileRoot = fileRoot;
        this.mapperInputFilePath = new Path(hdfsFileRoot + "/input.dat");
        if (mapperOutputFileName != null) {
            this.mapperOutputFilePath = new Path(hdfsFileRoot + "/" + mapperOutputFileName);
        }
        return this;
    }

    public HDFSDistributor setReportingHost(String hostPort) {
        this.reportingHost = hostPort;
        return this;
    }

    public HDFSDistributor setOutputFileDir(String fileName) {
        this.mapperOutputFileName = fileName;
        if (hdfsFileRoot != null) {
            this.mapperOutputFilePath = new Path(hdfsFileRoot + "/" + mapperOutputFileName);
        }
        return this;
    }

    public HDFSDistributor setConfiguration(Configuration configuration) {
        this.configuration = new JobConf(configuration);
        return this;
    }

    @Override
    public SearchDistributor setStateMachineText(String stateMachineText) {
        this.stateMachineText = stateMachineText;
        return this;
    }

    public SearchDistributor setMaxNumberOfLines(long maxNumberOfLines) {
        this.maxNumberOfLines = maxNumberOfLines;
        return this;
    }

    @Override
    public void distribute(List<SearchProblem> searchProblemList) {
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
        Job ret = new Job(configuration);
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

    public void writeProblemsToHDFS(List<SearchProblem> problems) throws IOException {
        FileSystem fs = FileSystem.get(configuration);
        log.info("hdfsFileRoot = " + hdfsFileRoot);
        FSDataOutputStream out = fs.create(mapperInputFilePath);

        StringBuilder sb = new StringBuilder();
        for (SearchProblem problem : problems) {
            String problemString = problem.toJson();
            sb.append(problemString.replace("\n", "").replace("\t", "").replace("\r", ""));
            sb.append("\n");
        }

        try {
            out.write(sb.toString().getBytes());
        } catch (IOException e) {
            log.error("Problem writing " + mapperInputFilePath + " prior to MR job execution");
        } finally {
            out.close();
        }
    }

}
