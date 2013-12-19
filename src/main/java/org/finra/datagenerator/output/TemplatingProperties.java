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
package org.finra.datagenerator.output;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.log4j.Logger;

public class TemplatingProperties {

    private static final Logger log = Logger.getLogger(TemplatingProperties.class);

    private static final Properties props = new Properties();

    /**
     * Loads the properties from a file.
     *
     * @param f
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void loadProperties(File f) throws FileNotFoundException, IOException {
        Preconditions.checkArgument(f.canRead(), "Can't read properties file " + f.getName());
        props.load(Files.newReader(f, Charsets.UTF_8));
    }

    /**
     * Returns a property given its name and a default value.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * Returns a map containing the properties whose names start with tools.
     *
     * @return
     */
    public static Map<String, String> getTools() {
        Map<String, String> toolsMap = new HashMap<>();
        // syntax for properties file is tools.<handlename>=<classname>
        for (Entry<Object, Object> prop : props.entrySet()) {
            String key = (String) prop.getKey();
            if (key.startsWith("tools.")) {
                toolsMap.put(key.substring(6), (String) prop.getValue());
            }
        }
        return toolsMap;
    }

}
