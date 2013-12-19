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

/**
 * A collection of info describing a check value requirement for use in the
 * branch graph.
 *
 * @author ChamberA
 *
 */
public class CheckPropertyRequirement {

    private final String variableAlias;
    private final String value;
    private final String property;
    private String groupAlias;

    // wnilkamal@yahoo.com: Adding clone function. Given primitives, shallow copy should suffice.
    public CheckPropertyRequirement clone() throws CloneNotSupportedException {
        return (CheckPropertyRequirement) super.clone();
    }

    public CheckPropertyRequirement(String variableAlias, String property, String value) {
        this.variableAlias = variableAlias;
        this.property = property;
        this.value = value;
    }

    public String getVariableAlias() {
        return variableAlias;
    }

    public String getGroupAlias() {
        return groupAlias;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public String getProperty() {
        return property;
    }

    public String getRequiredValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CheckPropertyRequirement) {
            final CheckPropertyRequirement other = (CheckPropertyRequirement) obj;
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
