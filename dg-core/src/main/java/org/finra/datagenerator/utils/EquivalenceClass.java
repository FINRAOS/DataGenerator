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


package org.finra.datagenerator.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;


/**
 * Marshall Peters
 * Date: 9/10/14
 * Updated: Mauricio Silva
 * Date: 3/24/15
 */
public class EquivalenceClass {

    /**
     * Default private Constructor
     */
    public EquivalenceClass() {
        random = new Random(currentTimeMillis());
        readSecuritiesList();
    }

    private Random random;

    /**
     * List of alpha numeric chars
     */
    private static final String[] WORDS = new String[]{"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz", "0123456789"};
    /**
     * List of male names
     */
    private static final String[] MALE_NAMES = new String[]{
            "Cade", "Melvin", "Mauricio", "Ignatius", "Ashton", "Nehru", "Garth", "Rigel", "Lester", "Dean", "Harper", "Boris",
            "Amos", "Holmes", "Amir", "Tad", "Trevor", "Carson", "Kamal", "Brennan", "Harding", "Alfonso", "Brendan", "Dante",
            "Rooney", "Tucker", "Vance", "Joshua", "August", "Dieter", "Hayes", "Kevin", "Lucas", "Amery", "Jakeem", "Hiram",
            "Garrison", "Neville", "Flynn", "Hyatt", "Simon", "Kirk", "Dexter", "Keith", "Avram", "Gage", "Jared", "Austin",
            "Arden", "Kennan", "Guy", "Acton", "Channing", "Ferris", "Galvin", "Acton", "Walter", "Valentine", "Eaton", "Marsden",
            "Dillon", "Flynn", "Raphael", "Oscar", "Fitzgerald", "Victor", "Dominic", "Lucius", "Bert", "Tucker", "Barry", "Gregory",
            "Bert", "Anthony", "Thor", "Troy", "Ryan", "Hamish", "Mason", "Mark", "Rahim", "Holmes", "Quentin", "Warren", "Arden",
            "Elijah", "Hayes", "Mohammad", "Lars", "Dane", "Tucker", "Uriah", "Joseph", "Ivan", "Ezra", "Martin", "Leo", "Lewis",
            "Daquan", "Lyle", "Judah"};
    /**
     * List of female names
     */
    private static final String[] FEMALE_NAMES = new String[]{
            "Giselle", "Chava", "Justine", "Jael", "Wynne", "Belle", "TaShya", "Judith", "Lysandra", "Nell", "Nadine", "Lila", "Ella",
            "Isadora", "Ariana", "Quemby", "Riley", "Rina", "Veda", "Kirestin", "Bianca", "Rana", "Mia", "Dara", "Ivy", "Rina", "Sade",
            "Dominique", "Clio", "Guinevere", "Clare", "Laurel", "Demetria", "Erin", "Stacey", "Mary", "Justine", "Glenna", "Roanna",
            "Grace", "Zenia", "Kitra", "Kylee", "Zoe", "Odessa", "Beatrice", "Robin", "Kellie", "Sierra", "Ivana", "Ignacia", "Meredith",
            "Kyra", "Bryar", "Jolie", "Josephine", "Quail", "Sonia", "Penelope", "Macey", "Desirae", "Helen", "Joy", "Ashely", "Wilma", "Rae",
            "Nyssa", "Mari", "Harriet", "Belle", "Eliana", "Galena", "Rama", "Nelle", "Jillian", "Hanae", "Ima", "Oprah", "Portia",
            "Jessamine", "Elizabeth", "Cassidy", "Karyn", "Idola", "Dacey", "Abra", "Caryn", "Kaitlin", "Pandora", "Angela", "Lana",
            "Melanie", "Uma", "Joan", "Flavia", "Colette", "Fleur", "Phyllis", "Venus", "Nerea"};
    /**
     * List of female names
     */
    private static final String[] LAST_NAMES = new String[]{
            "Horne", "Burgess", "Kirby", "Barrett", "Giles", "Harmon", "Case", "Rosales", "Dillard", "Kemp", "Santana", "Williams",
            "Valenzuela", "Kirby", "Swanson", "Howe", "Bright", "Webb", "Stokes", "Montgomery", "York", "Brewer", "Gallagher", "Jarvis",
            "Hurst", "Baker", "Dunlap", "Gibson", "Leonard", "Bruce", "Stephenson", "Levine", "Rivera", "Maynard", "Cherry", "Beasley",
            "Horne", "Gentry", "Ratliff", "Franks", "Santiago", "Wolf", "Young", "Gillespie", "Mcleod", "Ray", "Greene", "David", "Pickett",
            "Vance", "Jackson", "Rush", "Slater", "Shaffer", "Washington", "Herrera", "Rose", "Perry", "Burke", "Cash", "Barrera", "Carrillo",
            "Blake", "Mckee", "Rogers", "Parks", "Noble", "Hodges", "Sanford", "Santiago", "Mcclure", "Blake", "Bradley", "Bright", "Hobbs",
            "Swanson", "Erickson", "Foster", "Medina", "Shaffer", "Clay", "Nguyen", "Duncan", "Walls", "Manning", "Hickman", "Mcclain",
            "Hanson", "Graham", "Hudson", "Bryant", "Mcfarland", "Weaver", "Sargent", "Buck", "Scott", "Moore", "Oneal", "Carver", "Sims"};
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
     * Read securities from both files
     */
    private void readSecuritiesList() {
        readSecuritiesListDo("nasdaqlisted.txt", SYMBOLS_NASDAQ, SECURITY_NAMES_NASDAQ);
        readSecuritiesListDo("otherlisted.txt", SYMBOLS_NOT_NASDAQ, SECURITY_NAMES_NOT_NASDAQ);
    }

