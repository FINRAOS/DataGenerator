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

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.finra.datagenerator.input.AppendPropertyRequirement;
import org.finra.datagenerator.input.BranchGraph;
import org.finra.datagenerator.input.BranchGraphEdge;
import org.finra.datagenerator.input.CheckPropertyRequirement;
import org.finra.datagenerator.input.CreateGroupRequirement;
import org.finra.datagenerator.input.CreateVariableRequirement;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.IBranchGraphElement;
import org.finra.datagenerator.input.SetPropertyRequirement;
import org.finra.datagenerator.input.VariableSpec;

public abstract class AbstractBranchDataSetGenerator implements IBranchDataSetGenerator {

    private static final Logger LOG = Logger.getLogger(AbstractBranchDataSetGenerator.class);

    /**
     * Converts a path of edges to a path of nodes + edges. The input list
     * remains unaltered.
     *
     * @param edgePath
     * @param graph
     * @return
     */
    protected List<IBranchGraphElement> insertNodes(List<BranchGraphEdge> edgePath, BranchGraph graph) {
        List<IBranchGraphElement> pathWithNodes = new LinkedList<IBranchGraphElement>();
        // insert the first node
        pathWithNodes.add(graph.getEdgeSource(edgePath.get(0)));
        // the rest follow in a loop
        for (BranchGraphEdge edge : edgePath) {
            pathWithNodes.add(edge);
            pathWithNodes.add(graph.getEdgeTarget(edge));
        }
        return pathWithNodes;
    }

    protected DataSet dataSetFromPath(List<IBranchGraphElement> path, DataSpec dataSpec) throws IllegalStateException {
        DataSet dataSet = new DataSet();
        for (IBranchGraphElement elem : path) {
            // checkProperties requirements
            for (CheckPropertyRequirement checkPropReq : elem.getCheckPropertyReqs()) {
                String varAlias = checkPropReq.getVariableAlias();
                String groupAlias = checkPropReq.getGroupAlias();
                DataSetVariable var;
                if (groupAlias == null) {
                    var = dataSet.get(varAlias);
                } else {
                    var = dataSet.getGroup(groupAlias).get(varAlias);
                }
                String requiredVal = checkPropReq.getRequiredValue();
                String actualVal = var.get(checkPropReq.getProperty());
                if (!requiredVal.equals(actualVal)) {
                    throw new IllegalStateException("Required: " + requiredVal + " Actual: " + actualVal);
                }
            }
            // create group requirements that do not specify a parent group
            for (CreateGroupRequirement createGroupReq : elem.getCreateGroupReqs()) {
                if (createGroupReq.getParentAlias() != null) {
                    continue;
                }
                String groupType = createGroupReq.getGroupType();
                String alias = createGroupReq.getAlias();
                if (alias != null) {
                    dataSet.createGroup(dataSpec.getGroupSpec(groupType), alias);
                } else {
                    dataSet.createGroup(dataSpec.getGroupSpec(groupType));
                }
            }
            // create group requirements that do specify a parent group
            for (CreateGroupRequirement createGroupReq : elem.getCreateGroupReqs()) {
                if (createGroupReq.getParentAlias() == null) {
                    continue;
                }
                String groupType = createGroupReq.getGroupType();
                String parentGroupAlias = createGroupReq.getParentAlias();
                String alias = createGroupReq.getAlias();
                if (alias != null) {
                    dataSet.createGroup(dataSpec.getGroupSpec(groupType), dataSet.getGroup(parentGroupAlias), alias);
                } else {
                    dataSet.createGroup(dataSpec.getGroupSpec(groupType), dataSet.getGroup(parentGroupAlias));
                }
            }
            // create variable requirements
            for (CreateVariableRequirement createVarReq : elem.getCreateVariableReqs()) {
                String varType = createVarReq.getVariableType();
                String alias = createVarReq.getAlias();
                String groupAlias = createVarReq.getGroupAlias();
                VariableSpec varSpec = dataSpec.getVariableSpec(varType);
                if (groupAlias == null && alias == null) {
                    dataSet.createVariable(varSpec);
                } else if (groupAlias == null && alias != null) {
                    dataSet.createVariable(varSpec, alias);
                } else if (groupAlias != null && alias == null) {
                    dataSet.getGroup(groupAlias).addVariable(dataSet.createVariable(varSpec));
                } else if (groupAlias != null && alias != null) {
                    dataSet.getGroup(groupAlias).addVariable(dataSet.createVariable(varSpec, alias));
                }
            }
            // set property requirements
            for (SetPropertyRequirement setPropReq : elem.getSetPropertyReqs()) {
                String varAlias = setPropReq.getVariableAlias();
                String groupAlias = setPropReq.getGroupAlias();
                DataSetVariable var;
                if (groupAlias == null) {
                    var = dataSet.get(varAlias);
                } else {
                    var = dataSet.getGroup(groupAlias).get(varAlias);
                }

                String prop = setPropReq.getProperty();
                String val = setPropReq.getValue();

                if (var == null) {
                    throw new IllegalStateException("Variable " + varAlias + " undefined for dataSet");
                }

                if (prop == null) {
                    throw new IllegalStateException("Property " + prop + " undefined for variable " + varAlias);
                }

                if (val == null) {
                    throw new IllegalStateException("Missing value in setProperty statement for variable " + varAlias);
                }

                var.setProperty(prop, val);
            }
            // append property requirements
            for (AppendPropertyRequirement appendPropReq : elem.getAppendPropertyReqs()) {
                String varAlias = appendPropReq.getVariableAlias();
                String groupAlias = appendPropReq.getGroupAlias();
                DataSetVariable var;
                if (groupAlias == null) {
                    var = dataSet.get(varAlias);
                } else {
                    var = dataSet.getGroup(groupAlias).get(varAlias);
                }
                var.appendProperty(appendPropReq.getProperty(), appendPropReq.getValue());
            }
        }
        return dataSet;
    }

}
