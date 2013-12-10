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

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import org.finra.datagenerator.input.GroupSpec;
import org.finra.datagenerator.input.VariableSpec;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;

/**
 * Represents a group of variables within a dataset. Users will have acess to these in their templates
 * by calling $dataset.getGroupOfType("groupType") and iterating over their member variables.
 * 
 * @author ChamberA
 *
 */
public class DataSetGroup implements Serializable {

	/**
	 * Serializable ID
	 */
	private static final long serialVersionUID = 9007198894086306953L;
	
	private String type;
	private String alias;
	protected DataSetGroup parentGroup;

	// indices into this groups variables and child groups
	protected Multimap<String,DataSetGroup> childGroupsByType = LinkedHashMultimap.create();
	protected ConcurrentMap<String,DataSetGroup> childGroupsByAlias = new MapMaker().makeMap();
	protected Multimap<String,DataSetVariable> variablesByType = LinkedHashMultimap.create();
	protected ConcurrentMap<String,DataSetVariable> variablesByAlias = new MapMaker().makeMap();
	
	/**
	 * Creates data set group, initialized with it's variables set to default values.
	 * Parent group and child groups must be set externally.
	 * @param spec
	 */
	public DataSetGroup(GroupSpec groupSpec) {
		this.type = groupSpec.getName();
		this.alias = groupSpec.getName();
		// create it's variables with default values
		for (VariableSpec varSpec : groupSpec.getAllMemberVariableSpecs()) {
			this.addVariable(new DataSetVariable(varSpec));
		}
	}
	
	/**
	 * An empty data set group, only initiliazed by type. Useful for creating on-the-fly
	 * groups (i.e. when we don't have a GroupSpec)
	 * @param type
	 */
	public DataSetGroup(String type) {
		this.type = new String(type);
		this.alias = new String(type);
	}
	
	/**
	 * Copy constructor. WARNING references to other groups are shallow copies. This method is only intended
	 * to be used as part of the DataSet copy contructor, which will take care of updating these references.
	 * @param original
	 */
	protected DataSetGroup(DataSetGroup original) {
		this.type = original.getType();
		this.alias = original.getAlias();
		// deep copy of variables
		for (DataSetVariable originalVar : original.allVariables()) {
			DataSetVariable copiedVar = new DataSetVariable(originalVar);
			addVariable(copiedVar);
		}
		// shallow copy of group references, to be replaced later by DataSet() copy constructor
		childGroupsByAlias.putAll(original.childGroupsByAlias);
		childGroupsByType.putAll(original.childGroupsByType);
		parentGroup = original.parentGroup;
	}
	
	public void setParentGroup(DataSetGroup parentGroup) {
		this.parentGroup = parentGroup;
	}
	
	public void setAlias(String alias) {
		this.alias = new String(alias);
	}

	public String getType() { return new String(type); }
	public String getAlias() { return new String(alias); }

	protected void addVariable(DataSetVariable variable) {
		variablesByAlias.put(variable.getAlias(), variable);
		variablesByType.put(variable.getType(), variable);
	}
	
	public void addChildGroup(DataSetGroup group) {
		childGroupsByType.put(group.getType(),group);
		childGroupsByAlias.put(group.getAlias(), group);
	}
	
	/*
	 * The following functions should be known to the user for use in writing templates.
	 * They are analogs of the same method on DataSet.java
	 */
	
	public Collection<DataSetVariable> allVariables() {
		return variablesByType.values();
	}
	
	public DataSetVariable get(String varname) {
		return variablesByAlias.get(varname);
	}
	
	public Collection<DataSetVariable> getVariablesOfType(String variableType) {
		return variablesByType.get(variableType);
	}
	
	public DataSetGroup getChildGroup(String groupName) {
		return childGroupsByAlias.get(groupName);
	}
	
	public Collection<DataSetGroup> getChildGroupsOfType(String groupType) {
		return childGroupsByType.get(groupType);
	}
	
	
	public DataSetGroup getParentGroup() {
		return parentGroup;
	}
	
	public Collection<DataSetGroup> allChildGroups() {
		return childGroupsByType.values();
	}
	
}