    /**
     * Read securities from file and stores them into an array
     */
    private void readSecuritiesListDo(String fileName, String[] var1, String[] var2) {
        InputStream fileData = EquivalenceClass.class.getClassLoader().getResourceAsStream(fileName);
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
            fileData.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * alpha
     *
     * @param len length of alpha values
     * @return response
     */
    public String alpha(int len) {
        StringBuilder response = new StringBuilder();
        while (len > 0) {
            int word = random.nextInt(WORDS.length);
            int letter = random.nextInt(WORDS[word].length());
            response.append(WORDS[word].charAt(letter));
            len--;
        }
        return response.toString();
    }

    /**
     * alpha with Spaces
     *
     * @param len length of alpha values
     * @return response
     */
    public String alphaWithSpaces(int len) {
        StringBuilder response = new StringBuilder();
        int nextSpacePos = len - random.nextInt(9);

        while (len > 0) {
            if (len != nextSpacePos) {
                int word = random.nextInt(WORDS.length);
                int letter = random.nextInt(WORDS[word].length());
                response.append(WORDS[word].charAt(letter));
            } else {
                response.append(" ");
                nextSpacePos = len - random.nextInt(9);
            }
            len--;
        }
        return response.toString();
    }

    /**
     * Random Date
     *
     * @return response
     */
    public String date() {

        String temp = generateFromRegex("^(0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])[/](19|20)\\d\\d$.");
        String[] date = temp.split("/|\\.|-");
        int monthValue = Integer.valueOf(date[0]);
        int dayValue = Integer.valueOf(date[1]);
        int year = Integer.valueOf(date[2]);

        if (dayValue == 31 && (monthValue == 4 || monthValue == 6 || monthValue == 0 || monthValue == 11)) {
            dayValue = random.nextInt(30) + 1; // 31st of a month with 30 days
        }
        if (dayValue >= 29 && monthValue == 2 && !(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) {
            dayValue = random.nextInt(28) + 1; //  February 29th outside a leap year
        }

        return twoDigitFormat(monthValue) + "/" + twoDigitFormat(dayValue) + "/" + year;
    }

    /**
     * Add a '0' if number is less than 10
     *
     * @param value to be adjusted with 2 digits
     * @return new Digit
     */
    private String twoDigitFormat(int value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * random Email
     *
     * @return response
     */
    public String email() {
        return generateFromRegex("^[a-z0-9._]{2,10}@[a-z]{2,6}+\\.(com|net|org|edu)$");
    }

    /**
     * number
     *
     * @param expr expr
     * @return response
     */
    public String number(String expr) {
        StringBuilder b = new StringBuilder();
        String[] lengths = expr.split(",");
        int whole = Integer.valueOf(lengths[0]);
        int decimal = 0;

        if (lengths.length == 2) {
            decimal = Integer.valueOf(lengths[1]);
        }

        if (whole == 0 && decimal > 0) {
            b.append("0.");
            digitSequence(b, decimal);
        } else if (whole > 0) {
            b.append(random.nextInt(9) + 1);
            digitSequence(b, whole - 1);
            if (decimal > 0) {
                b.append(".");
                digitSequence(b, decimal);
            }
        }

        return b.toString();
    }

    /**
     * Digit Sequence
     *
     * @param b              appends digits
     * @param numberOfDigits amount of numbers
     * @return response
     */
    public String digitSequence(StringBuilder b, int numberOfDigits) {
        for (int i = 0; i < numberOfDigits; i++) {
            b.append(random.nextInt(10));
        }
        return b.toString();
    }

    /**
     * Random ssn
     *
     * @return response
     */
    public String ssn() {
        //See more details here - http://en.wikipedia.org/wiki/Social_Security_number#Valid_SSNs
        return generateFromRegex("^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))"
                + "-((?!00)\\d{2})-((?!0000)\\d{4})$");

    }

    /**
     * Random phone USA
     *
     * @return response
     */
    public String phoneDomesticUSA() {
        //See more details here - http://en.wikipedia.org/wiki/North_American_Numbering_Plan
        return generateFromRegex("^([2-9]\\d{2})( )([2-9]\\d{2})( )(\\d{4})$");
    }

    /**
     * Random Zip
     *
     * @return response
     */
    public String zip() {
        return generateFromRegex("^((\\d{5})([- ]\\d{4})?)$");
    }

    /**
     * Random Phone Domestic USA with Ext
     *
     * @return response
     */
    public String phoneDomesticUSAWithExt() {
        //See more details here - http://en.wikipedia.org/wiki/North_American_Numbering_Plan
        return generateFromRegex("^([2-9]\\d{2})( )([2-9]\\d{2})( )(\\d{4})( ext )(\\d{3})$");
    }

    /**
     * Random Currency
     *
     * @return response
     */
    public String currency() {
        return CURRENCY_CODES[random.nextInt(CURRENCY_CODES.length)];
    }

    /**
     * Random State
     *
     * @return response
     */
    public String state() {
        return stateLong();
    }

    /**
     * Random State long abbreviation
     *
     * @return response
     */
    public String stateLong() {
        return STATE_LONG[random.nextInt(STATE_LONG.length)];
    }

    /**
     * Random State Short abbreviation
     *
     * @return response
     */
    public String stateShort() {
        return STATES_SHORT[random.nextInt(STATES_SHORT.length)];
    }

    /**
     * Random country
     *
     * @return response
     */
    public String country() {
        return countryLong();
    }

    /**
     * Random country
     *
     * @return response
     */
    public String countryLong() {
        return COUNTRIES[random.nextInt(COUNTRIES.length)];
    }

    /**
     * Random symbolNASDAQ
     *
     * @return response
     */
    public String symbolNASDAQ() {
        return SYMBOLS_NASDAQ[random.nextInt(SYMBOLS_NASDAQ.length)];
    }

    /**
     * Random symbolNotNASDAQ
     *
     * @return response
     */
    public String symbolNotNASDAQ() {
        return SYMBOLS_NOT_NASDAQ[random.nextInt(SYMBOLS_NASDAQ.length)];
    }

    /**
     * Random securityNameNASDAQ
     *
     * @return response
     */
    public String securityNameNASDAQ() {
        return SECURITY_NAMES_NASDAQ[random.nextInt(SECURITY_NAMES_NASDAQ.length)];
    }

    /**
     * Random securityNameNotNASDAQ
     *
     * @return response
     */
    public String securityNameNotNASDAQ() {
        return SECURITY_NAMES_NOT_NASDAQ[random.nextInt(SECURITY_NAMES_NOT_NASDAQ.length)];
    }

    /**
     * Random name
     *
     * @param expr (female, male, any)
     * @return response
     */
    public String name(String expr) {
        switch (expr) {
            case "female":
                return FEMALE_NAMES[random.nextInt(FEMALE_NAMES.length)];
            case "male":
                return MALE_NAMES[random.nextInt(MALE_NAMES.length)];
            case "any":
                if (random.nextInt(2) == 1) {
                    return name("male");
                } else {
                    return name("female");
                }

            default:
                return name("any");

        }
    }

    /**
     * Random last Name
     *
     * @return response
     */
    public String lastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    /**
     * @param expr (female, male, any)
     * @return response  first And Last Name
     */
    public String firstAndLastName(String expr) {
        return name(expr) + " " + lastName();
    }

    /**
     * @param expr (female, male, any)
     * @return response  first, Middle. And Last Name
     */
    public String fullName(String expr) {
        int letter = random.nextInt(WORDS[0].length());
        return name(expr) + " " + WORDS[0].charAt(letter) + ". " + lastName();
    }

    /**
     * @param regex regular expresion
     * @return response  from RegEx
     */
    public String generateFromRegex(String regex) {
        StringBuilder response = new StringBuilder();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher("");

        int[] cypher = new int[95];
        boolean done = false;

        //start from an empty string and grow a solution
        while (!done) {
            //make a cypher to jumble the order letters are tried
            for (int i = 0; i < cypher.length; i++) {
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
                response.append((char) n);

                String result = response.toString();
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
                    response.deleteCharAt(response.length() - 1);
                    //no more possible characters to try and expand with - stop
                    if (i == 94) {
                        done = true;
                    }
                }
            }
        }

        return response.toString();
    }
}
