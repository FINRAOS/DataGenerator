package org.finra.datagenerator.csp;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.writer.DefaultWriter;

/**
 * Created with IntelliJ IDEA.
 * User: k24364 Marshall Peters
 * Date: 8/26/14
 */
public class Test2 {

    public static String test = "<scxml xmlns=\"http://www.w3.org/2005/07/scxml\"\n" +
            "       xmlns:cs=\"http://commons.apache.org/scxml\"\n" +
            "       version=\"1.0\"\n" +
            "       initial=\"start\">\n" +
            "\n" +
            "    <state id=\"start\">\n" +
            "        <transition event=\"RECORD_TYPE\" target=\"RECORD_TYPE\"/>\n" +
            "    </state>\n" +
            "\n" +
            "    <state id=\"RECORD_TYPE\">\n" +
            "        <!-- Mandatory -->\n" +
            "        <onentry>\n" +
            "            <assign name=\"var_out_RECORD_TYPE\" expr=\"set:{x,y,z}\"/>\n" +
            "            <assign name=\"var_out_RECORD_TYPE_2\" expr=\"set:{x,y,z}\"/>\n" +
            "        </onentry>\n" +
            "        <transition event=\"REQUEST_IDENTIFIER\" target=\"REQUEST_IDENTIFIER\" cond=\"${var_out_RECORD_TYPE != var_out_RECORD_TYPE_2}\"/>\n" +
            "    </state>\n" +
            "\n" +
            "    <state id=\"REQUEST_IDENTIFIER\">\n" +
            "        <!-- Mandatory -->\n" +
            "        <!-- There's nothing going on here -->\n" +
            "        <onentry>\n" +
            "            <assign name=\"var_out_REQUEST_IDENTIFIER\" expr=\"set:{1,2,3}\"/>\n" +
            "        </onentry>\n" +
            "        <transition event=\"MANIFEST_GENERATION_DATETIME\" target=\"MANIFEST_GENERATION_DATETIME\"/>\n" +
            "    </state>\n" +
            "\n" +
            "    <state id=\"MANIFEST_GENERATION_DATETIME\">\n" +
            "        <!-- Mandatory -->\n" +
            "        <onentry>\n" +
            "            <assign name=\"var_out_MANIFEST_GENERATION_DATETIME\" expr=\"#{nextint}\"/>\n" +
            "        </onentry>\n" +
            "        <transition target=\"end\"/>\n" +
            "    </state>\n" +
            "    \n" +
            "<state id=\"end\">\n" +
            "    </state>\n" +
            "</scxml>";

    public static void main(String[] args) {
        Engine exec = new SCXMLEngine();
        exec.setModelByText(test);
        exec.setBootstrapMin(10);

        DefaultDistributor dist = new DefaultDistributor();
        dist.setMaxNumberOfLines(45000);
        dist.setThreadCount(10);

        DataConsumer consumer = new DataConsumer();
        consumer.addDataWriter(new DefaultWriter(System.out, new String[]{"var_out_RECORD_TYPE_2", "var_out_RECORD_TYPE", "var_out_REQUEST_IDENTIFIER", "var_out_MANIFEST_GENERATION_DATETIME"}));
        dist.setDataConsumer(consumer);

        exec.process(dist);
    }
}
