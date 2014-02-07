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
package org.finra.datagenerator.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.finra.datagenerator.storage.DataOutput;

/**
 * A DataOutput class that outputs data from a byte[][] array to the local file system
 * @author Meles
 *
 */
public class LocalFileOutput implements DataOutput{

	private File outFile;
	
	public LocalFileOutput(URL fileName){
		this.outFile = new File(fileName.getPath());
	}
	
	@Override
	public void outputData(byte[][] data) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFile);
			for(int i = 0; i < data.length; i++){
				fos.write((new String(data[i]) + "\n").getBytes());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		} finally {
			fos.close();
		}
	}
}
