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

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.disklist.DiskList;
import org.finra.datagenerator.input.BranchGraph;
import org.finra.datagenerator.input.BranchGraphEdge;
import org.finra.datagenerator.input.BranchGraphNode;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.IBranchGraphElement;

public class AllPathsBranchDataSetGenerator extends AbstractBranchDataSetGenerator {

    private static final Logger log = Logger.getLogger(AllPathsBranchDataSetGenerator.class);

    private static int LOOP_DEPTH = 1; //max number of visits to any node
    private static AllPathsBranchDataSetGenerator instance = new AllPathsBranchDataSetGenerator();
    private static List<DataSet> dataSetCache = new LinkedList<>();

    private AllPathsBranchDataSetGenerator() {
    }

    public static AllPathsBranchDataSetGenerator getInstance() {
        return instance;
    }

    public List<DataSet> generateDataSets(DataSpec dataSpec, BranchGraph branchGraph) {
        // only recompute the datasets if they aren't in the cache
        // Who would have put them into the cache? If allPaths had been run before now?
        synchronized (dataSetCache) {
            if (dataSetCache.isEmpty()) {
                dataSetCache = internalDataSetGeneration(dataSpec, branchGraph);
            }
        }

        //Why is a defensive copy necessary? Is someone changing the datasets after generation?
        // return defensive copy of the cached dataset
        List<DataSet> ret = new LinkedList<>();
        for(DataSet cachedDs : dataSetCache){
            ret.add(new DataSet(cachedDs));
        }
        return ret;
    }

    private List<DataSet> internalDataSetGeneration(DataSpec dataSpec, BranchGraph graph) {
        // add a root state as the parent of all possible start states
        List<BranchGraphNode> startNodes = new LinkedList<>();
        for(BranchGraphNode node : graph.vertexSet()){
            if (graph.inDegreeOf(node)==0) {
                startNodes.add(node);
            }
        }
        Preconditions.checkArgument(!startNodes.isEmpty(), "No nodes with indegree 0 (aka start nodes) were found in the graph.");
        BranchGraphNode rootNode = new BranchGraphNode(AppConstants.ROOT_NODE);
        graph.addVertex(rootNode);
        for(BranchGraphNode startNode : startNodes){
            graph.addEdge(rootNode, startNode);
        }

        List<DiskList<BranchGraphEdge>> pathsList = allPaths(graph, rootNode);
        log.info("There are "+pathsList.size()+" paths.");

        // now that we have all the paths, generate datasets from them
        List<DataSet> generatedDataSets = new LinkedList<>();
        int i = 0;
        for(List<BranchGraphEdge> path : pathsList){

            if (i%1000==0) {
                log.info("Processed path "+i);
            }

            i++;

            List<IBranchGraphElement> pathWithNodes = insertNodes(path, graph);
            try {
                DataSet dataSet = dataSetFromPath(pathWithNodes, dataSpec);
                generatedDataSets.add(dataSet);
            }
            catch (IllegalStateException e) {
                log.warn("Skipping dataset due to IllegalStateException: "+e.getMessage());
                continue; // the requirements of this path cannot be satisfied, so no dataset
            }
        }

        return generatedDataSets;
    }

    protected List<DiskList<BranchGraphEdge>> allPaths(
            BranchGraph graph, BranchGraphNode rootNode) {
        List<DiskList<BranchGraphEdge>> pathsList = new LinkedList<>();
        // for each starting edge
        for(BranchGraphEdge startEdge : graph.outgoingEdgesOf(rootNode)){
            // keep a count of how many visits to each node
            Map<BranchGraphNode, Integer> nodeVisits = new HashMap<>();
            for(BranchGraphNode node : graph.vertexSet()){
                nodeVisits.put(node, 0);
            }
            // we start off visiting this edge and node
            LinkedList<BranchGraphEdge> curPath = new LinkedList<>();
            curPath.add(startEdge);
            nodeVisits.put(graph.getEdgeTarget(startEdge), 1);
            findPaths(graph, curPath, nodeVisits, pathsList);
        }
        return pathsList;
    }

    private void findPaths(
            BranchGraph graph,
            LinkedList<BranchGraphEdge> curPath,
            Map<BranchGraphNode, Integer> nodeVisits,
            List<DiskList<BranchGraphEdge>> pathsList) {

        Set<BranchGraphEdge> neighborEdges = graph.outgoingEdgesOf(graph.getEdgeTarget(curPath.getLast()));

        if (neighborEdges.isEmpty()) // special case where we start on an isolated node
        {
            pathsList.add(new DiskList<>(curPath));
        }
        // examine adjacent edges for path termination condition
        for(BranchGraphEdge neighbor : neighborEdges){
            if (nodeVisits.get(graph.getEdgeTarget(neighbor))>=LOOP_DEPTH
                    ||graph.outDegreeOf(graph.getEdgeTarget(neighbor))==0) {
                curPath.add(neighbor);
                pathsList.add(new DiskList<>(curPath));
                curPath.removeLast();
                // don't break here because multiple edges may go to end state
            }
        }
        //recursive search on all neighbor edges that aren't termination edges
        for(BranchGraphEdge neighbor : neighborEdges){
            BranchGraphNode neighborNode = graph.getEdgeTarget(neighbor);
            if (nodeVisits.get(neighborNode)>=LOOP_DEPTH
                    ||graph.outDegreeOf(neighborNode)==0) {
                continue;
            }
            curPath.add(neighbor);
            nodeVisits.put(neighborNode, nodeVisits.get(neighborNode)+1); //increment the visit count
            findPaths(graph, curPath, nodeVisits, pathsList);
            nodeVisits.put(neighborNode, nodeVisits.get(neighborNode)-1); //decrement as we back up
            curPath.removeLast();
        }
    }

}
