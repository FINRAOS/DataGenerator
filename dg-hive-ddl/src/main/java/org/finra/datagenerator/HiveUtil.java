package org.finra.datagenerator;

import java.util.HashMap;	
import java.util.Map;

import org.finra.datagenerator.utilities.HiveDDLUtils;
import org.finra.datagenerator.utilities.VelocityUtils;

public class HiveUtil {
    public static void main(String[] args) {
    	try {
    		HiveDDLUtils.generateOutputXML("/hiveDDLInput/SampleHiveDDLInput.txt");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
}
