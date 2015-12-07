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

import org.finra.datagenerator.utils.EquivalenceClass;

import java.util.Map;


/**
 * Marshall Peters
 * Date: 9/10/14
 * Updated: Mauricio Silva
 * Date: 3/24/15
 */
public class EquivalenceClassTransformer implements DataTransformer {

    /**
     * Performs transformations for common equivalence classes/data types
     *
     * @param cr a reference to DataPipe from which to read the current map
     */
    public void transform(DataFormatter cr) {
        Map<String, String> map = cr.getDataMap();
        EquivalenceClass.initializeEquivalenceClass();

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

                StringBuilder b = new StringBuilder();

                switch (macro) {
                    case "regex":
                        b.append(EquivalenceClass.generateFromRegex(expr));
                        break;

                    case "name":
                        b.append(EquivalenceClass.name(expr));
                        break;

                    case "lastName":
                        b.append(EquivalenceClass.lastName());
                        break;

                    case "firstAndLastName":
                        b.append(EquivalenceClass.firstAndLastName(expr));
                        break;

                    case "fullName":
                        b.append(EquivalenceClass.fullName(expr));
                        break;

                    case "email":
                        b.append(EquivalenceClass.email());
                        break;

                    case "alpha":
                        b.append(EquivalenceClass.alpha(Integer.valueOf(expr)));
                        break;

                    case "alphaWithSpaces":
                        b.append(EquivalenceClass.alphaWithSpaces(Integer.valueOf(expr)));
                        break;

                    case "number":
                        b.append(EquivalenceClass.number(expr));
                        break;

                    case "digits":
                        int length = Integer.valueOf(expr);
                        EquivalenceClass.digitSequence(b, length);
                        break;

                    case "date":
                        b.append(EquivalenceClass.date());
                        break;

                    case "ssn":
                        b.append(EquivalenceClass.ssn());
                        break;

                    case "zip":
                        b.append(EquivalenceClass.zip());
                        break;

                    case "phoneDomesticUSA":
                        b.append(EquivalenceClass.phoneDomesticUSA());
                        break;

                    case "phoneDomesticUSAWithExt":
                        b.append(EquivalenceClass.phoneDomesticUSAWithExt());
                        break;

                    case "currency":
                        b.append(EquivalenceClass.currency());
                        break;

                    case "state":
                    case "stateLong":
                        b.append(EquivalenceClass.stateLong());
                        break;

                    case "stateShort":
                        b.append(EquivalenceClass.stateShort());
                        break;

                    case "country":
                    case "countryLong":
                        b.append(EquivalenceClass.countryLong());
                        break;

                    case "symbolNASDAQ":
                        b.append(EquivalenceClass.symbolNASDAQ());
                        break;

                    case "symbolNotNASDAQ":
                        b.append(EquivalenceClass.symbolNotNASDAQ());
                        break;

                    case "securityNameNASDAQ":
                        b.append(EquivalenceClass.securityNameNASDAQ());
                        break;

                    case "securityNameNotNASDAQ":
                        b.append(EquivalenceClass.securityNameNotNASDAQ());
                        break;

                    default:
                        b.append(value);
                        break;
                }
                entry.setValue(b.toString());
            }
        }
    }

}
