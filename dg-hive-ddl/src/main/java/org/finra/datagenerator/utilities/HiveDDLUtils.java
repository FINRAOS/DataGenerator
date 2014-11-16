package org.finra.datagenerator.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;	
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class HiveDDLUtils {
//	private static Map<String, String> sampleData;
	private static List<String> sampleDataList;
	private static List<String> colNames;
	private static List<String> colValues;
	private static boolean startCreate;
	private static boolean done;
	private static String tableName;
	
	public static String generateOutputXML(String filepath) throws Exception {
		int rowNum = 0;
		
		/* Read the Hive DDL input file line by line & process each line */
		InputStream is = Class.class.getResourceAsStream(filepath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		try {
			String text = null;
			while ((text = br.readLine()) != null) {
		        System.out.println(text);
		        processDDLline(text);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		System.out.println("\nsampleData: \t" + sampleData);
		System.out.println("\nsampleDataList: \t" + sampleDataList);
		
		/* Generate XMl using velocity template */
    	String output = VelocityUtils.getGeneratedString(sampleDataList, "velocityTemplates/samplemachine.txt");
    	System.out.println(output);
    	
    	return output;
	}
	
	public static String getTableName() {
		return tableName;
	}
	
	private static void processDDLline(String line) throws Exception {
		Pattern pCreate = Pattern.compile("CREATE TABLE ([a-zA-Z_]+) \\(");
		Matcher mCreate = pCreate.matcher(line);
		
		Pattern pColEnd = Pattern.compile("([a-zA-Z_]+) (TINYINT|SMALLINT|INT|BIGINT|FLOAT|DOUBLE|DECIMAL|TIMESTAMP|DATE|STRING|VARCHAR\\([0-9]+\\)|CHAR\\([0-9]+\\)|BOOLEAN)\\)");
		Matcher mColEnd = pColEnd.matcher(line);
		
		Pattern pCol = Pattern.compile("([a-zA-Z_]+) (TINYINT|SMALLINT|INT|BIGINT|FLOAT|DOUBLE|DECIMAL|TIMESTAMP|DATE|STRING|VARCHAR\\([0-9]+\\)|CHAR\\([0-9]+\\)|BOOLEAN),");
		Matcher mCol = pCol.matcher(line);
		
		if (mCreate.find()){
			
			startCreate = true;
//			sampleData = new HashMap<String, String>();
			sampleDataList = new ArrayList<String>();
			tableName = mCreate.group(1);
			
		} else if (mCol.find()){
			
			parseCols(mCol);
			
		} else if (mColEnd.find()){
			
			startCreate = false;
			done = true;
			parseCols(mColEnd);
			
		} else {
			//ignore
		}
	}
	
	private static void parseCols(Matcher mCol) throws Exception {
		String colName = mCol.group(1);
		String colType = mCol.group(2);
		
		List<String> dataList = null;
		String value = null;
		
		switch (colType) {
			case "TINYINT":
				dataList = new ArrayList<String>(Arrays.asList("-128", "0", "127"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;
			case "SMALLINT":
				dataList = new ArrayList<String>(Arrays.asList("-32768", "0", "32767"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;
			case "INT":
				dataList = new ArrayList<String>(Arrays.asList("-2147483648", "0", "2147483647"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "BIGINT":
				dataList = new ArrayList<String>(Arrays.asList("-9223372036854775808", "0", "9223372036854775807"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "FLOAT":
				dataList = new ArrayList<String>(Arrays.asList(String.valueOf(2^(-126)), "0", String.valueOf(((2-(2^-23))*(2^127)))));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;
			case "DOUBLE":
				dataList = new ArrayList<String>(Arrays.asList(String.valueOf(2.225*(10^-307)), "0", String.valueOf((-2.23)*(10^-308))));	//TEMP: use these values for now 
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "DECIMAL":
				dataList = new ArrayList<String>(Arrays.asList("-9999999999", "0", "9999999999"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "TIMESTAMP":
				dataList = new ArrayList<String>(Arrays.asList("0000-01-01 00:00:00.000000000", "1007-06-01 06:00:00.000000000", "2014-12-31 12:59:59.999999999"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "DATE":
				dataList = new ArrayList<String>(Arrays.asList("0000-01-01", "1007-06-01", "2014-12-31"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;
			case "STRING":
				dataList = new ArrayList<String>(Arrays.asList("asdfghjkl;'qwertyuiop[]zxcvbnm./1234567890-=", "asdfghjkl;'sdfghjkl;;'qwertyuiop[]zxcv]zxcvbnm./1234567890-="));
				value = "set:{" + StringUtils.chop(StringUtils.substringAfter(dataList.toString(), "[")) + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			case "BOOLEAN":
				dataList = new ArrayList<String>(Arrays.asList("true", "false"));
				value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
				sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				break;	
			default:
				if (colType.startsWith("VARCHAR")) {
					int size = Integer.parseInt(StringUtils.substringBetween(colType, "(", ")"));
					if (size < 1 || size > 65355) {
						throw new Exception("VARCHAR size is not between 1 and 65355");
					}
					
					dataList = new ArrayList<String>(Arrays.asList(StringUtils.leftPad("a", size-1, 'a'), "a", StringUtils.leftPad("a", (int)Math.ceil((size-1)/2), 'a')));
					value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
					sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				} else if (colType.startsWith("CHAR")) {
					int size = Integer.parseInt(StringUtils.substringBetween(colType, "(", ")"));
					if (size < 1 || size > 255) {
						throw new Exception("CHAR size is not between 1 and 255");
					}
					
					dataList = new ArrayList<String>(Arrays.asList(StringUtils.leftPad("a", size-1, 'a'), "a", StringUtils.leftPad("a", (int)Math.ceil((size-1)/2), 'a')));
					value = "set:{" + StringUtils.substringBetween(dataList.toString(), "[", "]") + "}";
					sampleDataList.add("name=\"" + colName + "\" expr=\"" + value + "\"");
				}
				
				break;
		}
	}
}
