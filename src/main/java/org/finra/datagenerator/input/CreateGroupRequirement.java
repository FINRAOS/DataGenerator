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

public class CreateGroupRequirement {

	private final String groupType;
	private String alias;
	private String parentAlias;
	
	
	// wnilkamal@yahoo.com: Adding clone function. Given primitives, shallow copy should suffice.
	public CreateGroupRequirement clone() throws CloneNotSupportedException {
		return (CreateGroupRequirement)super.clone();
	}
	
	public CreateGroupRequirement(String groupType) {
		this.groupType = groupType;
	}

	public String getGroupType() {
		return groupType;
	}
	
	public String getParentAlias() {
		return parentAlias;
	}

	public void setParentAlias(String parentAlias) {
		this.parentAlias = parentAlias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	// overried hashcode and equals
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CreateGroupRequirement) {
			final CreateGroupRequirement other = (CreateGroupRequirement) obj;
			return Objects.equal(groupType, other.groupType)
					&& Objects.equal(alias, other.alias)
					&& Objects.equal(parentAlias, other.parentAlias);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(groupType, alias, parentAlias);
	}
}
