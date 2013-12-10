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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class BranchGraphNode implements IBranchGraphElement {

	private static Logger LOG =Logger.getLogger(BranchGraphNode.class);
	
	private final String text;
	private List<CreateVariableRequirement> createVarReqs = new LinkedList<CreateVariableRequirement>();
	private List<CreateGroupRequirement> createGroupReqs = new LinkedList<CreateGroupRequirement>();
	private List<SetPropertyRequirement> setPropReqs = new LinkedList<SetPropertyRequirement>();
	private List<CheckPropertyRequirement> checkPropReqs = new LinkedList<CheckPropertyRequirement>();
	private List<AppendPropertyRequirement> appendPropReqs = new LinkedList<AppendPropertyRequirement>();
	
	public BranchGraphNode(String text) {
		this.text = text;
	}
		
	public void addCreateVariableReq(CreateVariableRequirement req) {
		createVarReqs.add(req);
	}
	
	public void addCreateGroupReq(CreateGroupRequirement req) {
		createGroupReqs.add(req);
	}
	
	public void addSetPropertyReq(SetPropertyRequirement req) {
		setPropReqs.add(req);
	}
	
	public void addCheckPropertyReq(CheckPropertyRequirement req) {
		checkPropReqs.add(req);
	}

	public void addAppendPropertyReq(AppendPropertyRequirement req) {
		appendPropReqs.add(req);
	}
	public Collection<CreateVariableRequirement> getCreateVariableReqs() {
		return createVarReqs;
	}

	public Collection<CreateGroupRequirement> getCreateGroupReqs() {
		return createGroupReqs;
	}

	public Collection<SetPropertyRequirement> getSetPropertyReqs() {
		return setPropReqs;
	}

	public Collection<CheckPropertyRequirement> getCheckPropertyReqs() {
		return checkPropReqs;
	}

	public Collection<AppendPropertyRequirement> getAppendPropertyReqs() {
		return appendPropReqs;
	}
	
	public void mergeRequirements(BranchGraphNode otherNode) {
		for (CreateVariableRequirement req : otherNode.getCreateVariableReqs()) {
			if (!createVarReqs.contains(req))
				createVarReqs.add(req);
		}
		for (CreateGroupRequirement req : otherNode.getCreateGroupReqs()) {
			if (!createGroupReqs.contains(req))
				createGroupReqs.add(req);
		}
		for (SetPropertyRequirement req : otherNode.getSetPropertyReqs()) {
			if (!setPropReqs.contains(req))
				setPropReqs.add(req);
		}
		for (CheckPropertyRequirement req : otherNode.getCheckPropertyReqs()) {
			if (!checkPropReqs.contains(req))
				checkPropReqs.add(req);
		}
		for (AppendPropertyRequirement req : otherNode.getAppendPropertyReqs()) {
			if (!appendPropReqs.contains(req))
				appendPropReqs.add(req);
		}
	}

	// hashcode and equals are overriden so JGraphT
	// will consider nodes identical iff they have the same text
	@Override
	public int hashCode() {
		return text.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof BranchGraphNode))
			return false;
		BranchGraphNode otherNode = (BranchGraphNode) obj;
		return this.text.equals(otherNode.text);
	}
	
	@Override
	public String toString() {
		return text;
	}


}
