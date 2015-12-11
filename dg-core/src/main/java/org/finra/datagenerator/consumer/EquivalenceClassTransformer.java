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
        EquivalenceClass equivalenceClass = new EquivalenceClass();

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
                        b.append(equivalenceClass.generateFromRegex(expr));
                        break;

                    case "name":
                        b.append(equivalenceClass.name(expr));
                        break;

                    case "lastName":
                        b.append(equivalenceClass.lastName());
                        break;

                    case "firstAndLastName":
                        b.append(equivalenceClass.firstAndLastName(expr));
                        break;

                    case "fullName":
                        b.append(equivalenceClass.fullName(expr));
                        break;

                    case "email":
                        b.append(equivalenceClass.email());
                        break;

                    case "alpha":
                        b.append(equivalenceClass.alpha(Integer.valueOf(expr)));
                        break;

                    case "alphaWithSpaces":
                        b.append(equivalenceClass.alphaWithSpaces(Integer.valueOf(expr)));
                        break;

                    case "number":
                        b.append(equivalenceClass.number(expr));
                        break;

                    case "digits":
                        int length = Integer.valueOf(expr);
                        equivalenceClass.digitSequence(b, length);
                        break;

                    case "date":
                        b.append(equivalenceClass.date());
                        break;

                    case "ssn":
                        b.append(equivalenceClass.ssn());
                        break;

                    case "zip":
                        b.append(equivalenceClass.zip());
                        break;

                    case "phoneDomesticUSA":
                        b.append(equivalenceClass.phoneDomesticUSA());
                        break;

                    case "phoneDomesticUSAWithExt":
                        b.append(equivalenceClass.phoneDomesticUSAWithExt());
                        break;

                    case "currency":
                        b.append(equivalenceClass.currency());
                        break;

                    case "state":
                    case "stateLong":
                        b.append(equivalenceClass.stateLong());
                        break;

                    case "stateShort":
                        b.append(equivalenceClass.stateShort());
                        break;

                    case "country":
                    case "countryLong":
                        b.append(equivalenceClass.countryLong());
                        break;

                    case "symbolNASDAQ":
                        b.append(equivalenceClass.symbolNASDAQ());
                        break;

                    case "symbolNotNASDAQ":
                        b.append(equivalenceClass.symbolNotNASDAQ());
                        break;

                    case "securityNameNASDAQ":
                        b.append(equivalenceClass.securityNameNASDAQ());
                        break;

                    case "securityNameNotNASDAQ":
                        b.append(equivalenceClass.securityNameNotNASDAQ());
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
