/*
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
package org.finra.datagenerator.samples.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.defaults.ConsumerResult;

public class SampleMachineConsumer implements DataConsumer {

    protected static final Logger log = Logger.getLogger(SampleMachineConsumer.class);

    private final int myTemplateHashCode = "#{customplaceholder}".hashCode();
    private final int divideBy2HashCode = "#{divideBy2}".hashCode();
    private final Random rand = new Random(System.currentTimeMillis());

    @Override
    public void consume(ConsumerResult cr) {
    	// Go through our templates and fill them with values
        Map<String, String> originalRow = cr.getDataMap();
        HashMap<String, String> outputValues = new HashMap<>();
        
        for (Entry<String, String> entry : originalRow.entrySet()) {
            String value = entry.getValue();
            int hashCode = value.hashCode();

            if (hashCode == myTemplateHashCode && value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                value = "" + ran;
            }
            
            /*
             * Enhanced Demo
             */
            if (hashCode == divideBy2HashCode && value.equals("#{divideBy2}")) {
                value = "" + Integer.valueOf(outputValues.get("var_out_V3")) / 2;
            }
            outputValues.put(entry.getKey(), value);
        }

        // Using the values, compile our output. In our case, we will just write it to the console
        System.out.println("Row=" + outputValues.toString());
       
    }

}
