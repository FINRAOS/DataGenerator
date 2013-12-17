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

/**
 * This interface is implemented by BranchGraphNode and BranchGraphEdge. Both of these graph elements can be annotated
 * with requirements.
 *
 * @author ChamberA
 *
 */
public interface IBranchGraphElement {

    public void addCreateVariableReq(CreateVariableRequirement req);

    public void addCreateGroupReq(CreateGroupRequirement req);

    public void addSetPropertyReq(SetPropertyRequirement req);

    public void addCheckPropertyReq(CheckPropertyRequirement req);

    public void addAppendPropertyReq(AppendPropertyRequirement req);

    public Collection<CreateVariableRequirement> getCreateVariableReqs();

    public Collection<CreateGroupRequirement> getCreateGroupReqs();

    public Collection<SetPropertyRequirement> getSetPropertyReqs();

    public Collection<CheckPropertyRequirement> getCheckPropertyReqs();

    public Collection<AppendPropertyRequirement> getAppendPropertyReqs();
}
