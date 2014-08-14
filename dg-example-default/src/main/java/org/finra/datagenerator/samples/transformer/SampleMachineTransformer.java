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

package org.finra.datagenerator.samples.transformer;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

import java.util.Map;
import java.util.Random;

/**
 * A simple transformer replacing the reserved string "customplaceholder" with a random integer.
 */
public class SampleMachineTransformer implements DataTransformer {

    private static final Logger log = Logger.getLogger(SampleMachineTransformer.class);
    private final Random rand = new Random(System.currentTimeMillis());

    /**
     * The transform method for this DataTransformer
     * @param cr a reference to DataPipe from which to read the current map
     */
    public void transform(DataPipe cr) {
        for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
            String value = entry.getValue();

            if (value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                entry.setValue(String.valueOf(ran));
            }
        }
    }

}
