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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySpec {

    private final String name;
    private final List<String> positiveValues = new ArrayList<>();
    private final List<String> negativeValues = new ArrayList<>();
    private String defaultValue = null;

    public PropertySpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getPositiveValues() {
        return (ArrayList<String>) ((ArrayList<String>) positiveValues).clone();
    }

    public List<String> getNegativeValues() {
        return (ArrayList<String>) ((ArrayList<String>) negativeValues).clone();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public PropertySpec addPositiveValue(String posVal) {
        positiveValues.add(posVal);
        // the first pos val is taken to be default
        if (positiveValues.size() == 1) {
            defaultValue = posVal;
        }
        return this;
    }

    public PropertySpec addNegativeValue(String negVal) {
        negativeValues.add(negVal);
        return this;
    }

    @Override
    public String toString() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", this.name);
        map.put("defaultValue", this.defaultValue);
        map.put("positiveValues", this.positiveValues.toString());
        map.put("negativeValues", this.negativeValues.toString());

        return map.toString();
    }

}
