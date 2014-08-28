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

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.engine.scxml.SCXMLGapper;
import org.finra.datagenerator.samples.consumer.SampleMachineConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 4/7/2014.
 */
public class DataGeneratorMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    private static final Logger log = Logger.getLogger(DataGeneratorMapper.class);
    private String reportingHost;
    private String modelText;

    private final DefaultDistributor distributor = new DefaultDistributor();

    @Override
    public void setup(final Context context) {
        modelText = context.getConfiguration().get("stateMachineText");
        distributor.setMaxNumberOfLines(context.getConfiguration().getLong("maxNumberOfLines", 100000));

        reportingHost = context.getConfiguration().get("reportingHost", "NOTFOUND");
        log.info("Reporting host is:" + reportingHost);
    }


    @Override
    public void map(final LongWritable key, Text value, final Context context) throws IOException,
            InterruptedException {

        log.info("Entering the mapper");
        // Prepare DataConsumer
        SampleMachineConsumer wrappingConsumer = new SampleMachineConsumer(context);
        wrappingConsumer.setReportingHost(reportingHost);
        distributor.setDataConsumer(wrappingConsumer);

        // Prepare Problems (only one)
        List<Frontier> problemList = new ArrayList<>();

        SCXMLGapper gapper = new SCXMLGapper();
        String[] frontier = value.toString().split("\\|");
        String variables = frontier[1];
        String target = frontier[0];
        Map<String, String> decomposition = new HashMap<>();
        decomposition.put("model", modelText);
        decomposition.put("target", target);
        decomposition.put("variables", variables);
        Frontier problem = gapper.reproduce(decomposition);

        log.info(value.toString());
        problemList.add(problem);
        log.info("We have " + problemList.size() + " Problems");
        log.info("Distributing problems");
        // Execute
        distributor.distribute(problemList);

        log.info("Writing results");
    }
}
