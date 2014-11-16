package org.hhat.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.scxml.SCXMLEngine;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;
import org.finra.datagenerator.utilities.HiveDDLUtils;
import org.finra.datagenerator.writer.CustomHiveWriter;
import org.finra.datagenerator.writer.DataWriter;

public class DataGenWrapper {

	public void executeDataGen(InputStream is) throws IOException {
		class DumbDataTransformer implements DataTransformer {

			@Override
			public void transform(DataPipe cr) {

				for (Map.Entry<String, String> entry : cr.getDataMap()
						.entrySet()) {
					String value = entry.getValue();
				}
			}

		}

		class DumbDefaultWriter implements DataWriter {
			int lineCounter = 0;

			@Override
			public void writeOutput(DataPipe cr) {
				Object[] values = cr.getDataMap().keySet().toArray();
				String[] outputSet = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					outputSet[i] = values[i].toString();
				}
				System.out.println(cr.getPipeDelimited(outputSet));
				lineCounter++;
				FileWriter fw;
				try {
					fw = new FileWriter(new File("").getAbsolutePath()
							+ "/count.txt");
					fw.write(Integer.toString(lineCounter));
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		Engine engine = new SCXMLEngine();

		engine.setModelByInputFileStream(is);

		// Usually, this should be more than the number of threads you intend to
		// run
		engine.setBootstrapMin(1);

		// Prepare the consumer with the proper writer and transformer
		DataConsumer consumer = new DataConsumer();
		consumer.addDataTransformer(new DumbDataTransformer());
		consumer.addDataWriter(new DumbDefaultWriter());

		// Prepare the distributor
		DefaultDistributor defaultDistributor = new DefaultDistributor();
		defaultDistributor.setThreadCount(1);
		defaultDistributor.setDataConsumer(consumer);
		Logger.getLogger("org.apache").setLevel(Level.WARN);

		engine.process(defaultDistributor);
	}
	
	// The inputStream is the raw ddl
	public void executeDataGenWithHive(InputStream rawDdl)
	{
    	String tableName = null;
    	String outputXML = null;
    	try {
    		outputXML = HiveDDLUtils.generateOutputXML(rawDdl);
    		tableName = HiveDDLUtils.getTableName();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        Engine engine = new SCXMLEngine();

        //will default to samplemachine, but you could specify a different file if you choose to
        InputStream is = new ByteArrayInputStream(outputXML.getBytes(StandardCharsets.UTF_8));

        engine.setModelByInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        engine.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());
        consumer.addDataWriter(new CustomHiveWriter(System.out,
              new String[]{"intC", "floatC", "timestampC", "dateC", "stringC", "varcharC"}, tableName));

        //Prepare the distributor
        DefaultDistributor defaultDistributor = new DefaultDistributor();
        defaultDistributor.setThreadCount(1);
        defaultDistributor.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        engine.process(defaultDistributor);
		
	}

	@SuppressWarnings("deprecation")
	public void postToMobileApp() {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://yonecesito.site90.com/index.php");
			httpPost.setEntity(new UrlEncodedFormEntity(getParams()));

			httpClient.execute(httpPost);
			httpClient.close();

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	private List<NameValuePair> getParams() {
		// Building Parameters

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("tag", "report_status"));
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		params.add(new BasicNameValuePair("status", sdf.format(d) + ": 100"));

		return params;

	}
}
