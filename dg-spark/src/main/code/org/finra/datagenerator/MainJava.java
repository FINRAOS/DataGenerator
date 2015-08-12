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

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;

/**
 * Simple "Random Number Data Generator" example using Apache Spark.
 * Created by Brijesh on 6/1/2015.
 */

public final class MainJava implements Serializable {

    private MainJava() {
        //Private Constructor
    }

    /**
     * Entry point for the example.
     *
     * @param argv Command-line arguments for the example
     * @throws IOException IO Exception
     */
    public static void main(String[] argv) throws IOException {

        //You can define your own file "input.txt" in your directory with first line as "Total Count" and
        //second line as "Number of Split"

        //Read input data from file using File and InputStream
        File file = new File("./dg-spark/src/main/resources/file/input.txt");

        InputStream is = new FileInputStream(file);

        try {
            //Create instance of EngineImplementation
            RandomNumberEngine randomNumberEngine = new RandomNumberEngine();

            //Read the lines from text file
            randomNumberEngine.setModelByInputFileStream(is);

            //Define your host name here with port 7077 i.e hostname:7077
            String masterURL = "spark://sandbox.hortonworks.com:7077";

            //Define Spark Context and Scala Data Consumer object
            //SparkContext sparkContext = new SparkContext();
            ScalaDataConsumer scalaDataConsumer = new ScalaDataConsumer();

            //Create instance of SparkDistributor and set masterURL to Spark Context
            SparkDistributor sparkDistributor = new SparkDistributor(masterURL, scalaDataConsumer);

            //Generate data, distribute it and send it to data consumer
            randomNumberEngine.process(sparkDistributor);
        } finally {
            //Close the output Stream
            is.close();
        }
    }
}