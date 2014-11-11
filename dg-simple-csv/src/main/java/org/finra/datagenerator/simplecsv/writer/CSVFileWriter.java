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
package org.finra.datagenerator.simplecsv.writer;

import com.opencsv.CSVWriter;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.writer.DataWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Marshall Peters
 * Date: 10/2/14
 */
public class CSVFileWriter implements DataWriter {

    private final CSVWriter csvFile;

    /**
     * Constructor
     *
     * @param csvFile output file name
     * @throws IOException if the output file can not be opened for writing
     */
    public CSVFileWriter(final String csvFile) throws IOException {
        this.csvFile = new CSVWriter(new FileWriter(csvFile), ',');
    }

    /**
     * Prints one line to the csv file
     *
     * @param cr data pipe with search results
     */
    public void writeOutput(DataPipe cr) {
        String[] nextLine = new String[cr.getDataMap().entrySet().size()];

        int count = 0;
        for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
            nextLine[count] = entry.getValue();
            count++;
        }

        csvFile.writeNext(nextLine);
    }

    /**
     * Closes the CSV file, should be called once all writing is done
     */
    public void closeCSVFile() {
        try {
            csvFile.close();
        } catch (IOException e) {
            System.out.println("ERROR! Failed to close csv file");
        }
    }

}
