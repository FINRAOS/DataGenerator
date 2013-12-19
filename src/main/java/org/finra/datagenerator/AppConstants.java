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
package org.finra.datagenerator;

/**
 * Wrapper for application constants.
 *
 * TODO: Use System parameters to read those variables, with default settings
 */
public class AppConstants {

    /**
     * The file extensions that will be recognized as templates
     */
    public static final String[] TEMPLATE_EXTENSIONS = {"txt", "csv", "sql", "xml", "html", "java", "xls"};

    /**
     * The root group is the top of the group hierarchy in a dataset
     */
    public static final String ROOT_GROUP = "ROOT_GROUP";
    /**
     * All variable not explicitly assigned to a group fall into the default
     * group
     */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    /**
     * the root state that is inserted into a branch graph
     */
    public static final String ROOT_NODE = "ROOT_NODE";

    /**
     * The default output directory
     */
    public static final String DEFAULT_OUT_DIRNAME = "default";
    public static final String ISOPOS_OUT_DIRNAME = "isoPos";
    public static final String ISONEG_OUT_DIRNAME = "isoNeg";
    public static final String PAIRWISE_OUT_DIRNAME = "pairwise";
    public static final String ALLCOMBOS_OUT_DIRNAME = "allCombos";
    public static final String ALLPATHS_OUT_DIRNAME = "allPaths";
    public static final String ALLEDGES_OUT_DIRNAME = "allEdges";
    public static final String GLOBAL_OUT_DIRNAME = "global";

    // handles that will be stored in velocity contexts. users need to know these.
    public static final String DATASET = "dataset";
    public static final String DATASET_CNTR = "datasetCounter";
    public static final String ALL_DATASETS = "allDatasets"; // for use in global templates
    public static final String KEY_GROUP = "keyGroup";

    // iso-specific handles stored in velocity context (users of isoNeg/isoPos need to know these)
    public static final String ISO_VARIABLE = "isolatedVariable";
    public static final String ISO_CLASS = "isolatedClassId";

    // this is the special property of a variable that will be used for generating combinations.
    public static final String VALUE = "value"; // case insensitive.
}
