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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.disklist.DiskList;
import org.finra.datagenerator.input.BranchGraph;
import org.finra.datagenerator.input.BranchGraphEdge;
import org.finra.datagenerator.input.BranchGraphNode;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.IBranchGraphElement;

import com.google.common.base.Preconditions;

public class AllEdgesBranchDataSetGenerator extends AbstractBranchDataSetGenerator {

	private static Logger LOG = Logger.getLogger(AllEdgesBranchDataSetGenerator.class);
	
	private static AllEdgesBranchDataSetGenerator instance = new AllEdgesBranchDataSetGenerator();
	private static List<DataSet> dataSetCache =  new LinkedList<DataSet>();

	private AllEdgesBranchDataSetGenerator() {}

	public static AllEdgesBranchDataSetGenerator getInstance() {
		return instance;
	}

	public List<DataSet> generateDataSets(DataSpec dataSpec,
			BranchGraph branchGraph) {
		// only recompute the datasets if they aren't in the cache
		synchronized (dataSetCache) {
			if (dataSetCache.isEmpty())
				dataSetCache = internalDataSetGeneration(dataSpec, branchGraph);
		}
		// return defensive copy of the cached dataset 
		List<DataSet> ret = new LinkedList<DataSet>();
		for (DataSet cachedDs : dataSetCache) {
			ret.add(new DataSet(cachedDs));
		}
		return ret;
	}

	private List<DataSet> internalDataSetGeneration(DataSpec dataSpec,
			BranchGraph graph) {
		// add a root state as the parent of all possible start states
		List<BranchGraphNode> startNodes = new LinkedList<BranchGraphNode>();
		for (BranchGraphNode node : graph.vertexSet()) {
			if (graph.inDegreeOf(node) == 0){
				LOG.info("Adding start node " + node);
				startNodes.add(node);
			}
		}
		
		Preconditions.checkArgument(!startNodes.isEmpty(), "No nodes with indegree 0 (aka start nodes) were found in the graph.");
		BranchGraphNode rootNode = new BranchGraphNode(AppConstants.ROOT_NODE);
		graph.addVertex(rootNode);
		for (BranchGraphNode startNode : startNodes) {
			graph.addEdge(rootNode, startNode);
		}
		
		// basis for all edges is all paths
		List<DiskList<BranchGraphEdge>> allPaths = AllPathsBranchDataSetGenerator.getInstance().allPaths(graph, rootNode);
		// the set of edges to be covered = the union of edges covered by all paths
		Set<BranchGraphEdge> uncoveredEdges = new HashSet<BranchGraphEdge>();
		for (DiskList<BranchGraphEdge> path : allPaths) {
			uncoveredEdges.addAll(path);
		}


		// greedy approximation algorithm for mimimum set-cover
		List<DataSet> generatedDataSets = new LinkedList<DataSet>();
		while (!uncoveredEdges.isEmpty()) {
			// find the path that covers the most new edges
			int maxScore = 0;
			List<BranchGraphEdge> greedyPick = null;
			for (List<BranchGraphEdge> path : allPaths) {
				int pathScore = 0;
				for (BranchGraphEdge edge : path) {
					if (uncoveredEdges.contains(edge))
						pathScore++;
				}
				if (pathScore > maxScore) {
					maxScore = pathScore;
					greedyPick = path;
				}
			}
			// generate a dataset from this path if we can
			List<IBranchGraphElement> pathWithNodes = insertNodes(greedyPick, graph);
			try {
				DataSet dataSet = dataSetFromPath(pathWithNodes, dataSpec);
				generatedDataSets.add(dataSet);
				uncoveredEdges.removeAll(greedyPick);
				allPaths.remove(greedyPick);
			}
			catch (IllegalStateException e) {
				 // the requirements of this path cannot be satisfied, so no dataset, and it gets removed from the options
				allPaths.remove(greedyPick);
				// if this path contains an edge that others do not, then it is impossible to cover that edge
				Set<BranchGraphEdge> remainingEdges = new HashSet<BranchGraphEdge>();
				for (DiskList<BranchGraphEdge> path : allPaths) {
					remainingEdges.addAll(path);
				}
				for (BranchGraphEdge edge : greedyPick) {
					if (!remainingEdges.contains(edge))
						uncoveredEdges.remove(edge);
				}
				continue;
			}
		}
		
		return  generatedDataSets;
	}
	

}
