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

import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

import java.util.Map;
import java.util.Random;

/**
 * A Simple transformer which replaces two custom tags with dynamic
 * values
 */
public class SampleMachineTransformer implements DataTransformer {

    private final Random rand = new Random(System.currentTimeMillis());

    /**
     * Replace known tags in the current data values with actual values as appropriate
     *
     * @param cr a reference to DataPipe from which to read the current map
     */
    public void transform(DataPipe cr) {
        Map<String, String> map = cr.getDataMap();

        for (String key : map.keySet()) {
            String value = map.get(key);

            if (value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                map.put(key, String.valueOf(ran));
            } else if (value.equals("#{divideBy2}")) {
                String i = map.get("var_out_V3");
                String result = String.valueOf(Integer.valueOf(i) / 2);
                map.put(key, result);
            }
        }
    }

}
