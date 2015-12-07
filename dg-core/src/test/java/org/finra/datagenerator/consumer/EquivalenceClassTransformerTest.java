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
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weerasin on 11/28/14.
 */
public class EquivalenceClassTransformerTest {

    /**
     * Tests regular expression generator by making a cusip
     */
    @Test
    public void regexCUSIPTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String testRegularExpression = "^\\d{5}(?:[-\\s]\\d{4})?$";
        pipeToTransform.getDataMap().put("TEST", "%regex(" + testRegularExpression + ")");
        eqTransformer.transform(pipeToTransform);
        Pattern pattern = Pattern.compile(testRegularExpression);
        Matcher didItMatch = pattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }

    /**
     * Tests regular expression generator by making a sequence of random digits
     */
    @Test
    public void regexNumbersTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String testRegularExpression = "\\d{4}/\\d{2}/\\d{2}-\\d{2}:\\d{2}";
        pipeToTransform.getDataMap().put("TEST", "%regex(" + testRegularExpression + ")");
        eqTransformer.transform(pipeToTransform);
        Pattern pattern = Pattern.compile(testRegularExpression);
        Matcher didItMatch = pattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }

    /**
     * Tests regular expression generator by making a date
     */
    @Test
    public void regexDateTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String testRegularExpression = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
        pipeToTransform.getDataMap().put("TEST", "%regex(" + testRegularExpression + ")");
        eqTransformer.transform(pipeToTransform);
        Pattern pattern = Pattern.compile(testRegularExpression);
        Matcher didItMatch = pattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }

    /**
     * Tests regular expression generator by making an email
     */
    @Test
    public void regexEmailTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String testRegularExpression = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
        pipeToTransform.getDataMap().put("TEST", "%regex(" + testRegularExpression + ")");
        eqTransformer.transform(pipeToTransform);
        Pattern pattern = Pattern.compile(testRegularExpression);
        Matcher didItMatch = pattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }


    /**
     * %alpha macro
     */
    @Test
    public void alphaTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "%alpha(1)");
        eqTransformer.transform(pipeToTransform);
        Pattern alphaPattern = Pattern.compile("^[a-z0-9A-Z]{1}$");
        Matcher didItMatch = alphaPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%alpha(100)");
        eqTransformer.transform(pipeToTransform);
        alphaPattern = Pattern.compile("^[a-z0-9A-Z]{100}$");
        didItMatch = alphaPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%alpha(0)");
        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("", pipeToTransform.getDataMap().get("TEST"));
    }

    /**
     * %alphaWithSpacesMAcro
     */
    @Test
    public void alphaWithSpacesTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "%alphaWithSpaces(1)");
        eqTransformer.transform(pipeToTransform);
        Pattern alphaWithSpacesPattern = Pattern.compile("^[a-z0-9A-Z\\s]{1}$");
        Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%alphaWithSpaces(100)");
        eqTransformer.transform(pipeToTransform);
        alphaWithSpacesPattern = Pattern.compile("^[a-z0-9A-Z\\s]{100}$");
        didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%alphaWithSpaces(0)");
        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("", pipeToTransform.getDataMap().get("TEST"));
    }

    /**
     * %number makes a valid positive decimal or integer
     */
    @Test
    public void numberTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "%number(5)");
        eqTransformer.transform(pipeToTransform);
        Pattern alphaWithSpacesPattern = Pattern.compile("^[1-9]{1}[\\d]{4}$");
        Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%number(10,10)");
        eqTransformer.transform(pipeToTransform);
        alphaWithSpacesPattern = Pattern.compile("^[1-9]{1}[\\d]{9}\\.[\\d]{10}$");
        didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%number(1,10)");
        eqTransformer.transform(pipeToTransform);
        alphaWithSpacesPattern = Pattern.compile("^[1-9]{1}.[\\d]{10}$");
        didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%number(0,10)");
        eqTransformer.transform(pipeToTransform);
        alphaWithSpacesPattern = Pattern.compile("^0\\.[\\d]{10}$");
        didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%number(0,0)");
        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("", pipeToTransform.getDataMap().get("TEST"));

        pipeToTransform.getDataMap().put("TEST", "%number(0)");
        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("", pipeToTransform.getDataMap().get("TEST"));
    }

    /**
     * %date produces a date with a partial validness check
     */
    @Test
    public void dateTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();
        pipeToTransform.getDataMap().put("TEST", "%date");
        String testRegularExpression = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
        eqTransformer.transform(pipeToTransform);
        Pattern pattern = Pattern.compile(testRegularExpression);
        Matcher didItMatch = pattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }
    /**
     * %digits macro gives a string of n digits
     */
    @Test
    public void digitsTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();


        pipeToTransform.getDataMap().put("TEST", "%digits(5)");
        eqTransformer.transform(pipeToTransform);
        Pattern alphaWithSpacesPattern = Pattern.compile("^[\\d]{5}$");
        Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%digits(10)");
        eqTransformer.transform(pipeToTransform);
        alphaWithSpacesPattern = Pattern.compile("^[\\d]{10}$");
        didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());

        pipeToTransform.getDataMap().put("TEST", "%digits(0)");
        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("", pipeToTransform.getDataMap().get("TEST"));
    }

    /**
     * %ssn produces a social security number with a partial validness check
     */
    @Test
    public void ssnTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST", "%ssn");
            eqTransformer.transform(pipeToTransform);
            Pattern alphaWithSpacesPattern = Pattern.compile("^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))"
                    + "-((?!00)\\d{2})-((?!0000)\\d{4})$");
            Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
            Assert.assertTrue(didItMatch.matches());
        }
    }

    /**
     * %zip produces a USA zip with a partial validness check
     */
    @Test
    public void zipTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_zip", "%zip");
            eqTransformer.transform(pipeToTransform);
            Pattern alphaWithSpacesPattern = Pattern.compile("^((\\d{5})([- ]\\d{4})?)$");
            Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST_zip"));
            Assert.assertTrue(didItMatch.matches());
        }
    }

    /**
     * %phoneDomesticUSA produces a USA domestic phone number without extension with a partial validness check
     */
    @Test
    public void phoneDomesticUSATest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_phoneDomesticUSA", "%phoneDomesticUSA");
            eqTransformer.transform(pipeToTransform);
            Pattern alphaWithSpacesPattern = Pattern.compile("^([2-9]\\d{2})(\\D*)([2-9]\\d{2})(\\D*)(\\d{4})$");
            Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST_phoneDomesticUSA"));
            Assert.assertTrue("Wrong USA domestic phone number without extension generation! Created '"
                    + pipeToTransform.getDataMap().get("TEST_phoneDomesticUSA") + "'...", didItMatch.matches());
        }
    }

    /**
     * %phoneDomesticUSAWithExt produces a USA domestic phone number with extension with a partial validness check
     */
    @Test
    public void phoneDomesticUSAWithExtTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_phoneDomesticUSAWithExt", "%phoneDomesticUSAWithExt");
            eqTransformer.transform(pipeToTransform);
            Pattern alphaWithSpacesPattern = Pattern.compile("^([2-9]\\d{2})(\\D*)([2-9]\\d{2})(\\D*)"
                    + "(\\d{4})((\\D{1})(ext|e|extension)?(\\D*)(\\d*))?$");
            Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST_phoneDomesticUSAWithExt"));
            Assert.assertTrue("Wrong USA domestic phone number with extension generation! Created '"
                    + pipeToTransform.getDataMap().get("TEST_phoneDomesticUSAWithExt") + "'...", didItMatch.matches());
        }
    }

    /**
     * %nameCombinationsTest produces a full name with a full validness check
     */
    @Test
    public void nameCombinationsTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] maleNames = new String[] {
                "Cade", "Melvin", "Ignatius", "Ashton", "Nehru", "Garth", "Rigel", "Lester",
                "Dean", "Harper", "Mauricio", "Boris", "Amos", "Holmes", "Amir", "Tad", "Trevor", "Carson", "Kamal", "Brennan", "Harding", "Alfonso",
                "Brendan", "Dante", "Rooney", "Tucker", "Vance", "Joshua", "August", "Dieter", "Hayes", "Kevin", "Lucas", "Amery", "Jakeem",
                "Hiram", "Garrison", "Neville", "Flynn", "Hyatt", "Simon", "Kirk", "Dexter", "Keith", "Avram", "Gage", "Jared", "Austin",
                "Arden", "Kennan", "Guy", "Acton", "Channing", "Ferris", "Galvin", "Acton", "Walter", "Valentine", "Eaton", "Marsden", "Dillon",
                "Flynn", "Raphael", "Oscar", "Fitzgerald", "Victor", "Dominic", "Lucius", "Bert", "Tucker", "Barry", "Gregory", "Bert", "Anthony",
                "Thor", "Troy", "Ryan", "Hamish", "Mason", "Mark", "Rahim", "Holmes", "Quentin", "Warren", "Arden", "Elijah", "Hayes", "Mohammad",
                "Lars", "Dane", "Tucker", "Uriah", "Joseph", "Ivan", "Ezra", "Martin", "Leo", "Lewis", "Daquan", "Lyle", "Judah"};

        String [] femaleNames = new String[] {
                "Giselle", "Chava", "Justine", "Jael", "Wynne", "Belle", "TaShya", "Judith", "Lysandra", "Nell", "Nadine", "Lila", "Ella",
                "Isadora", "Ariana", "Quemby", "Riley", "Rina", "Veda", "Kirestin", "Bianca", "Rana", "Mia", "Dara", "Ivy", "Rina", "Sade",
                "Dominique", "Clio", "Guinevere", "Clare", "Laurel", "Demetria", "Erin", "Stacey", "Mary", "Justine", "Glenna", "Roanna",
                "Grace", "Zenia", "Kitra", "Kylee", "Zoe", "Odessa", "Beatrice", "Robin", "Kellie", "Sierra", "Ivana", "Ignacia", "Meredith",
                "Kyra", "Bryar", "Jolie", "Josephine", "Quail", "Sonia", "Penelope", "Macey", "Desirae", "Helen", "Joy", "Ashely", "Wilma", "Rae",
                "Nyssa", "Mari", "Harriet", "Belle", "Eliana", "Galena", "Rama", "Nelle", "Jillian", "Hanae", "Ima", "Oprah", "Portia",
                "Jessamine", "Elizabeth", "Cassidy", "Karyn", "Idola", "Dacey", "Abra", "Caryn", "Kaitlin", "Pandora", "Angela", "Lana",
                "Melanie", "Uma", "Joan", "Flavia", "Colette", "Fleur", "Phyllis", "Venus", "Nerea"};

        String[] lastNames = new String[] {
                "Horne", "Burgess", "Kirby", "Barrett", "Giles", "Harmon", "Case", "Rosales", "Dillard", "Kemp", "Santana", "Williams",
                "Valenzuela", "Kirby", "Swanson", "Howe", "Bright", "Webb", "Stokes", "Montgomery", "York", "Brewer", "Gallagher", "Jarvis",
                "Hurst", "Baker", "Dunlap", "Gibson", "Leonard", "Bruce", "Stephenson", "Levine", "Rivera", "Maynard", "Cherry", "Beasley",
                "Horne", "Gentry", "Ratliff", "Franks", "Santiago", "Wolf", "Young", "Gillespie", "Mcleod", "Ray", "Greene", "David", "Pickett",
                "Vance", "Jackson", "Rush", "Slater", "Shaffer", "Washington", "Herrera", "Rose", "Perry", "Burke", "Cash", "Barrera", "Carrillo",
                "Blake", "Mckee", "Rogers", "Parks", "Noble", "Hodges", "Sanford", "Santiago", "Mcclure", "Blake", "Bradley", "Bright", "Hobbs",
                "Swanson", "Erickson", "Foster", "Medina", "Shaffer", "Clay", "Nguyen", "Duncan", "Walls", "Manning", "Hickman", "Mcclain",
                "Hanson", "Graham", "Hudson", "Bryant", "Mcfarland", "Weaver", "Sargent", "Buck", "Scott", "Moore", "Oneal", "Carver", "Sims"};

        HashSet<String> maleNamesLookUp = new HashSet<>(Arrays.asList(maleNames));
        HashSet<String> femaleNamesLookUp = new HashSet<>(Arrays.asList(femaleNames));
        HashSet<String> lastNamesLookUp = new HashSet<>(Arrays.asList(lastNames));

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_MALE", "%name(male)");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue(maleNamesLookUp.contains(pipeToTransform.getDataMap().get("TEST_MALE")));

            pipeToTransform.getDataMap().put("TEST_FEMALE", "%name(female)");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue(femaleNamesLookUp.contains(pipeToTransform.getDataMap().get("TEST_FEMALE")));

            pipeToTransform.getDataMap().put("TEST_ANY", "%name(any)");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue(maleNamesLookUp.contains(pipeToTransform.getDataMap().get("TEST_ANY"))
                    || femaleNamesLookUp.contains(pipeToTransform.getDataMap().get("TEST_ANY")));
        }

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_firstAndLastName_FEMALE", "%firstAndLastName(female)");
            eqTransformer.transform(pipeToTransform);
            String[] femaleNamesTest = pipeToTransform.getDataMap().get("TEST_firstAndLastName_FEMALE").split("\\s+");
            Assert.assertTrue(femaleNamesLookUp.contains(femaleNamesTest[0]));
            Assert.assertTrue(lastNamesLookUp.contains(femaleNamesTest[1]));

            pipeToTransform.getDataMap().put("TEST_firstAndLastName_MALE", "%firstAndLastName(male)");
            eqTransformer.transform(pipeToTransform);
            String[] maleNamesTest = pipeToTransform.getDataMap().get("TEST_firstAndLastName_MALE").split("\\s+");
            Assert.assertTrue(maleNamesLookUp.contains(maleNamesTest[0]));
            Assert.assertTrue(lastNamesLookUp.contains(maleNamesTest[1]));

            pipeToTransform.getDataMap().put("TEST_firstAndLastName_ANY", "%firstAndLastName(any)");
            eqTransformer.transform(pipeToTransform);
            String[] userNames = pipeToTransform.getDataMap().get("TEST_firstAndLastName_MALE").split("\\s+");
            Assert.assertTrue(maleNamesLookUp.contains(userNames[0])
                    || femaleNamesLookUp.contains(userNames[0]));
            Assert.assertTrue(lastNamesLookUp.contains(userNames[1]));
        }

        Pattern alphaWithSpacesPattern = Pattern.compile("[A-Z].");

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_fullName_FEMALE", "%fullName(female)");
            eqTransformer.transform(pipeToTransform);
            String[] femaleNamesTest = pipeToTransform.getDataMap().get("TEST_fullName_FEMALE").split("\\s+");
            Matcher didItMatchFemale = alphaWithSpacesPattern.matcher(femaleNamesTest[1]);
            Assert.assertTrue(femaleNamesLookUp.contains(femaleNamesTest[0]));
            Assert.assertTrue(femaleNamesTest[1], didItMatchFemale.matches());
            Assert.assertTrue(lastNamesLookUp.contains(femaleNamesTest[2]));

            pipeToTransform.getDataMap().put("TEST_fullName_MALE", "%fullName(male)");
            eqTransformer.transform(pipeToTransform);
            String[] maleNamesTest = pipeToTransform.getDataMap().get("TEST_fullName_MALE").split("\\s+");
            Matcher didItMatchMale = alphaWithSpacesPattern.matcher(maleNamesTest[1]);
            Assert.assertTrue(maleNamesLookUp.contains(maleNamesTest[0]));
            Assert.assertTrue(maleNamesTest[1], didItMatchMale.matches());
            Assert.assertTrue(lastNamesLookUp.contains(maleNamesTest[2]));

            pipeToTransform.getDataMap().put("TEST_fullName_ANY", "%fullName(any)");
            eqTransformer.transform(pipeToTransform);
            String[] userNames = pipeToTransform.getDataMap().get("TEST_fullName_ANY").split("\\s+");
            Matcher didItMatchUser = alphaWithSpacesPattern.matcher(userNames[1]);
            Assert.assertTrue(maleNamesLookUp.contains(userNames[0])
                    || femaleNamesLookUp.contains(userNames[0]));
            Assert.assertTrue(userNames[1], didItMatchUser.matches());
            Assert.assertTrue(lastNamesLookUp.contains(userNames[2]));
        }


    }

    /**
     * %currency generates from a predefined list
     */
    @Test
    public void currencyTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] currencyCodes = {
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
                "XUA", "XXX", "YER", "ZAR", "ZMK", "ZWL"};

        Assert.assertTrue("Too small number of currency codes!", EquivalenceClass.CURRENCY_CODES.length >= 180);

        HashSet<String> currencyCodeLookUp = new HashSet<>(Arrays.asList(currencyCodes));

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST", "%currency");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue(currencyCodeLookUp.contains(pipeToTransform.getDataMap().get("TEST")));
        }
    }

    /**
     * Variables with values that are not macros are unaffected
     */
    @Test
    public void nonMacrosUnaffectedTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "Lorem ipsum doler  sit amet.");
        pipeToTransform.getDataMap().put("TEST2", "%Lorem");
        pipeToTransform.getDataMap().put("TEST3", "%ipsum(1,2)");

        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("Lorem ipsum doler  sit amet.", pipeToTransform.getDataMap().get("TEST"));
        Assert.assertEquals("%Lorem", pipeToTransform.getDataMap().get("TEST2"));
        Assert.assertEquals("%ipsum(1,2)", pipeToTransform.getDataMap().get("TEST3"));
    }

    /**
     * %state or $stateLong generates from a predefined list
     */
    @Test
    public void statesLongTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] statesLong = {
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
                "Wisconsin", "Wyoming"};

        Assert.assertTrue("Missed state long name(s)!", EquivalenceClass.STATE_LONG.length == 59);

        HashSet<String> statesLongLookUp = new HashSet<>(Arrays.asList(statesLong));

        // check 'state'
        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_states", "%state");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong state name(s)! Have '" + pipeToTransform.getDataMap().get("TEST_states") + "',"
                            + " but wait for one of '" + statesLongLookUp + "'...",
                    statesLongLookUp.contains(pipeToTransform.getDataMap().get("TEST_states")));
        }

        // check 'stateLong'
        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_statesLong", "%stateLong");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong state long name(s)! Have '" + pipeToTransform.getDataMap().get("TEST_statesLong") + "',"
                            + " but wait for one of '" + statesLongLookUp + "'...",
                    statesLongLookUp.contains(pipeToTransform.getDataMap().get("TEST_statesLong")));
        }
    }

    /**
     * %stateShort generates from a predefined list
     */
    @Test
    public void statesShortTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] statesShort = {
                "AL", "AK", "AS", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "GU", "HI", "ID",
                "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MH", "MA", "MI", "FM", "MN", "MS", "MO",
                "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "MP", "OH", "OK", "OR", "PW", "PA",
                "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "VI", "WA", "WV", "WI", "WY"};

        Assert.assertTrue("Missed state short name(s)!", EquivalenceClass.STATES_SHORT.length == 59);

        HashSet<String> statesShortLookUp = new HashSet<>(Arrays.asList(statesShort));

        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_statesShort", "%stateShort");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong state short name(s)! Have '" + pipeToTransform.getDataMap().get("TEST_statesShort") + "',"
                            + " but wait for one of '" + statesShortLookUp + "'...",
                    statesShortLookUp.contains(pipeToTransform.getDataMap().get("TEST_statesShort")));
        }
    }

    /**
     * %country generates from a predefined list
     */
    @Test
    public void countryLongTest() {
        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] countryLong = {
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

        Assert.assertTrue("Missed country long name(s)!", EquivalenceClass.COUNTRIES.length == 197);

        HashSet<String> countryLongLookUp = new HashSet<>(Arrays.asList(countryLong));

        // check 'country'
        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_country", "%country");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong country name(s)! Have '" + pipeToTransform.getDataMap().get("TEST_country") + "',"
                            + " but wait for one of '" + countryLongLookUp + "'...",
                    countryLongLookUp.contains(pipeToTransform.getDataMap().get("TEST_country")));
        }

        // check 'countryLong'
        for (int i = 0; i < 500; i++) {
            pipeToTransform.getDataMap().put("TEST_countryLong", "%countryLong");
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong country long name(s)! Have '" + pipeToTransform.getDataMap().get("TEST_countryLong") + "',"
                            + " but wait for one of '" + countryLongLookUp + "'...",
                    countryLongLookUp.contains(pipeToTransform.getDataMap().get("TEST_countryLong")));
        }
    }


    /**
     * %symbolNASDAQ and %securityNameNASDAQ generates from a predefined list
     */
    @Test
    public void securityNASDAQTest() {
        securityTestDo("nasdaqlisted.txt", EquivalenceClass.COUNT_NASDAQ_SECURITIES, "NASDAQ", "symbolNASDAQ",
                "securityNameNASDAQ", EquivalenceClass.SYMBOLS_NASDAQ, EquivalenceClass.SECURITY_NAMES_NASDAQ);

        securityTestDo("otherlisted.txt", EquivalenceClass.COUNT_NOT_NASDAQ_SECURITIES, "not NASDAQ", "symbolNotNASDAQ",
                "securityNameNotNASDAQ", EquivalenceClass.SYMBOLS_NOT_NASDAQ, EquivalenceClass.SECURITY_NAMES_NOT_NASDAQ);
    }

    private void securityTestDo(String fileName, int numberOfRecords, String securityType, String equivClass1,
                                String equivClass2, String[] symbolSet, String[] nameSet) {

        DataFormatter pipeToTransform = new DataFormatter();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        String[] symbols = new String[numberOfRecords];
        String[] securityNames = new String[numberOfRecords];

        InputStream fileData = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileData));
        String line;
        try {
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] lineSplitted = line.split("\\|");
                if (lineSplitted.length >= 2) {
                    symbols[i] = lineSplitted[0];
                    securityNames[i] = lineSplitted[1];
                    i++;
                }
            }
        } catch (IOException e) {
            Assert.assertFalse("Exception during reading '" + fileName + "' file! " + e, true);
        }

        Assert.assertTrue("Wrong number of " + securityType + " securities!", symbolSet.length == numberOfRecords);
        Assert.assertTrue("Wrong number of " + securityType + " securities!", nameSet.length == numberOfRecords);

        HashSet<String> symbolsLookUp = new HashSet<>(Arrays.asList(symbols));
        for (int i = 0; i < numberOfRecords * 10; i++) {
            pipeToTransform.getDataMap().put("TEST_symbol", "%" + equivClass1);
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong " + securityType + " security symbol ('"  + pipeToTransform.getDataMap().get("TEST_symbol") + "')!",
                    symbolsLookUp.contains(pipeToTransform.getDataMap().get("TEST_symbol")));
        }

        HashSet<String> securityNamesLookUp = new HashSet<>(Arrays.asList(securityNames));
        for (int i = 0; i < numberOfRecords * 10; i++) {
            pipeToTransform.getDataMap().put("TEST_securityName", "%" + equivClass2);
            eqTransformer.transform(pipeToTransform);
            Assert.assertTrue("Wrong " + securityType + " security name ('" + pipeToTransform.getDataMap().get("TEST_securityName") + "')!",
                    securityNamesLookUp.contains(pipeToTransform.getDataMap().get("TEST_securityName")));
        }
    }
}
