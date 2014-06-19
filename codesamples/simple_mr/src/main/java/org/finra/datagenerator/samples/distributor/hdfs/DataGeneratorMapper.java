package org.finra.datagenerator.samples.distributor.hdfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.samples.consumer.SampleMachineConsumer;

import com.google.gson.Gson;

/**
 * Created by robbinbr on 4/7/2014.
 */
public class DataGeneratorMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    protected static final Logger log = Logger.getLogger(DataGeneratorMapper.class);
    private String reportingHost;

    private final DefaultDistributor distributor = new DefaultDistributor();

    @Override
    public void setup(final Context context) {

        //DataConsumer dataConsumer = gson.fromJson(context.getConfiguration().get("dataConsumer"), DataConsumer.class);
        //distributor.setDataConsumer(dataConsumer);
        distributor.setStateMachineText(context.getConfiguration().get("stateMachineText"));
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
        distributor.setExitFlag(new AtomicBoolean(false));

        // Prepare Problems (only one)
        List<SearchProblem> problemList = new ArrayList<SearchProblem>();
        SearchProblem problem = SearchProblem.fromJson(value.toString());
        log.info(value.toString());
        problemList.add(problem);
        log.info("We have " + problemList.size() + " Problems");
        log.info("Distributing problems");
        // Execute
        distributor.distribute(problemList);

        log.info("Writing results");
    }
}
