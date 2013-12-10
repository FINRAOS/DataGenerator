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

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a <shape/> element parsed from a .vdx file.
 * The Apache Digester returns these to VDXBranchGraphReader.
 * 
 * @author ChamberA
 *
 */
public class VisioShape {

	private static Logger LOG =Logger.getLogger(VisioShape.class);
	
	private int id;
	private String text = null;
	private Multimap<String,String> properties = HashMultimap.create();
	
	// getters
	public int getId() {return id; }
	public String getText() {return text; }
	public Multimap<String, String> getProperties() { return properties; }
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	
}
