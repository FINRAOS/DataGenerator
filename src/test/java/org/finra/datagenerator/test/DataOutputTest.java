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
package org.finra.datagenerator.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.finra.datagenerator.factory.DataOutputFactory;
import org.finra.datagenerator.interfaces.DataOutput;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the DataOutput interface implementations
 * 
 * @author MeleS
 *
 */
public class DataOutputTest {
	
	private String[] expectedOut = new String[] { "Test 1", "Test 2"};
	private byte[][] testArr = new byte[][] {expectedOut[0].getBytes(), expectedOut[1].getBytes()};
	private final String testFileStr = System.getProperty("user.dir") + "/Test.txt";
	private final String factoryTestFileStr = "file://" + System.getProperty("user.dir") + "/Test.txt";
	private final File testFile = new File(factoryTestFileStr);
	
	/*
	 * Delete the test file (if the test successfully created that file)
	 */
	@After
	public void tearDown() {
		new File(testFileStr).delete();
	}
	
	/*
	 * Tests data output to a local file using a String
	 */
	@Test
	public void testOutputUsingString() throws IOException{
		DataOutput output = DataOutputFactory.getInstance().getDataOutput(factoryTestFileStr);
		
		output.outputData(testArr);
		String[] data = grabLines(new File(testFileStr));
		
		Assert.assertArrayEquals(expectedOut, data);
		
	}
	
	/*
	 * Tests data output to a local file using a URL
	 */
	@Test
	public void testOutputUsingURL() throws IOException{
		DataOutput output = DataOutputFactory.getInstance().getDataOutput(new URL(factoryTestFileStr));
		
		output.outputData(testArr);
		String[] data = grabLines(new File(testFileStr));
		
		Assert.assertArrayEquals(expectedOut, data);
		
	}
	
	/*
	 * Tests data output to a local file using a File
	 */
	@Test
	public void testOutputUsingFile() throws IOException{
		DataOutput output = DataOutputFactory.getInstance().getDataOutput(testFile);
		
		output.outputData(testArr);
		String[] data = grabLines(new File(testFileStr));
		
		Assert.assertArrayEquals(expectedOut, data);
		
	}
	
	/*
	 * Utility to pull out the data output to the file for validation
	 */
	private String[] grabLines(File file) throws IOException{
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		try {	
			while(br.ready()){
				lines.add(br.readLine());
			}
		} catch (IOException e) {
			
		}finally{
			br.close();
		}
		return lines.toArray(new String[lines.size()]);
	}
}
