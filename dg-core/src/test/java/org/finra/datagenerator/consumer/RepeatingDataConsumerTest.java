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

package org.finra.datagenerator.consumer;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Marshall Peters
 * Date: 12/10/14
 */
public class RepeatingDataConsumerTest {

    /**
     * The default repeat number is 1
     */
    @Test
    public void defaultRepeatNumberTest() {
        RepeatingDataConsumer repeatingDataConsumer = new RepeatingDataConsumer();
        Assert.assertEquals(1, repeatingDataConsumer.getRepeatNumber());
    }

    /**
     * Access methods for repeat number
     */
    @Test
    public void getSetRepeatNumberTest() {
        RepeatingDataConsumer repeatingDataConsumer = new RepeatingDataConsumer();
        repeatingDataConsumer.setRepeatNumber(3);
        Assert.assertEquals(3, repeatingDataConsumer.getRepeatNumber());
        repeatingDataConsumer.setRepeatNumber(6);
        Assert.assertEquals(6, repeatingDataConsumer.getRepeatNumber());
    }

    /**
     * consume returns repeat number
     */
    @Test
    public void returnsRepeatNumberTest() {
        RepeatingDataConsumer repeatingDataConsumer = new RepeatingDataConsumer();
        repeatingDataConsumer.setRepeatNumber(29);
        Assert.assertEquals(29, repeatingDataConsumer.consume(new HashMap<String, String>()));
    }

    /**
     * Should call the super consume repeat number of times
     */
    @Test
    public void callsSuperConsumeCorrectNumberOfTimesTest() {
        class CallCountTransformer implements DataTransformer {
            private final Map<String, String> inputDataMap;
            private int callCount;

            CallCountTransformer(final Map<String, String> inputDataMap) {
                this.inputDataMap = inputDataMap;
            }

            public int getCallCount() {
                return callCount;
            }

            public void transform(DataPipe cr) {
                Assert.assertEquals(inputDataMap, cr.getDataMap());
                callCount++;
            }
        }

        HashMap<String, String> mapToConsume = new HashMap<String, String>();
        mapToConsume.put("var_out_test", "LoremIpsum");
        CallCountTransformer callCountTransformer = new CallCountTransformer(mapToConsume);

        RepeatingDataConsumer repeatingDataConsumer = new RepeatingDataConsumer();
        repeatingDataConsumer.addDataTransformer(callCountTransformer);
        repeatingDataConsumer.setRepeatNumber(29);
        repeatingDataConsumer.consume(mapToConsume);

        Assert.assertEquals(29, callCountTransformer.getCallCount());
    }
}
