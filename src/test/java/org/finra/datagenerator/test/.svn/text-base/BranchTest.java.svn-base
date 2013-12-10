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

import java.io.File;
import java.io.FileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.Caller;
import org.junit.Assert;
import org.junit.Test;

public class BranchTest {

    @Test
    public void allEdgesSmokeTest() {
        // execute on combitest_nogroups
        File dspec = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmokeSpec.xls").getPath());
        File graph = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmoke.vdx").getPath());
        File template = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmokeTemplate.txt").getPath());
        File output = new File(dspec.getParentFile(), "output");
        String[] args = {"-dataspec", dspec.getPath(), "-templates", template.getPath(), "-branchgraph", graph.getPath(), "-out", output.getPath(), "-f", "-allEdges"};
        Caller.main(args);
        // Assert we have a single output file for defaults
        File outDir = new File(output, AppConstants.ALLEDGES_OUT_DIRNAME);
        Assert.assertTrue(outDir.getPath() + " not found", outDir.exists());
        // Assert that it contains 4 output subdirectories (datasets)
        Assert.assertTrue(" 4 output subdirs not found", outDir.listFiles((FileFilter) FileFilterUtils.directoryFileFilter()).length == 4);
    }

    @Test
    public void allPathsSmokeTest() {
        // execute on combitest_nogroups
        File dspec = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmokeSpec.xls").getPath());
        File graph = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmoke.vdx").getPath());
        File template = new File(this.getClass().getClassLoader().getResource("graphtests/graphSmokeTemplate.txt").getPath());
        File output = new File(dspec.getParentFile(), "output");
        String[] args = {"-dataspec", dspec.getPath(), "-templates", template.getPath(), "-branchgraph", graph.getPath(), "-out", output.getPath(), "-f", "-allPaths"};
        Caller.main(args);
        // Assert we have a single output file for defaults
        File outDir = new File(output, AppConstants.ALLPATHS_OUT_DIRNAME);
        Assert.assertTrue(outDir.getPath() + " not found", outDir.exists());
        // Assert that it contains 9 output subdirectories
        Assert.assertTrue(" 9 output subdirs not found", outDir.listFiles((FileFilter) FileFilterUtils.directoryFileFilter()).length == 9);
    }
}
