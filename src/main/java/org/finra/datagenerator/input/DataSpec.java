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

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import org.finra.datagenerator.AppConstants;

public class DataSpec {

    private ConcurrentMap<String, VariableSpec> varSpecs = new MapMaker().makeMap();
    private ConcurrentMap<String, GroupSpec> groupSpecs = new MapMaker().makeMap();

    // initialize with a default group, to which variable belong by default
    public DataSpec() {
        groupSpecs.put(AppConstants.DEFAULT_GROUP, new GroupSpec(AppConstants.DEFAULT_GROUP));
    }

    public void addVariableSpec(VariableSpec variableSpec) {
        Preconditions.checkNotNull(variableSpec.getName(), "Variable Spec must have a name");
        varSpecs.put(variableSpec.getName(), variableSpec);
    }

    public void addGroupSpec(GroupSpec groupSpec) {
        Preconditions.checkNotNull(groupSpec.getName(), "Group spec must have a name");
        groupSpecs.put(groupSpec.getName(), groupSpec);
    }

    public GroupSpec getGroupSpec(String groupType) {
        Preconditions.checkArgument(groupSpecs.containsKey(groupType), "No group spec named "+groupType);
        return groupSpecs.get(groupType);
    }

    public Collection<GroupSpec> getAllGroupSpecs() {
        return groupSpecs.values();
    }

    public VariableSpec getVariableSpec(String variableType) {
        Preconditions.checkArgument(varSpecs.containsKey(variableType), "No variable spec named "+variableType);
        return varSpecs.get(variableType);
    }

    public Collection<VariableSpec> getAllVariableSpecs() {
        return varSpecs.values();
    }

	//TODO: lets override equals() and hascode().
}
