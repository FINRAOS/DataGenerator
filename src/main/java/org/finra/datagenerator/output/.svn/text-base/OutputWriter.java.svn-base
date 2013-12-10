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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.PropertiesFactoryConfiguration;
import org.finra.datagenerator.AppConstants;
import org.finra.datagenerator.generation.DataSet;
import org.finra.datagenerator.generation.DataSetGroup;
import org.finra.datagenerator.input.Template;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;

public class OutputWriter implements Callable<File>{

	private static Logger LOG = Logger.getLogger(OutputWriter.class);

	// initialize Velocity
	static {
		Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		Velocity.setProperty("runtime.log.logsystem.log4j.logger", "VelocityLogging");
		Velocity.init();
	}
	
	private final File dir; // the output directory into which we write the templates
	private final List<Template> templates;
	private final List<DataSet> dataSets;

	public OutputWriter(File dir, List<Template> templates, List<DataSet> dataSets) {
		this.dir = dir;
		this.templates = templates;
		this.dataSets = dataSets;
	}

	public File call() throws Exception {
		try{
			Preconditions.checkArgument(dir.canWrite(), "Can't write to "+dir);
			LOG.info("Writing output to: "+dir.getName()+"...");

			// sort the templates into global and non-global
			Collection<Template> globalTemplates = Collections2.filter(templates, new Predicate<Template>() {
				public boolean apply(Template template) {
					return template.isGlobal();
				}
			});
			Collection<Template> nonGlobalTemplates = Collections2.filter(templates, new Predicate<Template>() {
				public boolean apply(Template template) {
					return !template.isGlobal();
				}
			});

			
			// for non global templates, apply each dataset to the templates, keeping a counter
		if (!nonGlobalTemplates.isEmpty()) {
				int dataSetCount = 0;
				for (DataSet dataSet : dataSets) {
					++dataSetCount;
					// create a subdir i
					File outputSubdir = new File(dir,""+dataSetCount);
					outputSubdir.mkdir();

					//VelocityContext vc = new VelocityContext();
					VelocityContext vc = newContext();
					vc.put(AppConstants.DATASET, dataSet);
					vc.put(AppConstants.DATASET_CNTR, dataSetCount);


					for (Template template : nonGlobalTemplates) {
						// check if this template is to be writtern on a per group basis
						String perGroup = TemplatingProperties.getProperty(template.getFilename()+".perGroup", null);
						if (perGroup == null)
							writeContextToFile(vc, template, outputSubdir, dataSetCount);
						else {
							// write a copy of this template for each instance of the group
							for (DataSetGroup group : dataSet.getGroupsOfType(perGroup)) {
								vc.put(AppConstants.KEY_GROUP, group);
								writeContextToFile(vc, template, outputSubdir, dataSetCount);
								vc.remove(AppConstants.KEY_GROUP);
							}
						}
					}
				}
			}
			
			
			// for global templates, put all the datasets in a single context
			if (!globalTemplates.isEmpty()) {

				VelocityContext vc = newContext();
				vc.put(AppConstants.ALL_DATASETS, dataSets);

				for (Template template : globalTemplates) {
					writeContextToFile(vc, template, dir, 0);
				}
		 	}




			LOG.info("Writing output to: "+dir.getName()+" completed. ("+dataSets.size()+" datasets)");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dir;
	}

	/**
	 * Applies the context to the template and write it to the given outdir.
	 * The filename defaults to the template name, but can be overriden via templating.properties.
	 * @param vc
	 * @param template
	 * @param outDir
	 * @param dataSetCount 
	 * @throws IOException
	 */
	private void writeContextToFile(VelocityContext vc, Template template, File outDir, int dataSetCount) throws IOException {
		// derive the output filename by applying the dataset to the template name
		String templateName = template.getFilename();
		String outputFileNameTemplate = TemplatingProperties.getProperty(templateName+".renameTo", templateName);
		
		// Testing for appending path ID to filename
		if(outputFileNameTemplate.contains("#")){
			outputFileNameTemplate = outputFileNameTemplate.replace("#", "" + dataSetCount);
		}
		
		StringWriter outputFileNameSW = new StringWriter();
		Velocity.evaluate(vc, outputFileNameSW, outputFileNameTemplate, outputFileNameTemplate);
		// write the output file
		File outputFile = new File(outDir,outputFileNameSW.toString());
		Writer outputWriter = Files.newWriter(outputFile, Charsets.UTF_8);
		Velocity.evaluate(vc, outputWriter, templateName, template.getFileReader());
		outputWriter.close();
	}

	// create a new context and populate it with tools specified in config file (if any)
	private VelocityContext newContext() throws ClassNotFoundException {
		VelocityContext vc = new VelocityContext();
		Map<String,String> toolsMap = TemplatingProperties.getTools();
		for (Entry<String, String> entry : toolsMap.entrySet()) {
			try {
				vc.put(entry.getKey(), Class.forName(entry.getValue()));
			} catch (ClassNotFoundException e) {
				LOG.error("ClassNotFound while loading velocity tools. Check your properties file and classpath");
				throw e;
			}
		}
		return vc;
	}
}
