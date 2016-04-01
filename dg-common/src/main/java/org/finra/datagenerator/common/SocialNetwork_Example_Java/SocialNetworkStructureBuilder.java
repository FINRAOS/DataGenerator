/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.common.SocialNetwork_Example_Java;

import org.finra.datagenerator.common.Graph.Graph;
import org.finra.datagenerator.common.GraphEngine.StructureBuilder;
import scala.NotImplementedError;
import scala.collection.Iterator;
import scala.collection.immutable.Vector;

import java.io.File;

/**
 * Builds all combinations of graph structures for friendships with a maximum graph size
 */
public final class SocialNetworkStructureBuilder extends StructureBuilder<User, UserTypeVal, UserStub, UserTypes> {
    // Defined as protected in the Scala abstract class, but for some reason it requires public on the Java impl.

    /**
     * Node data types
     * @return UserTypes
     */
    public UserTypes nodeDataTypes() {
        return UserTypes.getInstance();
    }

    private final String systemTempDir = System.getProperty("java.io.tmpdir").replace('\\', '/');
    private final String outDir = systemTempDir + (systemTempDir.endsWith("/") ? "" : "/") + "SocialNetworkGraphs/";

    private static final Boolean WRITE_STRUCTURES_IN_PARALLEL = false; // Same structure = same ID helps debugging.
    private static final Boolean ALSO_WRITE_AS_PNG = true;

    private static final SocialNetworkStructureBuilder INSTANCE = new SocialNetworkStructureBuilder();

    /**
     * Singleton instance
     * @return SocialNetworkStructureBuilder singleton
     */
    public static SocialNetworkStructureBuilder getInstance() {
        return INSTANCE;
    }

    private SocialNetworkStructureBuilder() {
        new File(outDir).mkdirs();
    }

    /**
     * Build all combinations of graph structures for generic event stubs of a maximum length
     * @param length Maximum number of nodes in each to generate
     * @return All graph combinations of specified length or less
     */
    public Vector<Graph<UserStub>> generateAllNodeDataTypeGraphCombinationsOfMaxLength(int length) {
        Vector<Graph<UserStub>> graphs = super.generateAllNodeDataTypeGraphCombinationsOfMaxLength(length);

        if (WRITE_STRUCTURES_IN_PARALLEL) {
            // Left as an exercise to the student.
            throw new NotImplementedError();
        } else {
            int i = 0;
            for (Iterator<Graph<UserStub>> iter = graphs.toIterator(); iter.hasNext();) {
                Graph<UserStub> graph = iter.next();
                graph.setGraphId("S_" + ++i + "_" + graph.allNodes().size());
                graph.writeDotFile(outDir + graph.graphId() + ".gv", false, ALSO_WRITE_AS_PNG);
            }
            System.out.println("Wrote " + i + " graph files in DOT format to " + outDir + "");
        }

        return graphs;
    }

}
