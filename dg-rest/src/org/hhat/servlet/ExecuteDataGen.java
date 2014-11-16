package org.hhat.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.hhat.common.DataGenWrapper;

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
		// TODO: Validation of the scxml?

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
					DataGenWrapper dgw = new DataGenWrapper();
					dgw.executeDataGen(stream);
					dgw.postToMobileApp();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}


}
