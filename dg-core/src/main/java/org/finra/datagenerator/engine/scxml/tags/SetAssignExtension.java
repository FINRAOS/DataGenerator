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

package org.finra.datagenerator.engine.scxml.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;

/**
 * Marshall Peters
 * Date: 11/7/14
 */
public class SetAssignExtension implements CustomTagExtension<SetAssignExtension.SetAssignTag> {
    private static final Logger log = Logger.getLogger(SetAssignExtension.class);

    private static Map<String, Set<String>> cachedDataFiles = new HashMap<String, Set<String>>();

    public Class<SetAssignTag> getTagActionClass() {
        return SetAssignTag.class;
    }

    public String getTagName() {
        return "assign";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Performs variable assignments from a set of values
     *
     * @param action a SetAssignTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(SetAssignTag action,
                                                            List<Map<String, String>> possibleStateList) {
        String variable = action.getName();
        String set = action.getSet();
        String range = action.getRange();
        String fileName = action.getFileName();
        String fileSeparator = action.getFileSeparator();

        Set<String> domain = new HashSet<String>();
        if (null != set) {
            for (String value : set.split(",")) {
                domain.add(value);
            }
        }
        if (null != range && range.length() > 0) {
            generateRangeValues(domain, range);
        }
        if (null != fileName && fileName.length() > 0) {
            generateFileValues(domain, fileName, fileSeparator);
        }

        //take the product
        List<Map<String, String>> productTemp = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (String value : domain) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, value);
                productTemp.add(n);
            }
        }

        return productTemp;
    }

    private void generateRangeValues(Set<String> domain, String range) {
        String[] rangeSplitted = range.split(":");
        if (2 == rangeSplitted.length || 3 == rangeSplitted.length) {
            float currentValue = Float.valueOf(rangeSplitted[0]);
            float lastValue = Float.valueOf(rangeSplitted[1]);
            float step;
            if (3 == rangeSplitted.length) {
                step = Float.valueOf(rangeSplitted[2]);
            } else {
                step = 1;
            }
            
            if ((lastValue > currentValue && step > 0) || (lastValue < currentValue && step < 0)) {
                while (currentValue <= lastValue) {
                    if (currentValue == (int) currentValue) {
                        domain.add(String.valueOf((int) currentValue));
                    } else if (currentValue == (long) currentValue) {
                        domain.add(String.valueOf((long) currentValue));
                    } else {
                        domain.add(String.valueOf(currentValue));
                    }
                    currentValue += step;
                }
            } else {
                log.error("Oops! Not valid 'range' parameters. "
                        + "It's not possible to come from '" + currentValue + "' to '" + lastValue + "' with '"  + step + "' step!");
            }
        } else {
            log.error("Oops! Can't parse 'range' parameter ('" + range + "'). "
                    + "It has be in '<first float value>:<last float value>[:<optional step value. Default value is '1.0'>]' format. "
                    + "For example, '0.1:2:0.4' or '2:18'");
        }
    }

    private void generateFileValues(Set<String> domain, String fileName, String fileSeparator) {
        if (null != fileName && !fileName.equals("") && null != fileSeparator && !fileSeparator.equals("")) {
            // check cache with data file's data, maybe this file already cached
            Set<String> data;
            if (cachedDataFiles.containsKey(fileName)) {
                domain.addAll(cachedDataFiles.get(fileName));
            } else {
                data = cacheAndReturnDataFile(fileName, fileSeparator);
                if (null != data) {
                    domain.addAll(data);
                }
            }
        }
    }

    private Set<String> cacheAndReturnDataFile(String fileName, String fileSeparator) {
        InputStream inputStream = SetAssignExtension.class.getResourceAsStream("/" + fileName);
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line;
                List<String> lines = new ArrayList<String>();
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                
                Set<String> data = new HashSet<String>();
                if (1 == lines.size()) {
                    if (null == fileSeparator || fileSeparator.equals("")) {
                        fileSeparator = ",";
                    }
                    String[] lineSplitted = lines.get(0).split(fileSeparator);
                    for (String value : lineSplitted) {
                        data.add(value);
                    }
                } else {
                    for (String currentLine : lines) {
                        if (!currentLine.startsWith("#")) {
                            data.add(currentLine);
                        }
                    }
                }
                cachedDataFiles.put(fileName, data);
                return data;
            } catch (IOException e) {
                log.error("Oops! Can't read '" + fileName + "' file... " + e);
            }
        }
        return null;
    }

    /**
     * A custom Action for the 'dg:assign' tag inside models
     */
    public static class SetAssignTag extends Action {
        private String name;
        private String set;
        private String range;
        private String fileName;
        private String fileSeparator;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSet() {
            return set;
        }

        public void setSet(String set) {
            this.set = set;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSeparator() {
            return fileSeparator;
        }

        public void setFileSeparator(String fileSeparator) {
            this.fileSeparator = fileSeparator;
        }

        /**
         * Required implementation of an abstract method in Action
         *
         * @param eventDispatcher unused
         * @param errorReporter   unused
         * @param scInstance      unused
         * @param log             unused
         * @param collection      unused
         * @throws ModelException           never
         * @throws SCXMLExpressionException never
         */
        public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance,
                            Log log, Collection collection) throws ModelException, SCXMLExpressionException {
            //Handled manually
        }
    }

}
