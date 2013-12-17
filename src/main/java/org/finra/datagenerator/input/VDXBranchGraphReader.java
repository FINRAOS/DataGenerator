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
package org.finra.datagenerator.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.Vector;

import org.apache.commons.digester3.Digester;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.generation.DataSet;
import org.finra.datagenerator.generation.PositiveCombiDataSetGenerator;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class VDXBranchGraphReader implements IBranchGraphReader {

    private static Logger LOG = Logger.getLogger(VDXBranchGraphReader.class);

    private BranchGraph branchGraph = new BranchGraph(BranchGraphEdge.class);
    private Digester digester;

    private Map<Integer, VisioShape> shapesByID = new HashMap<Integer, VisioShape>();
    private Map<Integer, List<VisioConnect>> connectorsByID = new HashMap<Integer, List<VisioConnect>>();
    private Future<DataSpec> dataSpecFuture;

    public BranchGraph readBranchGraphFromFiles(Collection<File> files) {
        Preconditions.checkArgument(!files.isEmpty(), "No files were provided to read branch graph from.");

        for(File f : files){
            LOG.info("BranchGraph: "+f.getName());
            processVDXFile(f);
        }
        return branchGraph;
    }

    private void processVDXFile(File file) {
        digester = new Digester(); // Apache recommends a new digester instance for each parse.
        configDigester();

        shapesByID.clear(); // clear these out for each new file
        connectorsByID.clear();

        try {
            digester.parse(file);
        }
        catch (IOException e) {
            LOG.error("Error parsing vdx file: "+file.getName(), e);
        }
        catch (SAXException e) {
            LOG.error("Error parsing vdx file: "+file.getName(), e);
        }

        incorporateVisioData();
    }

    private void configDigester() {
        digester.setValidating(false);

        /*
         * What follows here is a configuration of the Apache Digester to parse the vdx elements
         * and return to us VisioShape and VisioConnect objects via the 'addVisioShape' and 'addVisioConnect'
         * methods. Some of this may seem funky, but that's because we have to adapt to the vdx conventions.
         */
		// both nodes and connections are represented by <shape> tags
        // we read their ID, text, and properties values if available
        digester.addObjectCreate("VisioDocument/Pages/Page/Shapes/Shape", VisioShape.class);
        // all shapes have an ID
        digester.addSetProperties("VisioDocument/Pages/Page/Shapes/Shape", "ID", "id");
        // <Text> will be non-empty for vertices
        digester.addCallMethod("VisioDocument/Pages/Page/Shapes/Shape/Text", "setText", 0);
        // connections have a list of properties
        digester.addCallMethod("VisioDocument/Pages/Page/Shapes/Shape/Prop", "addProperty", 2);
        digester.addCallParam("VisioDocument/Pages/Page/Shapes/Shape/Prop/Label", 0);
        digester.addCallParam("VisioDocument/Pages/Page/Shapes/Shape/Prop/Value", 1);

        digester.addSetNext("VisioDocument/Pages/Page/Shapes/Shape", "addVisioShape");

        // There is a <connect> element for each end of a connection, i.e. 2 per connection
        digester.addObjectCreate("VisioDocument/Pages/Page/Connects/Connect", VisioConnect.class);
        digester.addSetProperties("VisioDocument/Pages/Page/Connects/Connect",
                new String[]{"FromSheet", "ToSheet", "FromCell"},
                new String[]{"shapeId", "connectedToId", "fromCell"});
        digester.addSetNext("VisioDocument/Pages/Page/Connects/Connect", "addVisioConnect");

        digester.push(this);
    }

    public void addVisioShape(VisioShape shape) {
        shapesByID.put(shape.getId(), shape);
    }

    public void addVisioConnect(VisioConnect connect) {
        int id = connect.getShapeId();
        if (!connectorsByID.containsKey(id)) {
            connectorsByID.put(id, new LinkedList<VisioConnect>());
        }
        connectorsByID.get(id).add(connect);
    }

    /**
     * Takes the VisioShapes and VisioConnects we have parsed, and builds them into the graph object
     */
    private void incorporateVisioData() {
        // each connection is represented by a pair of VisioConnects in connectorsByID
        for(List<VisioConnect> connectPair : connectorsByID.values()){

            if (connectPair.size()!=2) {
                if (connectPair.size()==0) {
                    LOG.error("Visio connection was not connected to any shapes");
                } else if (connectPair.size()==1) {

                    VisioShape shape = shapesByID.get(connectPair.get(0).getConnectedToId());
                    String label = shape.getText();
                    LOG.error("Visio connection was not connected to only 1 shape with label "+label);
                }
                continue;
            }

            // create BranchNodes for the two endpoints
            VisioConnect connect1 = connectPair.get(0);
            BranchGraphNode node1 = createNodeFromVisioShape(shapesByID.get(connect1.getConnectedToId()));
            VisioConnect connect2 = connectPair.get(1);
            BranchGraphNode node2 = createNodeFromVisioShape(shapesByID.get(connect2.getConnectedToId()));

			// add the nodes to the graph
            // if the graph already contains nodes by this name, we need to merge in the new requirements
            if (branchGraph.containsVertex(node1)) {
                for(BranchGraphNode existingNode : branchGraph.vertexSet()){
                    if (existingNode.equals(node1)) {
                        existingNode.mergeRequirements(node1);
                    }
                }
            } else {
                branchGraph.addVertex(node1);
            }

            if (branchGraph.containsVertex(node2)) {
                for(BranchGraphNode existingNode : branchGraph.vertexSet()){
                    if (existingNode.equals(node1)) {
                        existingNode.mergeRequirements(node1);
                    }
                }
            } else {
                branchGraph.addVertex(node2);
            }

            createEdgeFromVisioShape(connect1, connect2, node1, node2);

        }
    }

    private BranchGraphNode createNodeFromVisioShape(VisioShape shape) {
        Preconditions.checkNotNull(shape.getText(), "Visio node has no text. Text is required to uniquely identify nodes.");
        BranchGraphNode node = new BranchGraphNode(shape.getText());
        addRequirementsToGraphElement(node, shape.getProperties());
        return node;
    }

	// Adds edges to the graph.
    // wnilkamal@yahoo.com: Add feature to capture multiple string values instead of just a single string value.
    private void createEdgeFromVisioShape(VisioConnect connect1, VisioConnect connect2, BranchGraphNode node1, BranchGraphNode node2) {
		// wnilkamal@yahoo.com: Add feature to capture multiple string values instead of just a single string value.
        // create the edge
        BranchGraphEdge newEdge = new BranchGraphEdge();
        VisioShape shape = shapesByID.get(connect1.getShapeId());
        Vector<Vector<SetPropertyRequirement>>[] multiEdgeValues = addRequirementsToGraphElement(newEdge, shape.getProperties());

		// wnilkamal@yahoo.com: Add feature to capture multiple string values instead of just a single string value.
        // If no collections variables are set eg. 'setPropertyCollection' or setPropertyAllCombos or 'setPropertyPairwise'), just add the on edge
        if ((multiEdgeValues[CollectionPropertiesENUM.setPropertyCollection.getValue()].size()==0)&&(multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionAllCombos.getValue()].size()==0)&&(multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionPairwise.getValue()].size()==0)) {
            // figure out directionality and add edge
            if (connect1.isOrigin()) {
                branchGraph.addEdge(node1, node2, newEdge);
                LOG.debug("adding edge from: "+node1+" to: "+node2);
            } else {
                branchGraph.addEdge(node2, node1, newEdge);
                LOG.debug("adding edge from: "+node2+" to: "+node1);
            }
        } // wnilkamal@yahoo.com: Add feature to capture multiple string values instead of just a single string value.
        // If no collections variables are set eg. 'setPropertyCollection' or 'setPropertyAllCombos' or 'setPropertyPairwise'), add all edges within
        // setPropertyCollection ***
        else if (multiEdgeValues[CollectionPropertiesENUM.setPropertyCollection.getValue()].size()>0) {
            for(Vector<SetPropertyRequirement> reqs : multiEdgeValues[CollectionPropertiesENUM.setPropertyCollection.getValue()]){
                for(SetPropertyRequirement props : reqs){
                    BranchGraphEdge newEdgeForEachCollectionItem = newEdge.copy();
                    newEdgeForEachCollectionItem.addSetPropertyReq(props);
                    // figure out directionality and add edge
                    if (connect1.isOrigin()) {
                        branchGraph.addEdge(node1, node2, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node1+" to: "+node2);
                    } else {
                        branchGraph.addEdge(node2, node1, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node2+" to: "+node1);
                    }
                }
            }

        } // setPropertyCollectionAllCombos ***
        else if (multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionAllCombos.getValue()].size()>0) {
            DataSpec combos = null;
            try {
                combos = dataSpecFuture.get();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            for(Vector<SetPropertyRequirement> reqs : multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionAllCombos.getValue()]){
                PropertySpec ps = new PropertySpec(AppConstants.VALUE);
                for(SetPropertyRequirement props : reqs){
                    ps.addPositiveValue(props.getValue());
                }
                VariableSpec vs = new VariableSpec(reqs.get(0).getVariableAlias());
                vs.setGroup(AppConstants.DEFAULT_GROUP);
                vs.addPropertySpec(ps);
                combos.addVariableSpec(vs);
            }
            //List<DataSet> ds = DefaultCombiDataSetGenerator.getInstance().generateDataSets(combos);
            List<DataSet> ds = PositiveCombiDataSetGenerator.getInstance().generateDataSets(combos);
			// DefaultCombiDataSetGenerator.getInstance().generateDataSets(combos);
            //TBD **** look somer errror

            for(Vector<SetPropertyRequirement> reqs : multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionAllCombos.getValue()]){
                for(SetPropertyRequirement props : reqs){
                    BranchGraphEdge newEdgeForEachCollectionItem = newEdge.copy();
                    newEdgeForEachCollectionItem.addSetPropertyReq(props);
                    // figure out directionality and add edge
                    if (connect1.isOrigin()) {
                        branchGraph.addEdge(node1, node2, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node1+" to: "+node2);
                    } else {
                        branchGraph.addEdge(node2, node1, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node2+" to: "+node1);
                    }
                }
            }

        } // setPropertyCollectionPairwise ***
        else if (multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionPairwise.getValue()].size()>0) {
			//DataSpec

            for(Vector<SetPropertyRequirement> reqs : multiEdgeValues[CollectionPropertiesENUM.setPropertyCollectionPairwise.getValue()]){
                for(SetPropertyRequirement props : reqs){
                    BranchGraphEdge newEdgeForEachCollectionItem = newEdge.copy();
                    newEdgeForEachCollectionItem.addSetPropertyReq(props);
                    // figure out directionality and add edge
                    if (connect1.isOrigin()) {
                        branchGraph.addEdge(node1, node2, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node1+" to: "+node2);
                    } else {
                        branchGraph.addEdge(node2, node1, newEdgeForEachCollectionItem);
                        LOG.debug("adding edge from: "+node2+" to: "+node1);
                    }
                }
            }

        }

    }

    private Vector<Vector<SetPropertyRequirement>>[] addRequirementsToGraphElement(IBranchGraphElement elem, Multimap<String, String> multimap) {
        // wnilkamal@yahoo.com: Vector or Vectors containing the list of multivalued setPropertyCollection
        Vector<Vector<SetPropertyRequirement>> retMultiValsetPropertyCollection = new Vector<Vector<SetPropertyRequirement>>();
        // wnilkamal@yahoo.com: Vector or Vectors containing the list of multivalued setPropertyCollectionAllCombos
        Vector<Vector<SetPropertyRequirement>> retMultiValsetPropertyCollectionAllCombos = new Vector<Vector<SetPropertyRequirement>>();
        // wnilkamal@yahoo.com: Vector or Vectors containing the list of multivalued setPropertyCollectionPairwise
        Vector<Vector<SetPropertyRequirement>> retMultiValsetPropertyCollectionPairwise = new Vector<Vector<SetPropertyRequirement>>();

        // turn the visio annotations into the various requirements types
        for(Entry<String, String> property : multimap.entries()){
            String key = property.getKey();
            String value = property.getValue();

            // wnilkamal@yahoo.com: ESCAPE CHARACTER FOR the comma. All /, escaped commas will not be treated as a splitter.
            value = value.replaceAll("/,", "#escCHAR453");

            if (value==null) {
                LOG.error("Skipping graph annotation with null value: ("+key+" ,"+value+")");
                System.exit(1);
                continue;
            }

            // parse createVar annotations - described in wiki documentation
            Splitter dotSplitter = Splitter.on(".").omitEmptyStrings().trimResults();
            Splitter commaSplitter = Splitter.on(",").omitEmptyStrings().trimResults();
            ArrayList<String> splitKey = Lists.newArrayList(dotSplitter.split(key));
            ArrayList<String> splitVal = Lists.newArrayList(commaSplitter.split(value));
            int nKeyTokens = splitKey.size();
            int nValTokens = splitVal.size();
            if (nKeyTokens==0||nValTokens==0) {
                LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                System.exit(1);
                continue;
            }
            String cmd = splitKey.get(splitKey.size()-1); // the last token in the split key should be the command

            // based on the cmd string, we interpret the annotation as one of the following requirements
            if (cmd.equalsIgnoreCase("createVariable")) {
                // discard bad input
                if (nKeyTokens>2||nValTokens>2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                    System.exit(1);
                    continue;
                }
                String varType = splitVal.get(0);
                CreateVariableRequirement createVarReq = new CreateVariableRequirement(varType);
                if (nValTokens==2) {
                    createVarReq.setAlias(splitVal.get(1));
                }
                if (nKeyTokens==2) {
                    createVarReq.setGroupAlias(splitKey.get(0));
                }
                elem.addCreateVariableReq(createVarReq);
            } else if (cmd.equalsIgnoreCase("createGroup")) {
                // discard bad input
                if (nKeyTokens>2||nValTokens>2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                    System.exit(1);
                    continue;
                }
                String groupType = splitVal.get(0);
                CreateGroupRequirement createGroupReq = new CreateGroupRequirement(groupType);
                if (nValTokens==2) {
                    createGroupReq.setAlias(splitVal.get(1));
                }
                if (nKeyTokens==2) {
                    createGroupReq.setParentAlias(splitKey.get(0));
                }
                elem.addCreateGroupReq(createGroupReq);
            } else if (cmd.equalsIgnoreCase("setProperty")) {
                if (nKeyTokens<2||nKeyTokens>3||nValTokens!=2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                    System.exit(1);
                    continue;
                }
                String varAlias;
                if (nKeyTokens==2) {
                    varAlias = splitKey.get(0);
                } else {
                    varAlias = splitKey.get(1);
                }
                String propName = splitVal.get(0);
                // wnilkamal@yahoo.com: ESCAPE CHARACTER FOR the comma. All /, escaped commas will not be treated as a splitter.
                String propVal = (splitVal.get(1)).replaceAll("#escCHAR453", ",");
                SetPropertyRequirement setPropReq = new SetPropertyRequirement(varAlias, propName, propVal);
                if (nKeyTokens==3) {
                    setPropReq.setGroupAlias(splitKey.get(0));
                }
                elem.addSetPropertyReq(setPropReq);

            } // wnilkamal@yahoo.com: Add feature to capture multiple string values instead of just a single string value. // ONLY SUPPORTED FOR EDGES ***
            else if (cmd.equalsIgnoreCase("setPropertyCollection") //|| cmd.equalsIgnoreCase("setPropertyCollectionAllCombos") || cmd.equalsIgnoreCase("setPropertyCollectionPairwise") -- THIS IS NOT GOING TO BE USED -- FUTURE ENHANCEMENT
                    ) {
                if (nKeyTokens<2||nKeyTokens>3||nValTokens<2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" ) : Expects (key,v1,v2,v3) - eg. value,a,b,c");
                    System.exit(1);
                    continue;
                }
                if (elem instanceof BranchGraphNode) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" ) : setPropertyCollection cannot be set on nodes, it can only be set on edges!");
                    System.exit(1);
                    continue;
                }

                String varAlias;
                if (nKeyTokens==2) {
                    varAlias = splitKey.get(0);
                } else {
                    varAlias = splitKey.get(1);
                }
                String propName = splitVal.get(0);

                // wnilkamal@yahoo.com: Iterate through all values to get list of value enumerations.
                Vector<SetPropertyRequirement> valueList = new Vector<SetPropertyRequirement>();
                for(String val : splitVal.subList(1, splitVal.size())){
                    // wnilkamal@yahoo.com: ESCAPE CHARACTER FOR the comma. All /, escaped commas will not be treated as a splitter.
                    String propVal = val.replaceAll("#escCHAR453", ",");
                    SetPropertyRequirement setPropReq = new SetPropertyRequirement(varAlias, propName, propVal);
                    if (nKeyTokens==3) {
                        setPropReq.setGroupAlias(splitKey.get(0));
                    }
                    valueList.add(setPropReq);
                }

                if (cmd.equalsIgnoreCase("setPropertyCollection")) {
                    retMultiValsetPropertyCollection.add(valueList);
                }

                /* THIS IS NOT GOING TO BE USED -- FUTURE ENHANCEMENT
                 else if (cmd.equalsIgnoreCase("setPropertyCollectionAllCombos")) {
                 retMultiValsetPropertyCollectionAllCombos.add(valueList);
                 }
                 else if (cmd.equalsIgnoreCase("setPropertyCollectionPairwise")) {
                 retMultiValsetPropertyCollectionPairwise.add(valueList);
                 }*/
            } else if (cmd.equalsIgnoreCase("checkProperty")) {
                if (nKeyTokens<2||nKeyTokens>3||nValTokens!=2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                    System.exit(1);
                    continue;
                }
                String varAlias;
                if (nKeyTokens==2) {
                    varAlias = splitKey.get(0);
                } else {
                    varAlias = splitKey.get(1);
                }
                String propName = splitVal.get(0);
                // wnilkamal@yahoo.com: ESCAPE CHARACTER FOR the comma. All /, escaped commas will not be treated as a splitter.
                String propVal = (splitVal.get(1)).replaceAll("#escCHAR453", ",");
                CheckPropertyRequirement checkPropReq = new CheckPropertyRequirement(varAlias, propName, propVal);
                if (nKeyTokens==3) {
                    checkPropReq.setGroupAlias(splitKey.get(0));
                }
                elem.addCheckPropertyReq(checkPropReq);
            } else if (cmd.equalsIgnoreCase("appendProperty")) {
                if (nKeyTokens<2||nKeyTokens>3||nValTokens!=2) {
                    LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                    System.exit(1);
                    continue;
                }
                String varAlias;
                if (nKeyTokens==2) {
                    varAlias = splitKey.get(0);
                } else {
                    varAlias = splitKey.get(1);
                }
                String propName = splitVal.get(0);
                // wnilkamal@yahoo.com: ESCAPE CHARACTER FOR the comma. All /, escaped commas will not be treated as a splitter.
                String propVal = (splitVal.get(1)).replaceAll("#escCHAR453", ",");
                AppendPropertyRequirement appendPropReq = new AppendPropertyRequirement(varAlias, propName, propVal);
                if (nKeyTokens==3) {
                    appendPropReq.setGroupAlias(splitKey.get(0));
                }
                elem.addAppendPropertyReq(appendPropReq);
            } else {
                LOG.error("Couldn't interpret annotation: ( "+key+" , "+value+" )");
                System.exit(1);
                continue;
            }
        }

        Vector<Vector<SetPropertyRequirement>>[] ret = new Vector[3];
        ret[CollectionPropertiesENUM.setPropertyCollection.getValue()] = retMultiValsetPropertyCollection;
        ret[CollectionPropertiesENUM.setPropertyCollectionAllCombos.getValue()] = retMultiValsetPropertyCollectionAllCombos;
        ret[CollectionPropertiesENUM.setPropertyCollectionPairwise.getValue()] = retMultiValsetPropertyCollectionPairwise;

		// wnilkamal@yahoo.com: Return an array of vectors of type <Vector<SetPropertyRequirement>> for all collection properties.
        // NOTE: all collection properties can be define for edges only.
        return ret;
    }

}
