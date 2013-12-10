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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.VariableSpec;

import com.google.common.collect.MapMaker;

public class NegativeCombiDataSetGenerator implements ICombiDataSetGenerator {

	private static Logger LOG =Logger.getLogger(NegativeCombiDataSetGenerator.class);
	
	private static NegativeCombiDataSetGenerator instance = new NegativeCombiDataSetGenerator();
	private static ConcurrentMap<DataSpec, List<DataSet>> dataSetCache = new MapMaker().makeMap();
	
	private NegativeCombiDataSetGenerator() {}

	public static NegativeCombiDataSetGenerator getInstance() {
		return instance;
	}
	
	public List<DataSet> generateDataSets(DataSpec dataSpec) {
		// only recompute the datasets if they aren't in the cache
		// TODO: thread issue for concurrentmodification since the synchronize block doesn't extend out till return ret;
		synchronized (dataSetCache) {
			if (!dataSetCache.containsKey(dataSpec))
				dataSetCache.put(dataSpec, internalDataSetGeneration(dataSpec));
		}
		// return defensive copy of cached dataset
		List<DataSet> ret = new ArrayList<DataSet>();
		for (DataSet cachedDs : dataSetCache.get(dataSpec)) {
			ret.add(new DataSet(cachedDs));
		}
		return ret;
	}

	
	private List<DataSet> internalDataSetGeneration(DataSpec dataSpec) {
		// for each variable in the spec that has negative values
		// create new default datasets and overwrite a the values to cover each one
		List<DataSet> generatedDataSets = new LinkedList<DataSet>();
		for (VariableSpec varspec : dataSpec.getAllVariableSpecs()) {
			if (!varspec.getPropertySpec(AppConstants.VALUE).getNegativeValues().isEmpty()) {
				List<String> negValues = varspec.getPropertySpec(AppConstants.VALUE).getNegativeValues();
				for (String negValue : negValues) {
					DataSet newDataSet = DefaultCombiDataSetGenerator.getInstance().generateDataSets(dataSpec).get(0);
					// overwrite with the new negative value
					newDataSet.get(varspec.getName()).setProperty(AppConstants.VALUE, negValue);
					generatedDataSets.add(newDataSet);
				}
			}
		}
		return generatedDataSets;
	}
}

