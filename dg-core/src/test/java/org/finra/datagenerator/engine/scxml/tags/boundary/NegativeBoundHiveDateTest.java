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
package org.finra.datagenerator.engine.scxml.tags.boundary;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Negative tests for date type
 */
public class NegativeBoundHiveDateTest {

    /**
     * Test for lower bound date
     */
    @Test
    public void earliestTest() {
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();

        tag.setEarliest("1970-01-01");
        Assert.assertEquals(tag.getEarliest(), "1970-01-01");

        tag.setEarliest("2015-01-01");
        Assert.assertEquals(tag.getEarliest(), "2015-01-01");
    }

    /**
     * Test for lower bound date
     */
    @Test
    public void latestDateTest() {
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();

        tag.setLatest("1970-01-01");
        Assert.assertEquals(tag.getLatest(), "1970-01-01");

        tag.setLatest("2015-01-01");
        Assert.assertEquals(tag.getLatest(), "2015-01-01");
    }

    /**
     * testing leap year
     */
    @Test
    public void leapYearTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDate dateTest = new NegativeBoundHiveDate();
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();
        tag.setName("name");
        tag.setEarliest("2016-03-01");
        tag.setLatest("2016-02-28");

        List<Map<String, String>> newList = dateTest.pipelinePossibleStates(tag, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name"), "2016-02-29");
        Assert.assertEquals(newList.get(1).get("name"), "2016-02-29");
    }

    /**
     * testing lower bound
     */
    @Test
    public void lowerBoundTest2() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDate dateTest = new NegativeBoundHiveDate();
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();
        tag.setName("name");

        Calendar nextDate = Calendar.getInstance();
        nextDate.add(Calendar.DATE, +1);
        String next = new SimpleDateFormat("yyyy-MM-dd").format(nextDate.getTime());

        String day = next.substring(8, 10);
        String month = next.substring(5, 7);
        String year = next.substring(0, 4);

        List<Map<String, String>> newList = dateTest.pipelinePossibleStates(tag, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name"), "1969-12-31");
        Assert.assertEquals(newList.get(1).get("name"), next);
        Assert.assertEquals(newList.get(2).get("name"), month + "-" + day + "-" + year);
    }

    /**
     * test with provided min and max dates
     */
    @Test
    public void dateTest() {
        Map<String, String> variableDomains = new HashMap<>();
        List<Map<String, String>> listOfMaps = new LinkedList<>();
        listOfMaps.add(variableDomains);

        NegativeBoundHiveDate dateTest = new NegativeBoundHiveDate();
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();
        tag.setName("name");
        tag.setEarliest("2012-12-31");
        tag.setLatest("2014-01-01");

        List<Map<String, String>> newList = dateTest.pipelinePossibleStates(tag, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name"), "2012-12-30");
        Assert.assertEquals(newList.get(1).get("name"), "2014-01-02");
        Assert.assertEquals(newList.get(2).get("name"), "01-02-2014");
        Assert.assertEquals(newList.size(), 4);
    }

    /**
     * test for setName() and getName()
     */
    @Test
    public void nameTest() {
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();

        tag.setName("date_test");
        Assert.assertEquals(tag.getName(), "date_test");

        tag.setName("date_test2");
        Assert.assertEquals(tag.getName(), "date_test2");
    }

    /**
     * test for setName() and getName()
     */
    @Test
    public void nullTest() {
        NegativeBoundHiveDate.NegativeBoundHiveDateTag tag = new NegativeBoundHiveDate.NegativeBoundHiveDateTag();

        tag.setNullable("true");
        Assert.assertEquals(tag.getNullable(), "true");

        tag.setNullable("false");
        Assert.assertEquals(tag.getNullable(), "false");
    }
}
