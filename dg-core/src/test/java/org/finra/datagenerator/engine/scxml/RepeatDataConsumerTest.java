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

package org.finra.datagenerator.engine.scxml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.finra.datagenerator.consumer.EquivalanceClassTransformerTest;
import org.finra.datagenerator.consumer.RepeatingDataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.writer.DefaultWriter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Yankop Yuriy
 */
public class RepeatDataConsumerTest {
    private String[] setVarDomain1 = new String[]{"0", "1", "2"};
    private String[] setVarDomain2 = new String[]{"0", "0.5", "1"};
    private int repeatNumber1 = 5;
    String lineSeparator = System.getProperty("line.separator");
    
    /**
     * Verify 'RepeatingDataConsumer' with one step scxml
     */
    @Test
    public void testOneStepWithRepeatingDataConsumer() {
        SCXMLEngine e = new SCXMLEngine();
        InputStream is = RepeatDataConsumerTest.class.getResourceAsStream("/oneStepTest.xml");
        e.setModelByInputFileStream(is);

        try {
            e.setBootstrapMin(1);
            RepeatingDataConsumer consumer = new RepeatingDataConsumer();
            consumer.setRepeatNumber(5);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            consumer.addDataWriter(new DefaultWriter(output, new String[] {"var_1"}));

            DefaultDistributor defaultDistributor = new DefaultDistributor();
            defaultDistributor.setThreadCount(1);
            defaultDistributor.setDataConsumer(consumer);

            e.process(defaultDistributor);
            StringBuffer expectedValue = new StringBuffer();
            for (int i = 0; i < 5; i++) {
                expectedValue.append("value1" + lineSeparator);
            }
            Assert.assertEquals("Oops! We wait for '" + expectedValue + "', but have '" + output + "'!", output.toString(), expectedValue.toString());
        } catch (Exception ex) {
            Assert.fail("Oops! Exception = " + e);
        }
    }

    /**
     * Verify 'RepeatingDataConsumer' with 'range' from 'dg-assign'
     */
    @Test
    public void rangeWithRepeatingDataConsumer() {
        SCXMLEngine e = new SCXMLEngine();

        InputStream is = EquivalanceClassTransformerTest.class.getResourceAsStream("/DGRangeTest.xml");
        e.setModelByInputFileStream(is);

        e.setBootstrapMin(1);
        RepeatingDataConsumer consumer = new RepeatingDataConsumer();
        consumer.setRepeatNumber(5);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        consumer.addDataWriter(new DefaultWriter(output, new String[] {"var_1", "var_2"}));

        DefaultDistributor defaultDistributor = new DefaultDistributor();
        defaultDistributor.setThreadCount(1);
        defaultDistributor.setDataConsumer(consumer);

        e.process(defaultDistributor);
        
        List<List<String>> allPossibleValues = generateAllPossibleValues4RepeatWithRange();
        for (String currentLine : output.toString().split(lineSeparator)) {
          // let's remove this generated record from list of all necessary records for this test
          String[] currentLineSplitted = currentLine.split("\\|");
          List<String> recordType12 = new ArrayList<String>();
          for (String currentLineValue : currentLineSplitted) {
              recordType12.add(currentLineValue);
          }
          if (allPossibleValues.contains(recordType12)) {
            allPossibleValues.remove(recordType12);
        } else {
            Assert.fail("Oops! '" + recordType12 + "' combination was generated, but we don't wait for it!");
        }
      }
        
      // we have removed all generated values, so 'allPossibleValues' has be empty now
      for (List<String> value : allPossibleValues) {
          Assert.fail("Oops! We have not generated this combanation: '" + value + "'!");
      }
    }

    private List<List<String>> generateAllPossibleValues4RepeatWithRange() {
        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < repeatNumber1; i++) {
            for (String val1 : setVarDomain1) {
                for (String val2 : setVarDomain2) {
                    List<String> record = new ArrayList<String>();
                    record.add(val1);
                    record.add(val2);
                    result.add(record);
                }
            }
        }
        return result;
    }
}
