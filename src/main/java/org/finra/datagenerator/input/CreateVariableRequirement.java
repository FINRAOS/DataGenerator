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

import com.google.common.base.Objects;

public class CreateVariableRequirement implements Cloneable {

	private final String variableType;
	private String alias;
	private String groupAlias;
	
	// wnilkamal@yahoo.com: Adding clone function. Given primitives, shallow copy should suffice.
	public CreateVariableRequirement clone() throws CloneNotSupportedException {
		return (CreateVariableRequirement)super.clone();
	}
	
	
	public CreateVariableRequirement(String varType) {
		this.variableType = varType;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getGroupAlias() {
		return groupAlias;
	}

	public void setGroupAlias(String groupAlias) {
		this.groupAlias = groupAlias;
	}
	
	public String getVariableType() {
		return variableType;
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CreateVariableRequirement) {
			final CreateVariableRequirement other = (CreateVariableRequirement) obj;
			return Objects.equal(variableType, other.variableType)
					&& Objects.equal(alias, other.alias)
					&& Objects.equal(groupAlias, other.groupAlias);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(variableType, alias, groupAlias);
	}
	
}
