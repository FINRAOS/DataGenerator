package org.hhat.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.finra.datagenerator.utilities.HiveDDLUtils;
import org.hhat.common.DataGenWrapper;

/**
 * Servlet implementation class GenerateDataFromHiveDDL
 */
@WebServlet(asyncSupported = true, description = "Generates data from a specified Hive DDL", urlPatterns = { "/GenerateDataFromHiveDDL" })
public class GenerateDataFromHiveDDL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateDataFromHiveDDL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
					dgw.executeDataGenWithHive(stream);
					dgw.postToMobileApp();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
