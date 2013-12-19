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

import com.google.common.collect.MapMaker;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.GroupSpec;

/*
 * Generates the default combinatorial dataset. This is a dataset where all
 * variables are set to their default positive values, and all group requirements
 * are satisfied.
 */
public class DefaultCombiDataSetGenerator implements ICombiDataSetGenerator {

    private static final Logger log = Logger.getLogger(DefaultCombiDataSetGenerator.class);

    private static final DefaultCombiDataSetGenerator instance = new DefaultCombiDataSetGenerator();
    private static ConcurrentMap<DataSpec, DataSet> dataSetCache = new MapMaker().makeMap();

    private DefaultCombiDataSetGenerator() {
    }

    public static DefaultCombiDataSetGenerator getInstance() {
        return instance;
    }

    /**
     *
     * @param dataSpec
     * @return
     */
    @Override
    public List<DataSet> generateDataSets(DataSpec dataSpec) {
        // only recompute the dataset if it's not already in the cache
        synchronized (dataSetCache) {
            if (!dataSetCache.containsKey(dataSpec)) {
                dataSetCache.putIfAbsent(dataSpec, internalGenerateDataSet(dataSpec));
            }
        }

        // return a copy of the data set from the cache, wrapped in a List<>
        List<DataSet> ret = new LinkedList<>();
        ret.add(new DataSet(dataSetCache.get(dataSpec))); // defensive copy
        return ret;
    }

    /**
     * Makes a new DataSet and populates it via recursiveDataGeneration(),
     * beginning with the root group.
     *
     * @param dataSpec
     * @return
     */
    private DataSet internalGenerateDataSet(DataSpec dataSpec) {
        DataSet dataSet = new DataSet();
        recursiveDataGeneration(dataSpec, dataSet, dataSet.getGroup(AppConstants.ROOT_GROUP));
        return dataSet;
    }

    /**
     * Fills out default data recursively by groups. Enforces group hierarchy
     * and uniqueness requirements specified in the Data Spec.
     *
     * @param dataSet
     */
    private void recursiveDataGeneration(DataSpec dataSpec, DataSet dataSet, DataSetGroup parentGroup) {
        for (GroupSpec groupSpec : dataSpec.getAllGroupSpecs()) {
            // for each group whose parent is this group type
            if (groupSpec.getParentGroupType().equals(parentGroup.getType())) {
                // Figure out how many groups ought to be created per the dataspec. Recursively create that many groups.
                int n = groupSpec.getNumPerParent();
                for (int i = 0; i < n; ++i) {
                    DataSetGroup createdGroup = dataSet.createGroup(groupSpec, parentGroup);
                    recursiveDataGeneration(dataSpec, dataSet, createdGroup);
                    // if certain variables are required to be unique, overwrite the default values with the next unique combo
                    if (groupSpec.requiresUniqueElems()) {
                        Map<String, String> combo = groupSpec.getUniqueCombo(i);
                        for (Entry<String, String> elem : combo.entrySet()) {
                            createdGroup.get(elem.getKey()).setProperty(AppConstants.VALUE, elem.getValue());
                        }
                    }
                }
            }
        }
    }

}
