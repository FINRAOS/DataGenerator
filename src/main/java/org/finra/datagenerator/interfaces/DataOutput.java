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
package org.finra.datagenerator.interfaces;

import java.io.IOException;

/**
 * Interface to allow genericizing data output to be used via a factory
 * @author Meles
 *
 */
public interface DataOutput {
	/**
	 * Outputs data to a specific source
	 * @param arr - 2-D byte array that writes to file in a line-by-line manner
	 * @throws IOException
	 */
	public void outputData(byte[][] arr) throws IOException;
	
}
