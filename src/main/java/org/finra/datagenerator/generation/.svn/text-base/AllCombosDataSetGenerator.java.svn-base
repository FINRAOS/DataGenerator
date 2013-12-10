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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.VariableSpec;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

public class AllCombosDataSetGenerator implements ICombiDataSetGenerator {

	private static Logger LOG =Logger.getLogger(AllCombosDataSetGenerator.class);

	private static AllCombosDataSetGenerator instance = new AllCombosDataSetGenerator();
	private static ConcurrentMap<DataSpec, List<DataSet>> dataSetCache = new MapMaker().makeMap();

	private AllCombosDataSetGenerator() {}

	public static AllCombosDataSetGenerator getInstance() {
		return instance;
	}

	public List<DataSet> generateDataSets(DataSpec dataSpec) {
		// only recompute the datasets if they aren't in the cache
		synchronized (dataSetCache) {
			if (!dataSetCache.containsKey(dataSpec))
				dataSetCache.put(dataSpec, internalDataSetGeneration(dataSpec));
		}
		// return defensive copy of the cached dataset
		List<DataSet> ret = new LinkedList<DataSet>();
		for (DataSet cachedDs : dataSetCache.get(dataSpec)) {
			ret.add(new DataSet(cachedDs));
		}
		return ret;
	}
	
	
	/**
	 * Generates datasets the cover every combination of positive values
	 * @param dataSpec
	 * @return
	 */
	private List<DataSet> internalDataSetGeneration(DataSpec dataSpec) {
		
		// The idea here is to take all of the variable specs, enumerate the positive values for each,
		// take the cartesian product of these sets, and then build a dataset for each set in the result
		ArrayList<VariableSpec> variableSpecs = Lists.newArrayList(dataSpec.getAllVariableSpecs());
		
		List<Set<String>> options = Lists.transform(variableSpecs, new Function<VariableSpec,Set<String>>(){
			public Set<String> apply(VariableSpec varSpec) {
				System.out.println(varSpec.getPropertySpec(AppConstants.VALUE).getPositiveValues());
				return Sets.newHashSet(varSpec.getPropertySpec(AppConstants.VALUE).getPositiveValues());
			}
		});
		
		Set<List<String>> combos = Sets.cartesianProduct(options); // guava is awesome!!
		
		List<DataSet> generatedDataSets = new LinkedList<DataSet>();
		for (List<String> combo : combos) {
			DataSet comboDataSet = new DataSet();
			// each element in this combo corresponds directly to an elemnt in variableSpecs
			// so we advance two iterators through both lists, creating a variable and setting it's value
			Iterator<String> comboIter = combo.iterator();
			Iterator<VariableSpec> varSpecIter = variableSpecs.iterator();
			for (int i = 0; i < combo.size(); ++i) {
				comboDataSet.createVariable(varSpecIter.next()).setProperty(AppConstants.VALUE, comboIter.next());
			}
			generatedDataSets.add(comboDataSet);
		}
		return generatedDataSets;
	}

}
