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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.finra.datagenerator.input.PropertySpec;
import org.finra.datagenerator.input.VariableSpec;

import com.google.common.collect.MapMaker;

public class DataSetVariable implements Serializable {

	/**
	 * Serialized version ID
	 */
	private static final long serialVersionUID = -8495517303468675174L;

	private static Logger LOG =Logger.getLogger(DataSetVariable.class);
	
	private final String type;
	private final String alias;
	private Map<String,String> properties = new MapMaker().makeMap();
	
	/**
	 * Creates a default initialized variable for the dataset
	 * @param spec
	 */
	public DataSetVariable(VariableSpec spec) {
		type = spec.getName();
		alias = spec.getName();
		for (PropertySpec propSpec : spec.getPropertySpecs() ) {
			properties.put(propSpec.getName(), propSpec.getDefaultValue());
		}
	}
	
	/**
	 * Default initialized per spec, + given an alias
	 * @param spec
	 * @param alias
	 */
	public DataSetVariable(VariableSpec spec, String alias) {
		type = spec.getName();
		this.alias = alias;
		for (PropertySpec propSpec : spec.getPropertySpecs() ) {
			properties.put(propSpec.getName(), propSpec.getDefaultValue());
		}
	}
	
	/**
	 * Deep copy constructor
	 * @param original
	 */
	protected DataSetVariable(DataSetVariable original) {
		type = new String (original.type);
		alias = new String (original.alias);
		for (Entry<String,String> prop : original.properties.entrySet()) {
			properties.put(new String(prop.getKey()), new String (prop.getValue()));
		}
	}
	
	public String getType() {return type;}
	public String getAlias() {return alias;}
	
	protected void setProperty(String prop, String value) {
		properties.put(prop, new String(value));
	}
	//This is only used without a dataspec
	protected void appendProperty(String prop, String value){
		if (properties.get(prop).contains("$EMPTY$")){
			properties.put(prop, value);
		}else{
			properties.put(prop, properties.get(prop).concat(";" + value));
		}
	}
	
	/**
	 * This is what is actually called from the Velocity templates by $dataset.var.prop
	 */
	public String get(String prop) {
		return properties.get(prop);
	}
}
