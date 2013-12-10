/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.Caller;
import org.junit.Assert;
import org.junit.Test;

public class CombiTest {

    @Test
    public void defaulTest() throws FileNotFoundException, IOException {
        // execute on combitest_nogroups
        File input = new File(Resources.getResource("combitest_nogroups").getPath());
        File output = new File(input.getParentFile(), "combitest_nogroups_output");
        String[] args = {"-dataspec", input.getPath() + "/dataspec", "-templates", input.getPath() + "/templates", "-out", output.getPath(), "-f"};
        Caller.main(args);
        // Assert we have a single output file for defaults
        File defaultOut = new File(output, AppConstants.DEFAULT_OUT_DIRNAME);
        Assert.assertTrue("Default output dir not found", defaultOut.exists());
        // Assert that it contains a file named vars.txt
        File templated = new File(defaultOut, "1/vars.txt");
        Assert.assertTrue("vars.txt not found", templated.exists());
        // Read in the file and asser that it contains the expected data
        try {
            Reader templateReader = Files.newReader(templated, Charsets.UTF_8);
            String templateAsString = IOUtils.toString(templateReader);
            String lookfor = "NAME: Kelly Vanleuven";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "EMAIL: test@mail.com";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "STREET: fake st.";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "NUMBER: 1235";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            templateReader.close();
        } catch (FileNotFoundException e) {
            Assert.fail();
            throw e;
        } catch (IOException e) {
            Assert.fail();
            throw e;
        }
    }

    @Test
    public void positiveTest() throws FileNotFoundException, IOException {
        // execute on combitest_nogrpus
        File input = new File(Resources.getResource("combitest_nogroups").getPath());
        File output = new File(input.getParentFile(), "combitest_nogroups_output");
        String[] args = {"-dataspec", input.getPath() + "/dataspec", "-templates", input.getPath() + "/templates", "-out", output.getPath(), "-f", "-isoPos"};
        Caller.main(args);
        // Assert we have the output dir
        File isoPosOut = new File(output, AppConstants.ISOPOS_OUT_DIRNAME);
        Assert.assertTrue("isoPos output dir not found", isoPosOut.exists());
        // Assert that it contains 4 subdirs
        File[] outdirs = isoPosOut.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
        Assert.assertEquals("4 positive output subdirs not found", 4, outdirs.length);
        // Assert that all positive values are covered
        Set<String> coverage = new HashSet<>();
        coverage.add("Kelly Vanleuven");
        coverage.add("Dinny O'Connel");
        coverage.add("test@mail.com");
        coverage.add("fake st.");
        coverage.add("evergreen terrace");
        coverage.add("1235");
        coverage.add("-5");

        for (File f : outdirs) {
            File template = new File(f, "vars.txt");
            Assert.assertTrue("vars.txt not found", template.exists());
            try (Reader templateReader = Files.newReader(template, Charsets.UTF_8)) {
                String templateAsString = IOUtils.toString(templateReader);
                Set<String> covered = new HashSet<>();
                for (String s : coverage) {
                    if (templateAsString.contains(s)) {
                        covered.add(s);
                    }
                }
                coverage.removeAll(covered);
            }
        }

        Assert.assertTrue("Some positive values were not covered: " + coverage, coverage.isEmpty());
    }

    @Test
    public void negativeTest() throws FileNotFoundException, IOException {
        // execute on combitest_nogrpus
        File input = new File(Resources.getResource("combitest_nogroups").getPath());
        File output = new File(input.getParentFile(), "combitest_nogroups_output");
        String[] args = {"-dataspec", input.getPath() + "/dataspec", "-templates", input.getPath() + "/templates", "-out", output.getPath(), "-f", "-isoNeg"};
        Caller.main(args);
        // Assert we have a single output file for negative
        File isoNegOut = new File(output, AppConstants.ISONEG_OUT_DIRNAME);
        Assert.assertTrue("isoneg output dir not found", isoNegOut.exists());
        // Assert that it contains a file named vars.txt
        File templated = new File(isoNegOut, "1/vars.txt");
        Assert.assertTrue("vars.txt not found", templated.exists());
        // Read in the file and asser that it contains the expected data
        try (Reader templateReader = Files.newReader(templated, Charsets.UTF_8)) {
            String templateAsString = IOUtils.toString(templateReader);
            String lookfor = "NAME: Kelly Vanleuven";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "EMAIL: test@mail.com";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "STREET: fake st.";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
            lookfor = "NUMBER: notanumber";
            Assert.assertTrue(lookfor + " not found", templateAsString.contains(lookfor));
        }
    }
}
