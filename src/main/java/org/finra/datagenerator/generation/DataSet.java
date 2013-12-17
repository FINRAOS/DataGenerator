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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.input.GroupSpec;
import org.finra.datagenerator.input.VariableSpec;

public class DataSet implements Serializable {

    /**
     * Serialized ID for versioning of serialization - change if old objects become unusable
     */
    private static final long serialVersionUID = 2925168185791074578L;

    private static final Logger log = Logger.getLogger(DataSet.class);

    // indices into the dataset groups and variables
    private final Multimap<String, DataSetGroup> groupsByType = LinkedHashMultimap.create();
    private final Multimap<String, DataSetVariable> variablesByType = LinkedHashMultimap.create();
    private final ConcurrentHashMap<String, DataSetGroup> groupsByAlias = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DataSetVariable> variablesByAlias = new ConcurrentHashMap<>();

    /**
     * Initializes the dataset by creating the root variable group.
     */
    public DataSet() {
        DataSetGroup rootGroup = new DataSetGroup(new GroupSpec(AppConstants.ROOT_GROUP)); // TODO no need to construct a groupspec
        indexGroup(rootGroup);
    }

    /**
     * Copy constructor. Deep copies all DataSetGroups and DataSetVariables to produce an independent DataSet
     *
     * @param original
     */
    public DataSet(DataSet original) {
        // clone and add each group from the original. Also keep a map of new -> original groups so we can update references.
        Map<DataSetGroup, DataSetGroup> replacementMap = new HashMap<>();
        for(DataSetGroup origGroup : original.groupsByType.values()){
            DataSetGroup copyGroup = new DataSetGroup(origGroup);
            indexGroup(copyGroup);
            replacementMap.put(origGroup, copyGroup);
        }
        // update references based on the replacementMap
        for(DataSetGroup copiedGroup : groupsByType.values()){
            // update it's children
            Multimap<String, DataSetGroup> updatedChildGroupsByType = LinkedHashMultimap.create();
            for(Entry<String, DataSetGroup> origEntry : copiedGroup.childGroupsByType.entries()){
                updatedChildGroupsByType.put(origEntry.getKey(), replacementMap.get(origEntry.getValue()));
            }
            copiedGroup.childGroupsByType = updatedChildGroupsByType;
            ConcurrentMap<String, DataSetGroup> updatedChildGroupsByAlias = new MapMaker().makeMap();
            for(Entry<String, DataSetGroup> origEntry : copiedGroup.childGroupsByAlias.entrySet()){
                updatedChildGroupsByAlias.put(origEntry.getKey(), replacementMap.get(origEntry.getValue()));
            }
            copiedGroup.childGroupsByAlias = updatedChildGroupsByAlias;
            // updates it's parent
            copiedGroup.setParentGroup(replacementMap.get(copiedGroup.parentGroup));
        }
    }

    private void indexGroup(DataSetGroup group) {
        groupsByType.put(group.getType(), group);
        groupsByAlias.put(group.getAlias(), group);
        for(DataSetVariable var : group.allVariables()){
            indexVariable(var);
        }
    }

    private void indexVariable(DataSetVariable variable) {
        variablesByAlias.put(variable.getAlias(), variable);
        variablesByType.put(variable.getType(), variable);
    }

    /**
     * Creates a new DataSet group and set it's parent to the given parent group. All variables within the group will be
     * given default values.
     *
     * @param groupSpec
     * @param parentGroup
     * @return
     */
    public DataSetGroup createGroup(GroupSpec groupSpec, DataSetGroup parentGroup) {
        DataSetGroup newGroup = new DataSetGroup(groupSpec); // create new group based on spec
        newGroup.setParentGroup(parentGroup); // give it a handle to it's parent
        parentGroup.addChildGroup(newGroup); // add it to the parent groups index
        indexGroup(newGroup); // add it to the dataset index
        return newGroup;
    }

    // Create group with a parent group and an alias
    public DataSetGroup createGroup(GroupSpec groupSpec, DataSetGroup parentGroup, String alias) {
        DataSetGroup newGroup = new DataSetGroup(groupSpec); // create new group based on spec
        newGroup.setAlias(alias);
        newGroup.setParentGroup(parentGroup); // give it a handle to it's parent
        parentGroup.addChildGroup(newGroup); // add it to the parent groups index
        indexGroup(newGroup); // add it to the dataset index
        return newGroup;
    }

    // Create a group without a parent group (added to default group)
    public DataSetGroup createGroup(GroupSpec groupSpec) {
        // make a default group if there isn't one
        if (!groupsByAlias.containsKey(AppConstants.DEFAULT_GROUP)) {
            DataSetGroup defGroup = new DataSetGroup(AppConstants.DEFAULT_GROUP);
            indexGroup(defGroup);
        }
        return createGroup(groupSpec, groupsByAlias.get(AppConstants.DEFAULT_GROUP));
    }

