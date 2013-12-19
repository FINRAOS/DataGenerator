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
import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class BranchGraphEdge extends DefaultWeightedEdge implements IBranchGraphElement {

    private static Logger LOG = Logger.getLogger(BranchGraphEdge.class);

    private List<CreateVariableRequirement> createVarReqs = new LinkedList<CreateVariableRequirement>();
    private List<CreateGroupRequirement> createGroupReqs = new LinkedList<CreateGroupRequirement>();
    private List<SetPropertyRequirement> setPropReqs = new LinkedList<SetPropertyRequirement>();
    private List<CheckPropertyRequirement> checkPropReqs = new LinkedList<CheckPropertyRequirement>();
    private List<AppendPropertyRequirement> appendPropReqs = new LinkedList<AppendPropertyRequirement>();

    // wnilkamal@yahoo.com: Copy/clone function. Not overriding the object.clone function
    public BranchGraphEdge copy() {
        BranchGraphEdge ret = new BranchGraphEdge();
        try {
            for (CreateVariableRequirement val : createVarReqs) {
                ret.addCreateVariableReq(val.clone());
            }
            for (CreateGroupRequirement val : createGroupReqs) {
                ret.addCreateGroupReq(val.clone());
            }
            for (SetPropertyRequirement val : setPropReqs) {
                ret.addSetPropertyReq(val.clone());
            }
            for (CheckPropertyRequirement val : checkPropReqs) {
                ret.addCheckPropertyReq(val.clone());
            }
            for (AppendPropertyRequirement val : appendPropReqs) {
                ret.addAppendPropertyReq(val.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void addCreateVariableReq(CreateVariableRequirement req) {
        createVarReqs.add(req);
        new LinkedList<CreateVariableRequirement>();
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
}
