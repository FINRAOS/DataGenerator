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

package org.finra.datagenerator;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.EquivalenceClassTransformer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.multithreaded.SingleThreadedProcessing;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses a multithreaded approach to process Frontiers in parallel. 
 *
 * Created by Brijesh
 */
public class SparkDistributorJava implements SearchDistributor, Serializable {

    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(SparkDistributorJava.class);

    private int threadCount = 1;
    private final Queue<Map<String, String>> queue = new ConcurrentLinkedQueue<>();
    private static DataConsumer dataConsumer = new DataConsumer();
    private final AtomicBoolean searchExitFlag = new AtomicBoolean(false);
    private final AtomicBoolean hardExitFlag = new AtomicBoolean(false);
    private long maxNumberOfLines = -1;
    private final String masterURL;
    private final String[] outTemplate;

    /**
     * Sets the master URL
     *
     * @param masterURL a String containing master URL
     * @param outTemplate an array of String containing variable names of the model
     */
    public SparkDistributorJava(final String masterURL, final String[] outTemplate) {
        this.masterURL = masterURL;
        this.outTemplate = outTemplate;
    }

    /**
     * Sets the maximum number of lines to generate
     *
     * @param numberOfLines a long containing the maximum number of lines to
     *                      generate
     * @return a reference to the current DefaultDistributor
     */
    public SparkDistributorJava setMaxNumberOfLines(long numberOfLines) {

        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    /**
     * Sets the number of threads to use
     *
     * @param threadCount an int containing the thread count
     * @return a reference to the current DefaultDistributor
     */
    public SparkDistributorJava setThreadCount(int threadCount) {

        this.threadCount = threadCount;
        return this;
    }

    @Override
    public SparkDistributorJava setDataConsumer(DataConsumer dataConsumer) {

        this.dataConsumer = dataConsumer;
        dataConsumer.setExitFlag(hardExitFlag);
        return this;
    }

    @Override
    public void distribute(final List<Frontier> frontierList) {

        final SingleThreadedProcessing singleThreadedProcessing = new SingleThreadedProcessing(maxNumberOfLines);

        final SparkConf conf = new SparkConf().setAppName("dg-spark").setMaster(masterURL);

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> mapJavaRDD = sc.parallelize(frontierList).map(new Function<Frontier, String>() {

            String finalResult;

            StringBuilder stringBuilder = new StringBuilder();

            @Override
            public String call(Frontier frontier) {

                dataConsumer.addDataTransformer(new SampleMachineTransformer());

                dataConsumer.addDataTransformer(new EquivalenceClassTransformer());

                singleThreadedProcessing.setDataConsumer(dataConsumer);

                try {
                    stringBuilder = frontier.searchForScenarios(singleThreadedProcessing, searchExitFlag, outTemplate, stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (stringBuilder != null) {

                    finalResult = stringBuilder.toString();
                }

                return finalResult;
            }
        });

        Random random = new Random();

        String path = "./dg-spark/out/result" + random.nextInt(1000) + ".txt";

        mapJavaRDD.saveAsTextFile(path);

    }
}