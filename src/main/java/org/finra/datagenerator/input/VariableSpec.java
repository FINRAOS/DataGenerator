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
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;

/**
 * Represents a specification for a variable type. Name, group membership, and associated properties.
 *
 * @author ChamberA
 *
 */
public class VariableSpec {

    private static final Logger LOG = Logger.getLogger(VariableSpec.class);

    private final String name;
    private String group = AppConstants.DEFAULT_GROUP;
    private final Map<String, PropertySpec> propertySpecs = new HashMap<>();

    /**
     * Constructor requires a name.
     *
     * @param name
     */
    public VariableSpec(String name) {
        this.name = name;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void addPropertySpec(PropertySpec propertySpec) {
        propertySpecs.put(propertySpec.getName(), propertySpec);
    }

    public Collection<PropertySpec> getPropertySpecs() {
        return propertySpecs.values();
    }

    public PropertySpec getPropertySpec(String specName) {
        return propertySpecs.get(specName);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("name=")
                .append(this.name)
                .append(",")
                .append("group=")
                .append(this.group)
                .append(",")
                .append("propertySpecs={")
                .append(this.propertySpecs.toString())
                .append("}");
        return b.toString();
    }

}
