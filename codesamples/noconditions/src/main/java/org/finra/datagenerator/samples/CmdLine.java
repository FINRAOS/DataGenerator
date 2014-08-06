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
package org.finra.datagenerator.samples;

import java.io.InputStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.exec.ChartExec;
import org.finra.datagenerator.samples.consumer.SampleMachineConsumer;
import org.finra.datagenerator.samples.transformer.SampleMachineTransformer;
import org.finra.datagenerator.writer.DefaultWriter;

public class CmdLine {

    private static final DefaultDistributor defaultDist = new DefaultDistributor();

    public static void main(String args[]) throws Exception {
        
    	ChartExec chartExec = new ChartExec();
        
    	//will default to samplemachine, but you could specify a different file if you choose to
        InputStream is = CmdLine.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");

        chartExec.setInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        chartExec.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());
        consumer.addDataWriter(new DefaultWriter(System.out, new String[] {"var_out_V1", "var_out_V2", "var_out_V3"}));

        // In usual cases, that should be >4
        defaultDist.setThreadCount(1);

        defaultDist.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        chartExec.process(defaultDist);
    }
}
