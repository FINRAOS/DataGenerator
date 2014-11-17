package org.finra.datagenerator;

import org.finra.datagenerator.utilities.HiveDDLUtils;

public class HiveUtil {
    public static void main(String[] args) {
    	try {
    		HiveDDLUtils.generateOutputXML("/hiveDDLInput/SampleHiveDDLInput.txt");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
}
