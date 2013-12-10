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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.Caller;
import org.junit.Assert;
import org.junit.Test;

public class TemplatingPropsTest {

    @Test
    public void renameTest() {

        File dataspec = new File(this.getClass().getClassLoader().getResource("tempPropsTest/tempPropsSpec.xls").getPath());
        File template = new File(this.getClass().getClassLoader().getResource("tempPropsTest/dummy.txt").getPath());
        File propFile = new File(this.getClass().getClassLoader().getResource("tempPropsTest/tempprops.properties").getPath());
        File output = new File(dataspec.getParentFile(), "output");
        String[] args = {"-dataspec", dataspec.getPath(), "-templates", template.getPath(), "-config", propFile.getPath(), "-out", output.getPath(), "-f"};
        Caller.main(args);
        // Assert we have a single output dir for defaults
        File defaultOut = new File(output, AppConstants.DEFAULT_OUT_DIRNAME);
        Assert.assertTrue("Default output dir not found", defaultOut.exists());
        // Assert that it contains a file named renamedTemplate.txt
        File templated = new File(defaultOut, "1/renamedTemplate.txt");
        Assert.assertTrue("renamedTemplate.txt not found", templated.exists());
    }

    @Test
    public void perGroupTest() {
        File dataspec = new File(this.getClass().getClassLoader().getResource("tempPropsTest/tempPropsSpec.xls").getPath());
        File template = new File(this.getClass().getClassLoader().getResource("tempPropsTest/dummy.txt").getPath());
        File propFile = new File(this.getClass().getClassLoader().getResource("tempPropsTest/pergroup.properties").getPath());
        File output = new File(dataspec.getParentFile(), "output");
        String[] args = {"-dataspec", dataspec.getPath(), "-templates", template.getPath(), "-config", propFile.getPath(), "-out", output.getPath(), "-f"};
        Caller.main(args);
        // Assert we have the output directory
        File defaultOut = new File(output, AppConstants.DEFAULT_OUT_DIRNAME);
        Assert.assertTrue("Default output dir not found", defaultOut.exists());
        // Assert that it contains 3 files
        File[] outfiles = new File(defaultOut, "1").listFiles();
        Assert.assertTrue("3 files were not found in the output. We have instead: " + outfiles, outfiles.length == 3);
    }

    @Test
    public void globalTest() throws FileNotFoundException, IOException {
        File dataspec = new File(this.getClass().getClassLoader().getResource("tempPropsTest/tempPropsSpec.xls").getPath());
        File templates = new File(this.getClass().getClassLoader().getResource("tempPropsTest/globaltest.txt").getPath());
        File propFile = new File(this.getClass().getClassLoader().getResource("tempPropsTest/globaltemp.properties").getPath());
        File output = new File(dataspec.getParentFile(), "output");
        String[] args = {"-dataspec", dataspec.getPath(), "-templates", templates.getPath(), "-config", propFile.getPath(), "-out", output.getPath(), "-f", "-isoPos"};
        Caller.main(args);
        // Assert we have the output directory
        File outdir = new File(output, AppConstants.ISOPOS_OUT_DIRNAME);
        Assert.assertTrue("Isopos output dir not found", outdir.exists());
        // Assert that it contains a filenemaed globaltest.txt
        File outfile = new File(outdir, "globaltest.txt");
        Assert.assertTrue(outfile.getPath() + " not found.", outfile.exists());
        // There should be 4 datasets, so we assert that it contains the string NAME: 4 times
        try (Reader templateReader = Files.newReader(outfile, Charsets.UTF_8)) {
            String templateAsString = IOUtils.toString(templateReader);
            Assert.assertTrue("Output doesn't have 4 datasets", StringUtils.countMatches(templateAsString, "NAME:") == 4);
        }
    }

    @Test
    public void toolsTest1() throws FileNotFoundException, IOException {
        File dataspec = new File(this.getClass().getClassLoader().getResource("tempPropsTest/tempPropsSpec.xls").getPath());
        File templates = new File(this.getClass().getClassLoader().getResource("tempPropsTest/toolstest.txt").getPath());
        File propFile = new File(this.getClass().getClassLoader().getResource("tempPropsTest/vtools.properties").getPath());
        File output = new File(dataspec.getParentFile(), "toolsTest1output");
        String[] args = {"-dataspec", dataspec.getPath(), "-templates", templates.getPath(), "-config", propFile.getPath(), "-out", output.getPath(), "-f", "-isoPos"};
        Caller.main(args);
        // Assert we have the output directory
        File outdir = new File(output, AppConstants.ISOPOS_OUT_DIRNAME);
        Assert.assertTrue("Isopos output dir not found", outdir.exists());
        File outfile = new File(outdir, "1/toolstest.txt");
        Assert.assertTrue(outfile.getPath() + " not found.", outfile.exists());
        try (Reader templateReader = Files.newReader(outfile, Charsets.UTF_8)) {
            String templateAsString = IOUtils.toString(templateReader);
            Assert.assertFalse("$date was found in the output. Looks like the date tool didn't work", templateAsString.contains("$date"));
        }
    }

}
