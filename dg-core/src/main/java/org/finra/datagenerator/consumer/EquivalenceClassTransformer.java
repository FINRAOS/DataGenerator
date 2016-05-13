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

import org.finra.datagenerator.engine.scxml.tags.boundary.Holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs transformations for common equivalence classes/data types
 */
public class EquivalenceClassTransformer implements DataTransformer {

    private Random random;

    /**
     * List of alpha numeric chars
     */
    private static final String[] WORDS = new String[]{"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz", "0123456789"};

    /**
     * List of currency codes
     */
    public static final String[] CURRENCY_CODES = {
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
     * List of US states. Long version
     */
    public static final String[] STATE_LONG = {
        "Alabama", "Alaska", "American Samoa", "Arizona", "Arkansas",
        "California", "Colorado", "Connecticut", "Delaware", "Dist. of Columbia",
        "Florida", "Georgia", "Guam", "Hawaii", "Idaho", "Illinois", "Indiana",
        "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Marshall Islands",
        "Massachusetts", "Michigan", "Micronesia", "Minnesota", "Mississippi",
        "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
        "New Mexico", "New York", "North Carolina", "North Dakota", "Northern Marianas",
        "Ohio", "Oklahoma", "Oregon", "Palau", "Pennsylvania", "Puerto Rico",
        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas",
        "Utah", "Vermont", "Virginia", "Virgin Islands", "Washington", "West Virginia",
        "Wisconsin", "Wyoming"
    };

    /**
     * List of US states. Short 2 chars version
     */
    public static final String[] STATES_SHORT = {
        "AL", "AK", "AS", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "GU", "HI", "ID",
        "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MH", "MA", "MI", "FM", "MN", "MS", "MO",
        "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "MP", "OH", "OK", "OR", "PW", "PA",
        "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "VI", "WA", "WV", "WI", "WY"
    };

    /**
     * List of Holidays
     */
    public static final Holiday[] HOLIDAYS = {
        new Holiday("New Years Day", 1, 1),
        new Holiday("Martin Luther King Day", 1, 2, 3),
        new Holiday("Presidents Day", 2, 2, 3),
        new Holiday("Memorial Day", 5, 2, 5),
        new Holiday("Independence Day", 7, 4),
        new Holiday("Labor Day", 9, 2, 1),
        new Holiday("Thanksgiving", 11, 5, 4),
        new Holiday("Christmas", 12, 25)
    };

    /**
     * List of countries. Long version
     */
    public static final String[] COUNTRIES = {
        "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua & Barbuda", "Argentina",
        "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
        "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia & Herzegovina", "Botswana",
        "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada",
        "Cape Verde", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo",
        "Congo Democratic Republic", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic",
        "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "East Timor", "Egypt", "El Salvador",
        "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia",
        "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
        "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel",
        "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea North", "Korea South",
        "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",
        "Lithuania", "Luxembourg", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
        "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
        "Montenegro", "Morocco", "Mozambique", "Myanmar (Burma)", "Namibia", "Nauru", "Nepal", "The Netherlands",
        "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palau", "Palestinian State",
        "Panama", "Papua New Guinea", "Paraguay", "Peru", "The Philippines", "Poland", "Portugal", "Qatar", "Romania",
        "Russia", "Rwanda", "St. Kitts & Nevis", "St. Lucia", "St. Vincent & The Grenadines", "Samoa", "San Marino",
        "Sao Tome & Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
        "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Sudan", "Spain", "Sri Lanka",
        "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania",
        "Thailand", "Togo", "Tonga", "Trinidad & Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda",
        "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "Uruguay", "Uzbekistan",
        "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"
    };

    /**
     * Number of NASDAQ securities ( ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt)
     */
    public static final int COUNT_NASDAQ_SECURITIES = 2975;

    /**
     * NASDAQ symbols list ( ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt)
     */
    public static final String[] SYMBOLS_NASDAQ = new String[COUNT_NASDAQ_SECURITIES];

    /**
     * NASDAQ names list (from ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt)
     */
    public static final String[] SECURITY_NAMES_NASDAQ = new String[COUNT_NASDAQ_SECURITIES];

    /**
     * Number of not NASDAQ securities ( ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt)
     */
    public static final int COUNT_NOT_NASDAQ_SECURITIES = 5207;

    /**
     * Not NASDAQ symbols list (from ftp://ftp.nasdaqtrader.com/symboldirectory/otherlisted.txt)
     */
    public static final String[] SYMBOLS_NOT_NASDAQ = new String[COUNT_NOT_NASDAQ_SECURITIES];

    /**
     * Not NASDAQ names list (from ftp://ftp.nasdaqtrader.com/symboldirectory/otherlisted.txt)
     */
    public static final String[] SECURITY_NAMES_NOT_NASDAQ = new String[COUNT_NOT_NASDAQ_SECURITIES];


    /**
     * Constructor
     */
    public EquivalenceClassTransformer() {
        random = new Random(System.currentTimeMillis());
        readSecuritiesList();
    }

    private void readSecuritiesList() {
        readSecuritiesListDo("nasdaqlisted.txt", SYMBOLS_NASDAQ, SECURITY_NAMES_NASDAQ);
        readSecuritiesListDo("otherlisted.txt", SYMBOLS_NOT_NASDAQ, SECURITY_NAMES_NOT_NASDAQ);
    }

    private void readSecuritiesListDo(String fileName, String[] var1, String[] var2) {
        InputStream fileData = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileData));
        String line;
        try {
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] lineSplitted = line.split("\\|");
                if (lineSplitted.length >= 2) {
                    var1[i] = lineSplitted[0];
                    var2[i] = lineSplitted[1];
                    i++;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void alpha(StringBuilder b, int len) {
        while (len > 0) {
            int word = random.nextInt(WORDS.length);
            int letter = random.nextInt(WORDS[word].length());
            b.append(WORDS[word].charAt(letter));
            len--;
        }
    }

    private void alphaWithSpaces(StringBuilder b, int len) {
        int nextSpacePos = len - random.nextInt(9);

        while (len > 0) {
            if (len != nextSpacePos) {
                int word = random.nextInt(WORDS.length);
                int letter = random.nextInt(WORDS[word].length());
                b.append(WORDS[word].charAt(letter));
            } else {
                b.append(" ");
                nextSpacePos = len - random.nextInt(9);
            }
            len--;
        }
    }

    private void digitSequence(StringBuilder b, int numberOfDigits) {
        for (int i = 0; i < numberOfDigits; i++) {
            b.append(random.nextInt(10));
        }
    }

    private void ssn(StringBuilder b) {
        //See more details here - http://en.wikipedia.org/wiki/Social_Security_number#Valid_SSNs
        generateFromRegex(b, "^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))"
            + "-((?!00)\\d{2})-((?!0000)\\d{4})$");
    }

    private void phoneDomesticUSA(StringBuilder b) {
        //See more details here - http://en.wikipedia.org/wiki/North_American_Numbering_Plan
        generateFromRegex(b, "^([2-9]\\d{2})( )([2-9]\\d{2})( )(\\d{4})$");
    }

    private void zip(StringBuilder b) {
        generateFromRegex(b, "^((\\d{5})([- ]\\d{4})?)$");
    }

    private void phoneDomesticUSAWithExt(StringBuilder b) {
        //See more details here - http://en.wikipedia.org/wiki/North_American_Numbering_Plan
        generateFromRegex(b, "^([2-9]\\d{2})( )([2-9]\\d{2})( )(\\d{4})( ext )(\\d{3})$");
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
    @Override
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
                        int precision = Integer.valueOf(lengths[0]);
                        int scale = 0;

                        if (lengths.length == 2) {
                            scale = Integer.valueOf(lengths[1]) < 0 ? 0 : Integer.valueOf(lengths[1]);
                        }

                        int wholeDigits = precision - scale < 0 ? 0 : precision - scale;

                        if (wholeDigits == 0 && scale > 0) {
                            b.append("0.");
                            digitSequence(b, scale);
                        } else if (wholeDigits > 0) {
                            b.append(random.nextInt(9) + 1);
                            digitSequence(b, wholeDigits - 1);
                            if (scale > 0) {
                                b.append(".");
                                digitSequence(b, scale);
                            }
                        }
                        break;

                    case "digits":
                        int length = Integer.valueOf(expr);
                        digitSequence(b, length);
                        break;

                    case "ssn":
                        ssn(b);
                        break;

                    case "zip":
                        zip(b);
                        break;

                    case "phoneDomesticUSA":
                        phoneDomesticUSA(b);
                        break;

                    case "phoneDomesticUSAWithExt":
                        phoneDomesticUSAWithExt(b);
                        break;

                    case "currency":
                        b.append(CURRENCY_CODES[random.nextInt(CURRENCY_CODES.length)]);
                        break;

                    case "state":
                    case "stateLong":
                        b.append(STATE_LONG[random.nextInt(STATE_LONG.length)]);
                        break;

                    case "stateShort":
                        b.append(STATES_SHORT[random.nextInt(STATES_SHORT.length)]);
                        break;

                    case "country":
                    case "countryLong":
                        b.append(COUNTRIES[random.nextInt(COUNTRIES.length)]);
                        break;

                    case "symbolNASDAQ":
                        b.append(SYMBOLS_NASDAQ[random.nextInt(SYMBOLS_NASDAQ.length)]);
                        break;

                    case "symbolNotNASDAQ":
                        b.append(SYMBOLS_NOT_NASDAQ[random.nextInt(SYMBOLS_NOT_NASDAQ.length)]);
                        break;

                    case "securityNameNASDAQ":
                        b.append(SECURITY_NAMES_NASDAQ[random.nextInt(SECURITY_NAMES_NASDAQ.length)]);
                        break;

                    case "securityNameNotNASDAQ":
                        b.append(SECURITY_NAMES_NOT_NASDAQ[random.nextInt(SECURITY_NAMES_NOT_NASDAQ.length)]);
                        break;

                    default:
                        b.append(value);
                        break;
                }
                entry.setValue(b.toString());

            } else if (value.startsWith("$")) {
                String variable, varValue;

                int param = value.indexOf("{");
                if (param != -1) {
                    variable = value.substring(param + 1, value.length() - 1);
                } else {
                    variable = value.substring(1);
                }

                if (map.containsKey(variable)) {
                    varValue = map.get(variable);
                } else {
                    varValue = null;
                }
                entry.setValue(varValue);
            }
        }
    }

}
