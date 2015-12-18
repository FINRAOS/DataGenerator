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

import org.finra.datagenerator.engine.scxml.SCXMLEngine;

import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;

/**
 * Simple "Random Number Data Generator" example using Apache Spark.
 * Created by Brijesh on 6/1/2015.
 */

public final class Main implements Serializable {

    private Main() {
        //Private Constructor
    }

    /**
     * Entry point for the example.
     * @param argv Command-line arguments for the example
     * @throws IOException IO Exception
     */
    public static void main(String[] argv) throws IOException {

        try (InputStream is = Main.class.getResourceAsStream("/samplemachine.xml")) {

            SCXMLEngine scxmlEngine = new SCXMLEngine();

            scxmlEngine.setModelByInputFileStream(is);
            scxmlEngine.setBootstrapMin(1);

            String masterURL = "local[5]";
            //String masterURL = "spark://sandbox.hortonworks.com:7077";

            final String[] outTemplate = new String[]{"var_1_1", "var_1_2", "var_1_3", "var_1_4", "var_1_5", "var_1_6",
                    "var_2_1", "var_2_2", "var_2_3", "var_2_4", "var_2_5", "var_2_6"};

            SparkDistributorJava sparkDistributor = new SparkDistributorJava(masterURL, outTemplate);

            sparkDistributor.setMaxNumberOfLines(40);
            scxmlEngine.process(sparkDistributor);

        }
    }
}
