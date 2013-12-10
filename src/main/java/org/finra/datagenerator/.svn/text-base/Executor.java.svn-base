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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.finra.datagenerator.generation.AllCombosDataSetGenerator;
import org.finra.datagenerator.generation.AllEdgesBranchDataSetGenerator;
import org.finra.datagenerator.generation.AllPathsBranchDataSetGenerator;
import org.finra.datagenerator.generation.DataSet;
import org.finra.datagenerator.generation.DefaultCombiDataSetGenerator;
import org.finra.datagenerator.generation.NegativeCombiDataSetGenerator;
import org.finra.datagenerator.generation.PairwiseCombiDataSetGenerator;
import org.finra.datagenerator.generation.PositiveCombiDataSetGenerator;
import org.finra.datagenerator.input.BranchGraph;
import org.finra.datagenerator.input.DataSpec;
import org.finra.datagenerator.input.Template;
import org.finra.datagenerator.input.TemplateReader;
import org.finra.datagenerator.input.VDXBranchGraphReader;
import org.finra.datagenerator.input.XLSDataSpecReader;
import org.finra.datagenerator.output.OutputWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Directs the main tasks of the data generator.
 * @author ChamberA
 *
 */
class Executor {

	private static Logger LOG = Logger.getLogger(Executor.class);

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	private Future<DataSpec> dataSpecFuture;
	private Future<BranchGraph> branchGraphFuture;
	private Future<List<Template>> templatesFuture;

	private List<DataSet> defaultDataSet;
	private List<DataSet> isoPosDataSets;
	private List<DataSet> isoNegDataSets;
	private List<DataSet> pairwiseDataSets;
	private List<DataSet> allCombosDataSets;