    // Create a group without a parent group, and with an alias
    public DataSetGroup createGroup(GroupSpec groupSpec, String alias) {
        // make a default group if there isn't one
        if (!groupsByAlias.containsKey(AppConstants.DEFAULT_GROUP)) {
            DataSetGroup defGroup = new DataSetGroup(AppConstants.DEFAULT_GROUP);
            indexGroup(defGroup);
        }
        return createGroup(groupSpec, groupsByAlias.get(AppConstants.DEFAULT_GROUP), alias);
    }

    /**
     * Creates a new default initialized variable based on a variable spec. The variable is associated with the default
     * group (i.e. not part of any specific group)
     *
     * @param varSpec
     * @return
     */
    public DataSetVariable createVariable(VariableSpec varSpec) {
        // make a default group if there isn't one
        if (!groupsByAlias.containsKey(AppConstants.DEFAULT_GROUP)) {
            DataSetGroup defGroup = new DataSetGroup(AppConstants.DEFAULT_GROUP);
            indexGroup(defGroup);
        }
        // make the new variable, and associate it with the default group
        DataSetVariable newVar = new DataSetVariable(varSpec);
        groupsByAlias.get(AppConstants.DEFAULT_GROUP).addVariable(newVar);
        indexVariable(newVar);
        return newVar;
    }

    //same as above but with an alias
    public DataSetVariable createVariable(VariableSpec varSpec, String alias) {
        // make a default group if there isn't one
        if (!groupsByAlias.containsKey(AppConstants.DEFAULT_GROUP)) {
            DataSetGroup defGroup = new DataSetGroup(AppConstants.DEFAULT_GROUP);
            indexGroup(defGroup);
        }
        // make the new variable, and associate it with the default group
        DataSetVariable newVar = new DataSetVariable(varSpec, alias);
        groupsByAlias.get(AppConstants.DEFAULT_GROUP).addVariable(newVar);
        indexVariable(newVar);
        return newVar;
    }

    /*
     * The following functions should be known to the user for use in writing templates.
     */
    /**
     * This is what is actually called from a Velocity template when user types $dataset.varname
     *
     * @param variableName
     * @return
     */
    public DataSetVariable get(String variableName) {
        return variablesByAlias.get(variableName);
    }

    public Collection<DataSetVariable> getVariablesOfType(String variableType) {
        return variablesByType.get(variableType);
    }

    public DataSetGroup getGroup(String groupName) {
        return groupsByAlias.get(groupName);
    }

    public Collection<DataSetGroup> getGroupsOfType(String groupType) {
        return groupsByType.get(groupType);
    }

    public Collection<DataSetVariable> allVariables() {
        return variablesByType.values();
    }

    public void debug() {
        log.debug("Debugging dataset: "+this.hashCode());
        log.debug("The variables by type:");
        log.debug("Count = "+variablesByType.size());
        for(Entry<String, DataSetVariable> e : variablesByType.entries()){
            log.debug(e.getKey()+" "+e.getValue().hashCode());
        }
        log.debug("The variables by alias:");
        log.debug("Count = "+variablesByAlias.size());
        for(Entry<String, DataSetVariable> e : variablesByAlias.entrySet()){
            log.debug(e.getKey()+" "+e.getValue().hashCode());
        }
        log.debug("The groups by type:");
        log.debug("Count = "+groupsByType.size());
        for(Entry<String, DataSetGroup> e : groupsByType.entries()){
            log.info(e.getKey()+" "+e.getValue().hashCode());
        }
        log.debug("The groups by alias:");
        log.debug("Count = "+groupsByAlias.size());
        for(Entry<String, DataSetGroup> e : groupsByAlias.entrySet()){
            log.debug(e.getKey()+" "+e.getValue().hashCode());
        }

        log.debug("Iterating though groups by type and printing their variables");
        for(DataSetGroup group : groupsByType.values()){
            log.debug("Group: "+group.hashCode()+" "+group.getType());
            log.debug("it's variables by type:");
            for(Entry<String, DataSetVariable> e : group.variablesByType.entries()){
                log.debug(e.getKey()+" "+e.getValue().hashCode());
            }
            log.debug("it's variables by alias:");
            for(Entry<String, DataSetVariable> e : group.variablesByAlias.entrySet()){
                log.debug(e.getKey()+" "+e.getValue().hashCode());
            }
            log.debug("and it has the following child groups by type:");
            for(Entry<String, DataSetGroup> e : group.childGroupsByType.entries()){
                log.debug(e.getKey()+" "+e.getValue().hashCode());
            }
            log.debug("and it has the following child groups by alias:");
            for(Entry<String, DataSetGroup> e : group.childGroupsByAlias.entrySet()){
                log.debug(e.getKey()+" "+e.getValue().hashCode());
            }
        }

    }

}
