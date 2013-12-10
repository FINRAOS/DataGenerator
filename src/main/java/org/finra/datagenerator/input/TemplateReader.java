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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;




import com.google.common.base.Preconditions;

/**
 * This class reads in a list of template files, and returns them as a 
 * list of Template objects.
 * @author ChamberA
 *
 */
public class TemplateReader {

	private static Logger LOG = Logger.getLogger(TemplateReader.class);
	
	public List<Template> readTemplateFiles(Collection<File> files) {
		Preconditions.checkArgument(!files.isEmpty(), "No template files were provided to read.");
		
		List<Template> templates = new Vector<Template>();
		for (File f : files) {
			Template template = new Template(f);
			templates.add(template);
			LOG.info("Template: "+template.getFilename());
		}
		return templates;
	}
}
