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

package org.finra.datagenerator.distributor.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.engine.Frontier;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Brijesh
 */
public class SparkDistributor implements SearchDistributor, Serializable {

    private static DataConsumer dataConsumer = new DataConsumer();
    private final AtomicBoolean searchExitFlag = new AtomicBoolean(false);
    private final AtomicBoolean hardExitFlag = new AtomicBoolean(false);
    private final String masterURL;

    private JavaRDD<Map<String, String>> generatedMaps;

    /**
     * Sets the master URL
     *
     * @param masterURL a String containing master URL
     */
    public SparkDistributor(final String masterURL) {
        this.masterURL = masterURL;
    }

    @Override
    public SparkDistributor setDataConsumer(DataConsumer dataConsumer) {
        SparkDistributor.dataConsumer = dataConsumer;
        dataConsumer.setExitFlag(hardExitFlag);
        return this;
    }

    @Override
    public void distribute(final List<Frontier> frontierList) {
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("dg-spark").setMaster(masterURL));

        generatedMaps = sc
                .parallelize(frontierList)
                .flatMap(new FlatMapFunction<Frontier, Map<String, String>>() {
                    @Override
                    public Iterable<Map<String, String>> call(Frontier frontier) {
                        LinkedList<Map<String, String>> storage = new LinkedList<>();
                        frontier.searchForScenarios(new CatchAndStoreProcessing(storage), searchExitFlag);

                        return storage;
                    }
                })
                .flatMap(new FlatMapFunction<Map<String, String>, Map<String, String>>() {
                    @Override
                    public Iterable<Map<String, String>> call(Map<String, String> initialVars) {
                        return SparkDistributor.dataConsumer.transformAndReturn(initialVars);
                    }
                });
    }

    public JavaRDD<Map<String, String>> getGeneratedMaps() {
        return generatedMaps;
    }
}
