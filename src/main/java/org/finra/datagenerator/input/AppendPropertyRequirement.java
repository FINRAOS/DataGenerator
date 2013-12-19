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

public class AppendPropertyRequirement {

    private final String variableAlias;
    private final String value;
    private String groupAlias;
    private final String property;

    // wnilkamal@yahoo.com: Adding clone function. Given primitives, shallow copy should suffice.
    public AppendPropertyRequirement clone() throws CloneNotSupportedException {
        return (AppendPropertyRequirement) super.clone();
    }

    public AppendPropertyRequirement(String variableAlias, String property, String value) {
        this.variableAlias = variableAlias;
        this.property = property;
        this.value = value;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public String getVariableAlias() {
        return variableAlias;
    }

    public String getGroupAlias() {
        return groupAlias;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AppendPropertyRequirement) {
            final AppendPropertyRequirement other = (AppendPropertyRequirement) obj;
            return Objects.equal(variableAlias, other.variableAlias)
                    && Objects.equal(value, other.value)
                    && Objects.equal(groupAlias, other.groupAlias)
                    && Objects.equal(property, other.property);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variableAlias, value, groupAlias, property);
    }
}
