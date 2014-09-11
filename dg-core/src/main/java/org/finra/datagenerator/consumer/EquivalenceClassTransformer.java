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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Marshall Peters
 * Date: 9/10/14
 */
public class EquivalenceClassTransformer implements DataTransformer {

    private Random random;

    /**
     * Constructor
     */
    public EquivalenceClassTransformer() {
        random = new Random(System.currentTimeMillis());
    }

    private String generateFromRegex(String regex) {
        StringBuilder b = new StringBuilder();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher("");

        int[] cypher = new int[95];
        boolean done = false;

        //start from an empty string and grow a solution
        while (!done) {
            //make a cypher to jumble the order letters are tried
            for (int i = 0; i < 95; i++) {
                cypher[i] = i;
            }

            for (int i = 0; i < 95; i++) {
                int n = random.nextInt(95 - i) + i;

                int t = cypher[n];
                cypher[n] = cypher[i];
                cypher[i] = t;
            }

            //try and grow partial solution using an extra letter on the end
            for (int i = 0; i < 95; i++) {
                int n = cypher[i] + 32;
                b.append((char) n);

                String result = b.toString();
                m.reset(result);
                if (m.matches()) { //complete solution found
                    //don't try to expand to a larger solution
                    if (!random.nextBoolean()) {
                        done = true;
                    }

                    break;
                } else if (m.hitEnd()) { //prefix to a solution found, keep this letter
                    break;
                } else { //dead end found, try a new character at the end
                    b.deleteCharAt(b.length() - 1);

                    //no more possible characters to try and expand with - stop
                    if (i == 94) {
                        done = true;
                    }
                }
            }
        }

        return b.toString();
    }

    /**
     * Performs transformations for common equivalence classes/data types
     *
     * @param cr a reference to DataPipe from which to read the current map
     */
    public void transform(DataPipe cr) {
        Map<String, String> map = cr.getDataMap();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();

            if (value.startsWith("%")) {
                String macro;
                String expr;

                int param = value.indexOf("(");
                if (param != -1) {
                    macro = value.substring(1, param);
                    expr = value.substring(param + 1, value.length() - 1);
                } else {
                    macro = value.substring(1);
                    expr = "";
                }

                switch (macro) {
                    case "regex": entry.setValue(generateFromRegex(expr));
                    default: break;
                }
            }
        }
    }

}
