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
package org.finra.datagenerator.generation;

import com.google.common.base.Charsets;
import com.google.common.collect.MapMaker;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.VariableSpec;

public class PairwiseCombiDataSetGenerator implements ICombiDataSetGenerator {

    private static Logger LOG = Logger.getLogger(PairwiseCombiDataSetGenerator.class);

    private static PairwiseCombiDataSetGenerator instance = new PairwiseCombiDataSetGenerator();
    private static ConcurrentMap<DataSpec, List<DataSet>> dataSetCache = new MapMaker().makeMap();

    private PairwiseCombiDataSetGenerator() {
    }

    public static PairwiseCombiDataSetGenerator getInstance() {
        return instance;
    }

    public List<DataSet> generateDataSets(DataSpec dataSpec) {
        // only recompute the datasets if they aren't in the cache
        synchronized (dataSetCache) {
            if (!dataSetCache.containsKey(dataSpec)) {
                dataSetCache.put(dataSpec, internalDataSetGeneration(dataSpec));
            }
        }
        // return defensive copy of the cached dataset
        List<DataSet> ret = new LinkedList<DataSet>();
        for(DataSet cachedDs : dataSetCache.get(dataSpec)){
            ret.add(new DataSet(cachedDs));
        }
        return ret;
    }

    /**
     * Generates the pairwise datasets by writing a temp file formatted for UnsychronizedAllPairs.java, running the
     * pairwise algorithm on it, and creating datasets from the results.
     *
     * @param dataSpec
     * @return
     */
    private List<DataSet> internalDataSetGeneration(DataSpec dataSpec) {
		// UnsynchronizedAllPairs.java needs a particularly formatted file to read, containig all the choices for each variable.
        // so we create that out of the dataspec here and write it to a temp file.
        try {
            File tempFile = File.createTempFile("pairwise", null);
            BufferedWriter tempWriter = Files.newWriter(tempFile, Charsets.UTF_8);
			// for each variable in the spec, we need to write out it's possible values in a row
            // as well as keep track of which row corresponds to which variable
            List<String> varOrder = new ArrayList<String>();
            for(VariableSpec varSpec : dataSpec.getAllVariableSpecs()){
                // if it has values, print them separated by |
                for(String value : varSpec.getPropertySpec(AppConstants.VALUE).getPositiveValues()){
                    tempWriter.append(value+"|");
                }
                tempWriter.newLine();
                varOrder.add(varSpec.getName());
            }
            tempWriter.close();

            // ask UnsychronizedAllPairs to generate the combos
            String[][] combos = UnsynchronizedAllPairs.main(new String[]{"-goes", "0", "-file", tempFile.getParent()}, tempFile.getPath(), tempFile);

            // each row represents a unique combo and corresponds to a dataset
            List<DataSet> generatedDataSets = new LinkedList<DataSet>();
            for(int row = 0; row<combos.length; row++){
                DataSet pwDataSet = new DataSet();
                for(int col = 0; col<combos[row].length; col++){
                    // each column is a variable value, that type of which we look up in varOrder
                    VariableSpec spec = dataSpec.getVariableSpec(varOrder.get(col));
                    DataSetVariable pwVar = pwDataSet.createVariable(spec);
                    // overwrite it's value with the value from the pw generator
                    pwVar.setProperty(AppConstants.VALUE, combos[row][col]);
                }
                generatedDataSets.add(pwDataSet);
            }
            return generatedDataSets;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
