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

import java.util.Map;

/**
 * Consumer that repeat whole set necessary number of times
 */
public class RepeatingDataConsumer extends DataConsumer {
    private int repeatNumber = 1;

    public int getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    /**
     * Overridden 'consume' method. Corresponding parent method will be called necessary number of times
     * 
     * @param initialVars - a map containing the initial variables assignments
     * @return the number of lines written
     */
    public int consume(Map<String, String> initialVars) {
        int result = 0;

        for (int i = 0; i < repeatNumber; i++) {
            result += super.consume(initialVars);
        }

        return result;
    }

}
