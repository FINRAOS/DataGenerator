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

        Calendar currDate = Calendar.getInstance();
        String current = new SimpleDateFormat("yyyy-MM-dd").format(currDate.getTime());
        int day = Integer.parseInt(current.substring(8, 10));
        int month = Integer.parseInt(current.substring(5, 7));
        int year = Integer.parseInt(current.substring(0, 4));

        String earlyMo = (Integer.toString(month).length() < 2 ? "0" + month : "" + month);
        String earlyDy = (Integer.toString(day).length() < 2 ? "0" + ++day : "" + ++day);

        List<Map<String, String>> newList = dateTest.pipelinePossibleStates(tag, listOfMaps);
        Assert.assertEquals(newList.get(0).get("name"), "1969-12-31");
        Assert.assertEquals(newList.get(1).get("name"), year + "-" + earlyMo + "-" + earlyDy);
        Assert.assertEquals(newList.get(2).get("name"), earlyMo + "-" + earlyDy + "-" + year);
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