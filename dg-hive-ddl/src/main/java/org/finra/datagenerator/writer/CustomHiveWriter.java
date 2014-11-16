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
package org.finra.datagenerator.writer;

import org.apache.commons.lang.math.NumberUtils;	
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Orders result variables based on a template and writes them seperated by pipe characters to a given OutputStream.
 *
 * Created by robbinbr on 5/28/2014.
 */
public class CustomHiveWriter implements DataWriter {

	private String table;
	
    /**
     * Logger
     */
    protected static final Logger log = Logger.getLogger(CustomHiveWriter.class);
    private final OutputStream os;
    private String[] outTemplate;

    /**
     * Constructor
     *
     * @param os the output stream to use in writing
     * @param outTemplate the output template to format writing
     */
    public CustomHiveWriter(final OutputStream os, final String[] outTemplate, String table) {
        this.os = os;
        this.outTemplate = outTemplate;
        this.table = table;
    }

    @Override
    public void writeOutput(DataPipe cr) {
        try {
        	String output = cr.getPipeDelimited(outTemplate);
        	String[] values = output.split("\\|");
        	output = "INSERT INTO TABLE " + table + " VALUES (";
        	for (String s: values) {
        		Pattern p = Pattern.compile(".*[a-zA-Z]+.*");
        		Matcher m = p.matcher(s);
        		
        		Pattern pDate = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        		Matcher mDate = pDate.matcher(s);
        		
        		if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {	//boolean
        			output += s.trim();
        		} else if (mDate.find()) {	//timestamp
        			output += "'" + s.trim() + "'";
        		} else if (m.find()) {
        			output += "\"" + s.trim() + "\"";
        		} else {
        			output += s.trim();
        		}
        		output += ",";
        	}
        	output = StringUtils.chop(output);
        	output += ");";
        	
            os.write(output.getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            log.error("IOException in DefaultConsumer", e);
        }
    }
}
