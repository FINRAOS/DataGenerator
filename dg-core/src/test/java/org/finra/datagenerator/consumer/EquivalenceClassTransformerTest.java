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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
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
     * %digits macro gives a string of n digits
     */
    @Test
    public void digitsTest() {
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "%ssn");
        eqTransformer.transform(pipeToTransform);
        Pattern alphaWithSpacesPattern = Pattern.compile("^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))"
                + "-((?!00)\\d{2})-((?!0000)\\d{4})$");
        Matcher didItMatch = alphaWithSpacesPattern.matcher(pipeToTransform.getDataMap().get("TEST"));
        Assert.assertTrue(didItMatch.matches());
    }

    /**
     * %currency generates from a predefined list
     */
    @Test
    public void currencyTest() {
        DataPipe pipeToTransform = new DataPipe();
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
        DataPipe pipeToTransform = new DataPipe();
        EquivalenceClassTransformer eqTransformer = new EquivalenceClassTransformer();

        pipeToTransform.getDataMap().put("TEST", "Lorem ipsum doler  sit amet.");
        pipeToTransform.getDataMap().put("TEST2", "%Lorem");
        pipeToTransform.getDataMap().put("TEST3", "%ipsum(1,2)");

        eqTransformer.transform(pipeToTransform);
        Assert.assertEquals("Lorem ipsum doler  sit amet.", pipeToTransform.getDataMap().get("TEST"));
        Assert.assertEquals("%Lorem", pipeToTransform.getDataMap().get("TEST2"));
        Assert.assertEquals("%ipsum(1,2)", pipeToTransform.getDataMap().get("TEST3"));
    }
}
