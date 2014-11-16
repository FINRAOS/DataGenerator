package org.hhat.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.finra.datagenerator.writer.DataWriter;

/**
 * Servlet implementation class ExecuteDataGen
 */
@WebServlet(asyncSupported = true, description = "DataGenerator scxml will be given in a post and then executed with datagen defaults", urlPatterns = { "/ExecuteDataGen" })
public class ExecuteDataGen extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExecuteDataGen() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// This is a post that will come the app to kick off the execution of
		// the datagenerator
		// Here we will get the scxml that has been posted
		// TODO: Validation of the scxml

		// Now I got the workflow, now we'll toss the workflow to datagen
		// Since datagen's a monster in execution time we'll do this in an async
		// fashion
		final AsyncContext acontext = request.startAsync();
		acontext.start(new Runnable() {
			public void run() {
				try {
					InputStream is = acontext.getRequest().getInputStream();
					String workflow = "";
					while (is.available() != 0) {
						byte[] readBytes = new byte[is.available()];
						is.read(readBytes);
						workflow += new String(readBytes);
					}
					OutputStream os = acontext.getResponse().getOutputStream();
					os.write(workflow.getBytes());
					acontext.complete();
					InputStream stream = new ByteArrayInputStream(workflow
							.getBytes(StandardCharsets.UTF_8));

					executeDataGen(stream);
					postToMobileApp();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void postToMobileApp() {
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://yonecesito.site90.com/index.php");
			httpPost.setEntity(new UrlEncodedFormEntity(getParams()));

			HttpResponse httpResponse = httpClient.execute(httpPost);

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

	private void executeDataGen(InputStream is) throws IOException {
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
}
