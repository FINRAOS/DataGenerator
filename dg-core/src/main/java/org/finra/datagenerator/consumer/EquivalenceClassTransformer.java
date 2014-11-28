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
    private final String[] words = new String[]{"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz", "0123456789"};
    private final String[] currencyCodes = {
            "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD",
            "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF",
            "BMD", "BND", "BOB", "BOV", "BRL", "BSD", "BTN", "BWP",
            "BYR", "BZD", "CAD", "CDF", "CHE", "CHF", "CHW", "CLF",
            "CLP", "CNY", "COP", "COU", "CRC", "CUC", "CUP", "CVE",
            "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB",
            "EUR", "FJD", "FKP", "GBP", "GEL", "GHS", "GIP", "GMD",
            "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF",
            "IDR", "ILS", "INR", "IQD", "IRR", "ISK", "JMD", "JOD",
            "JPY", "KES", "KGS", "KHR", "KMF", "KPW", "KRW", "KWD",
            "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LTL",
            "LVL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT",
            "MOP", "MRO", "MUR", "MVR", "MWK", "MXN", "MXV", "MYR",
            "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR",
            "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR",
            "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG",
            "SEK", "SGD", "SHP", "SLL", "SOS", "SRD", "SSP", "STD",
            "SVC", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP",
            "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "USN",
            "USS", "UYI", "UYU", "UZS", "VEF", "VND", "VUV", "WST",
            "XAF", "XAG", "XAU", "XBA", "XBB", "XBC", "XBD", "XCD",
            "XDR", "XFU", "XOF", "XPD", "XPF", "XPT", "XSU", "XTS",
            "XUA", "XXX", "YER", "ZAR", "ZMK", "ZWL"
    };

    /**
     * Constructor
     */
    public EquivalenceClassTransformer() {
        random = new Random(System.currentTimeMillis());
    }

    private void alpha(StringBuilder b, int len) {
        while (len > 0) {
            int word = random.nextInt(words.length);
            int letter = random.nextInt(words[word].length());
            b.append(words[word].charAt(letter));
            len--;
        }
    }

    private void alphaWithSpaces(StringBuilder b, int len) {
        int nextSpacePos = len - random.nextInt(9);

        while (len > 0) {
            if (len != nextSpacePos) {
                int word = random.nextInt(words.length);
                int letter = random.nextInt(words[word].length());
                b.append(words[word].charAt(letter));
            } else {
                b.append(" ");
                nextSpacePos = len - random.nextInt(9);
            }
            len--;
        }
    }

    private void number(String type, StringBuilder b, int wholeDigits, int fractions) {
        int requestedwholeDigits = wholeDigits;
        while (wholeDigits > 0) {
            b.append(random.nextInt(10));
            wholeDigits--;
        }

        if (fractions > 0) {
            b.append('.');
            while (fractions > 0) {
                b.append(random.nextInt(10));
                fractions--;
            }
        }
        // If type is a number we got to make sure the first digit of the 'wholeDigits' component cannot be ZERO
        // unless the size of the 'wholeDigits' is 1
        // Bug fix for Github issue #165
        if (type.equals("number") && requestedwholeDigits > 1 && (b.charAt(0)) == '0') {
            // Gets a random int from 0-8 and adds 1 to it. Which means we get numbers from 1-9.
            b.replace(0, 1, Integer.toString((random.nextInt(9) + 1)));
        }
    }

    private void ssn(StringBuilder b) {
        //Replacing the trivial SSN generator to be a more robust SSN value generator according SSN rules
        //(REFER: http://en.wikipedia.org/wiki/Social_Security_number#Valid_SSNs)
        /*
        for (int i = 0; i != 3; i++) {
            b.append(random.nextInt(10));
        }
        b.append("-");
        for (int i = 0; i != 2; i++) {
            b.append(random.nextInt(10));
        }
        b.append("-");
        for (int i = 0; i != 4; i++) {
            b.append(random.nextInt(10));
        }*/

        //Replacing the trivial SSN generator to be a more robust SSN value generator according SSN rules
        //(REFER: http://en.wikipedia.org/wiki/Social_Security_number#Valid_SSNs)
        generateFromRegex(b, "^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))"
                        + "-((?!00)\\d{2})-((?!0000)\\d{4})$");

    }

    private void generateFromRegex(StringBuilder r, String regex) {
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

        r.append(b.toString());
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

                StringBuilder b = new StringBuilder();

                switch (macro) {
                    case "regex":
                        generateFromRegex(b, expr);
                        break;
                    case "alpha":
                        alpha(b, Integer.valueOf(expr));
                        break;
                    case "alphaWithSpaces":
                        alphaWithSpaces(b, Integer.valueOf(expr));
                        break;

                    // If type is a number we got to make sure the first digit of the 'wholeDigits' component cannot
                    // be ZERO unless the size of the 'wholeDigits' is 1
                    // Bug fix for Github issue #165
                    case "number":
                    // digits can have a number with starting with ZERO while number cannot
                    case "digits":
                        String[] lengths = expr.split(",");

                        int whole = Integer.valueOf(lengths[0]);
                        int decimal = 0;
                        if (lengths.length == 2) {
                            decimal = Integer.valueOf(lengths[1]);
                        }

                        number(macro, b, whole, decimal);

                        break;

                    case "ssn":
                        ssn(b);
                        break;
                    case "currency":
                        b.append(currencyCodes[random.nextInt(currencyCodes.length)]);
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
