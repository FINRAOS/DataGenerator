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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import org.apache.log4j.Logger;
import org.finra.datagenerator.output.TemplatingProperties;

/**
 * This class represent a single template (regular or global). It's just a
 * wrapper around a file, with methods for getting the filename and a Reader.
 *
 * @author ChamberA
 *
 */
public class Template {

    private static Logger LOG = Logger.getLogger(Template.class);

    private final File file;

    public Template(File file) {
        Preconditions.checkArgument(file.canRead(), "Cannot read file: " + file.getPath());
        this.file = file;
    }

    public String getFilename() {
        return file.getName();
    }

    public Reader getFileReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    public boolean isGlobal() {
        return TemplatingProperties.getProperty(getFilename() + ".isGlobal", "false").equalsIgnoreCase("true");
    }

}
