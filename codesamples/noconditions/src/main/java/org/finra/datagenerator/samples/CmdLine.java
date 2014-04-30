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
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.exec.ChartExec;
import org.finra.datagenerator.samples.consumer.SampleMachineConsumer;

public class CmdLine {

    private static final DefaultDistributor defaultDist = new DefaultDistributor();

    public static void main(String args[]) throws Exception {
        ChartExec chartExec = new ChartExec();
        InputStream is = CmdLine.class.getResourceAsStream("/samplemachine.xml");

        chartExec.setInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        chartExec.setBootstrapMin(1);

        // In usual cases, that should be >4
        defaultDist.setThreadCount(1);

        defaultDist.setDataConsumer(new SampleMachineConsumer());
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        chartExec.process(defaultDist);
    }
}
