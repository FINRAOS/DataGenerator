/*
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
package org.finra.datagenerator.samples.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;

public class SampleMachineConsumer implements DataTransformer {

    protected static final Logger log = Logger.getLogger(SampleMachineConsumer.class);

    private final int myTemplateHashCode = "#{customplaceholder}".hashCode();
    private final int divideBy2HashCode = "#{divideBy2}".hashCode();
    private final Random rand = new Random(System.currentTimeMillis());
    private final Context context;
    private final AtomicLong lines = new AtomicLong(0);
    
    public SampleMachineConsumer(){
    	this.context = null;
    }
    public SampleMachineConsumer(Context context){
    	this.context = context;
    }
    
    @Override
    public void consume(DataPipe cr) {
    	AtomicBoolean exitFlag = cr.getExitFlag();
    	
    	// Go through our templates and fill them with values
        Map<String, String> originalRow = cr.getDataMap();
        HashMap<String, String> outputValues = new HashMap<String, String>();
        
        for (Entry<String, String> entry : originalRow.entrySet()) {
            String value = entry.getValue();
            int hashCode = value.hashCode();

            if (hashCode == myTemplateHashCode && value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                value = "" + ran;
            }
            
            /*
             * Enhanced Demo
             */
            if (hashCode == divideBy2HashCode && value.equals("#{divideBy2}")) {
                value = "" + Integer.valueOf(outputValues.get("var_out_V3")) / 2;
            }
            outputValues.put(entry.getKey(), value);
            
        }

        // Using the values, compile our output. In our case, we will just write it to the console
        if(context == null){
        	System.out.println("Row=" + outputValues.toString());
        //for HDFS we each mapper will write to thier own context
        }else{
        	try {
				lines.addAndGet(1);
	        	context.write(NullWritable.get(), new Text(outputValues.toString()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        if (exit.get()) {
            log.info("Exit is true, setting exitflag to true");
            exitFlag.set(true);
            if (reportingThread != null) {
                reportingThread.interrupt();
            }
        }

    }
        
    String reportingHost = null;
    Thread reportingThread = null;
    long lastReportedLineCount = 0;
    AtomicBoolean exit = new AtomicBoolean(false);

    public void setReportingHost(String hostPort) {
            this.reportingHost = hostPort;
            reportingThread = new Thread() {
                @Override
                public void run() {
                    while (!this.isInterrupted()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }

                        
						long currentLineCount = lines.get();
                        long delta = currentLineCount - lastReportedLineCount;
                        lastReportedLineCount = currentLineCount;

                        // Report the delta
                        String url = "http://" + reportingHost + "/" + delta;
                        String response = getUrlContents(url);
                        if (response.contains("exit")) {
                            exit.set(true);
                        }
                    }
                }
            };
            reportingThread.setDaemon(true);
            reportingThread.start();
    }

	private static String getUrlContents(String theUrl) {
	    StringBuilder content = new StringBuilder();
	    try {
	        URL url = new URL(theUrl);
	
	        URLConnection urlConnection = url.openConnection();
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	        
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            content.append(line).append("\n");
	        }
	        bufferedReader.close();
	    } catch (IOException e) {
	        log.error(e);
	    	log.error("Error while reading: " + theUrl);
	    }
	    log.info(content.toString());
	    return content.toString();
}

}