	private List<DataSet> allPathsDataSets;
	private List<DataSet> allEdgesDataSets;

	
	/**
	 * Wait for all tasks to complete, and then shutdown the task executor.
	 * @throws InterruptedException 
	 * @throws IllegalStateException 
	 */
	public void shutdownTaskExecutor() throws IllegalStateException, InterruptedException {
		taskExecutor.shutdown();
		LOG.debug("ThreadPoolTaskExecutor shutdown initiated!");
		taskExecutor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS); // blocks until termination or timeout
	}

	/**
	 * Threads out a DataSpecReader to read in the DataSpec from the files or dir given on the command line.
	 * The executor stores this internally as a Future<DataSpec>
	 */
	public void readDataSpec(String cmdLineArg) {

		// create a collection of xls files to pass to the XLSDataSpecReader.
		final Collection<File> xlsFiles = getFilesFromCmdLineArg(cmdLineArg, new String[]{"xls"});

		Preconditions.checkArgument(!xlsFiles.isEmpty(), "No xls files found in %s",cmdLineArg);
		
		// thread out an XLSDataSpecReader, and store the future.
		dataSpecFuture = taskExecutor.submit( new Callable<DataSpec>() {
			public DataSpec call() {
				return new XLSDataSpecReader().readDataSpecFromFiles(xlsFiles);
			}
		});
	}

	/**
	 * Threads out a BranchGraphReader to read in the BranchGraph from files in <input dir>/branch.
	 * The executor stores this internally as a Future<BranchGraph>
	 */
	public void readBranchGraph(String cmdLineArg) {

		final Collection<File> vdxFiles = getFilesFromCmdLineArg(cmdLineArg, new String[]{"vdx"});

		Preconditions.checkArgument(!vdxFiles.isEmpty(), "No vdx files found in %s",cmdLineArg);

		// thread out a VDXBranchGraphReader and store the future
		branchGraphFuture = taskExecutor.submit( new Callable<BranchGraph>() {
			public BranchGraph call() {
				return new VDXBranchGraphReader().readBranchGraphFromFiles(vdxFiles);
			}
		});
	}

	/**
	 * Threads out one TemplateReader to read in the template files from <input dir>/templates
	 * and another to read global templates <input dir>/templates/global. Only certain file extensions
	 * will be recognized as templates. Check wiki documentation and AppConstants.TEMPLATE_EXTENSIONS.
	 */
	public void readTemplateInput(String cmdLineArg) {

		final Collection<File> templateFiles = getFilesFromCmdLineArg(cmdLineArg, AppConstants.TEMPLATE_EXTENSIONS);

		Preconditions.checkArgument(!templateFiles.isEmpty(), "No template files found in %s",cmdLineArg);

		// thread out a TemplateReader for the non-global templates
		templatesFuture = taskExecutor.submit( new Callable<List<Template>>() {
			public List<Template> call() {
				return new TemplateReader().readTemplateFiles(templateFiles);
			}
		});
	}

	/**
	 * Generates the default data set. This is done by assigning each variable in the dataspec
	 * it's default value.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void generateDefaultCombiDataset() throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		defaultDataSet = DefaultCombiDataSetGenerator.getInstance().generateDataSets(dataSpec);

	}

	public void generateIsoPosCombiDataSets() throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		isoPosDataSets = PositiveCombiDataSetGenerator.getInstance().generateDataSets(dataSpec);
	}

	public void generateIsoNegCombiDataSets() throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		isoNegDataSets = NegativeCombiDataSetGenerator.getInstance().generateDataSets(dataSpec);
	}

	public void generatePairwiseCombiDataSets() throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		pairwiseDataSets = PairwiseCombiDataSetGenerator.getInstance().generateDataSets(dataSpec);
	}
	
	public void generateAllCombosDataSets() throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		allCombosDataSets = AllCombosDataSetGenerator.getInstance().generateDataSets(dataSpec);
	}	

	public void generateAllPathsDataSets()  throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		BranchGraph graph = branchGraphFuture.get();
		allPathsDataSets = AllPathsBranchDataSetGenerator.getInstance().generateDataSets(dataSpec, graph);
	} 

	public void generateAllEdgesDataSets()  throws InterruptedException, ExecutionException {
		DataSpec dataSpec = dataSpecFuture.get();
		BranchGraph graph = branchGraphFuture.get();
		allEdgesDataSets= AllEdgesBranchDataSetGenerator.getInstance().generateDataSets(dataSpec, graph);
	}

	/**
	 * Applies the generated datasets to the templates and writes them into the output directory.
	 * Assumes that the outputDir argument exists and can be written to.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void writeOutput(File outputDir) throws InterruptedException, ExecutionException, IOException {
		
		Preconditions.checkArgument(outputDir.canWrite());
		// go through the datasets
		writeOutDataSets(defaultDataSet, new File(outputDir, AppConstants.DEFAULT_OUT_DIRNAME));
		writeOutDataSets(isoPosDataSets, new File(outputDir, AppConstants.ISOPOS_OUT_DIRNAME));
		writeOutDataSets(isoNegDataSets, new File(outputDir, AppConstants.ISONEG_OUT_DIRNAME));
		writeOutDataSets(pairwiseDataSets, new File(outputDir, AppConstants.PAIRWISE_OUT_DIRNAME));
		writeOutDataSets(allCombosDataSets, new File(outputDir, AppConstants.ALLCOMBOS_OUT_DIRNAME));
		writeOutDataSets(allPathsDataSets, new File(outputDir, AppConstants.ALLPATHS_OUT_DIRNAME));
		writeOutDataSets(allEdgesDataSets, new File(outputDir, AppConstants.ALLEDGES_OUT_DIRNAME));
	}

	private void writeOutDataSets(List<DataSet> dataSets, File outDir) throws InterruptedException, ExecutionException {
		if (dataSets == null)
			return;

		outDir.mkdir();
		taskExecutor.submit(new OutputWriter(outDir, templatesFuture.get(), dataSets));
	}

	/**
	 * We allow the user to specify either a single file or directory of files on the command line.
	 * This method is used to take that command line argument and return a collection of 0 or more files.
	 * @param cmdLineArg
	 * @param exts Acceptable extensions
	 * @return
	 */
	private Collection<File> getFilesFromCmdLineArg(String cmdLineArg, String[] exts) {
		// turn the string into a file and check that it exists
		File cmdLineFile = new File(cmdLineArg);
		Preconditions.checkArgument(cmdLineFile.exists()," Bad command line argument: File/Dir not found: %s",cmdLineFile.getPath());
		
		// case 1 - We got a directory, so we returns all the files that match the extensions
		if (cmdLineFile.isDirectory()) {
			return FileUtils.listFiles(cmdLineFile, exts, false);
		}
		
		// case 2 - We got a file, so we return it in a collection if it matches the extensions
		if (Arrays.asList(exts).contains(FilenameUtils.getExtension(cmdLineFile.getName()))) {
			return Lists.newArrayList(cmdLineFile);
		}
		else {
			return new LinkedList<File>(); // empty
		}
	}
}
