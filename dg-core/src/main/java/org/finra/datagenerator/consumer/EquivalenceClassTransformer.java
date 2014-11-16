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
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileNotFoundException;

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
    private final String[] states = {
        "Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","District Of Columbia",
                "Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine",
                "Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada",
                "New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma",
                "Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah",
                "Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"
    };
    private final String[] countries = {
            "Afghanistan","Albania","Algeria","Andorra","Angola","Antigua & Deps","Argentina","Armenia","Australia",
            "Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin",
            "Bhutan","Bolivia","Bosnia Herzegovina","Botswana","Brazil","Brunei","Bulgaria","Burkina","Burundi",
            "Cambodia","Cameroon","Canada","Cape Verde","Central African Rep","Chad","Chile","China","Colombia",
            "Comoros","Congo","Congo {Democratic Rep}","Costa Rica","Croatia","Cuba","Cyprus","Czech Republic","Denmark",
            "Djibouti","Dominica","Dominican Republic","East Timor","Ecuador","Egypt","El Salvador","Equatorial Guinea",
            "Eritrea","Estonia","Ethiopia","Fiji","Finland","France","Gabon","Gambia","Georgia","Germany","Ghana",
            "Greece","Grenada","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Honduras","Hungary","Iceland",
            "India","Indonesia","Iran","Iraq","Ireland {Republic}","Israel","Italy","Ivory Coast","Jamaica","Japan",
            "Jordan","Kazakhstan","Kenya","Kiribati","Korea North","Korea South","Kosovo","Kuwait","Kyrgyzstan","Laos",
            "Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macedonia",
            "Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius",
            "Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco","Mozambique","Myanmar, {Burma}",
            "Namibia","Nauru","Nepal","Netherlands","New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan",
            "Palau","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Romania",
            "Russian Federation","Rwanda","St Kitts & Nevis","St Lucia","Saint Vincent & the Grenadines","Samoa","San Marino",
            "Sao Tome & Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia",
            "Slovenia","Solomon Islands","Somalia","South Africa","South Sudan","Spain","Sri Lanka","Sudan","Suriname",
            "Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Togo","Tonga",
            "Trinidad & Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates",
            "United Kingdom","United States","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam",
            "Yemen","Zambia","Zimbabwe",
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

    private void number(StringBuilder b, int wholeDigits, int fractions) {
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
    }

    private void ssn(StringBuilder b) {
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
        }
    }
    private void marketSymbols(StringBuilder b){
        int rand = random.nextInt(5239);
        int count = 0;
        String symbol = "";
        EquivalenceClassTransformer.class.getResource("marketSymbols.txt");
        try {
            Scanner line = new Scanner(new FileReader("symbols.txt"));
            while (count != rand) {
                symbol=line.nextLine();
                count++;
            }
            b.append(symbol);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

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
                    case "number":
                        String[] lengths = expr.split(",");

                        int whole = Integer.valueOf(lengths[0]);
                        int decimal = 0;
                        if (lengths.length == 2) {
                            decimal = Integer.valueOf(lengths[1]);
                        }

                        number(b, whole, decimal);

                        break;
                    case "ssn":
                        ssn(b);
                        break;
                    case "currency":
                        b.append(currencyCodes[random.nextInt(currencyCodes.length)]);
                        break;
                    case "marketSymbol":
                        marketSymbols(b);
                        break;
                    case "country":
                        b.append(countries[random.nextInt(countries.length)]);
                        break;
                    case "state":
                        b.append(states[random.nextInt(states.length)]);
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
