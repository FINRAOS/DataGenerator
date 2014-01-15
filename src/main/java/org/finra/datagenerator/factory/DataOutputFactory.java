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
package org.finra.datagenerator.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.finra.datagenerator.impl.LocalFileOutput;
import org.finra.datagenerator.interfaces.DataOutput;

/**
 * Factory class that grabs the corresponding DataOutput class based on the output file passed in
 * @author MeleS
 *
 */
public class DataOutputFactory {
		
	private static DataOutputFactory instance;
	
	private DataOutputFactory(){
		//do nothing
	}
	
	public static DataOutputFactory getInstance(){
		if(instance == null){
			instance = new DataOutputFactory();
		}
		
		return instance;
	}
	/**
	 *
	 * Returns a DataOutput file based on the filePath you pass in?
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public DataOutput getDataOutput(String filePath) throws IOException{
		return getDataOutput(new URL(filePath));
	}
	
	public DataOutput getDataOutput(File theFile) throws IOException{
		return getDataOutput(new URL("file://" + theFile.getPath()));
	}
	
	public DataOutput getDataOutput(URL url) throws IOException{
		if(!url.toString().startsWith("file:/")){
			throw new IOException("Unable to identify the resource type (" + url.toString() + ")");
		}else{
			return new LocalFileOutput(url);
		}
	}
}
