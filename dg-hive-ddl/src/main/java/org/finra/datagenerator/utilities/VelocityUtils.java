package org.finra.datagenerator.utilities;

import java.io.StringWriter;	
import java.util.Map;
import java.util.List;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityUtils {
		//Utilize velocity to generate sql
		public static String getGeneratedString(List<String> params, String filePath){
			try {
				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
				ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				ve.init();
				VelocityContext context = new VelocityContext();
				context.put("sampleDataList", params);
				
				Template t = ve.getTemplate(filePath);
				StringWriter sWriter = new StringWriter();
				t.merge(context, sWriter);
				sWriter.flush();
				sWriter.close();
				return sWriter.toString();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		//Utilize velocity to generate sql
		public static String getGeneratedString(Map<String, String> params, String filePath){
			try {
				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
				ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				ve.init();
				VelocityContext context = new VelocityContext();
				context.put("sampleData", params);

				Template t = ve.getTemplate(filePath);
				StringWriter sWriter = new StringWriter();
				t.merge(context, sWriter);
				sWriter.flush();
				sWriter.close();
				return sWriter.toString();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
}
