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

/**
 * Represent a <connect/> element parsed from a .vdx file. The Apache Digester returns these to VDXBranchGraphReader.
 *
 * @author ChamberA
 *
 */
public class VisioConnect {

    private String fromCell; // "BeginX" means connection origin "EndX" means terminus (from vdx schema)
    private int shapeId;
    private int connectedToId;

    // getters
    public String getFromCell() {
        return fromCell;
    }

    public int getShapeId() {
        return shapeId;
    }

    public int getConnectedToId() {
        return connectedToId;
    }

    public void setFromCell(String fromCell) {
        this.fromCell = fromCell;
    }

    public void setShapeId(int shapeId) {
        this.shapeId = shapeId;
    }

    public void setConnectedToId(int connectedToId) {
        this.connectedToId = connectedToId;
    }

    public boolean isOrigin() {
        return fromCell.equals("BeginX");
    }
}
