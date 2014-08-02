package org.finra.datagenerator.exec;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.DataPipe;
import org.finra.datagenerator.consumer.DataTransformer;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 3/3/14.
 */
public class ChartExecTests {

    @Test
    public void testProcessMinThree() throws Exception {
        ChartExec exec = new ChartExec();
        exec.setInputFileName("src/test/resources/test.xml");
        DataConsumer consumer = new DataConsumer();
        TestTransformer testTransformer = new TestTransformer();
        consumer.addDataTransformer(testTransformer);
        DefaultDistributor distributor = new DefaultDistributor();
        distributor.setDataConsumer(consumer);
        exec.setBootstrapMin(3).process(distributor);

        System.out.println(testTransformer.getData());

        Assert.assertEquals(9, testTransformer.getData().size());
    }

    @Test
    public void testProcessMinZero() throws Exception {
        ChartExec exec = new ChartExec();
        exec.setInputFileName("src/test/resources/test.xml");
        DataConsumer consumer = new DataConsumer();
        TestTransformer testTransformer = new TestTransformer();
        consumer.addDataTransformer(testTransformer);
        DefaultDistributor distributor = new DefaultDistributor();
        distributor.setDataConsumer(consumer);
        exec.setBootstrapMin(0).process(distributor);

        System.out.println(testTransformer.getData());

        Assert.assertEquals(9, testTransformer.getData().size());
    }


    @Test
    public void testProcessParallelThreeByThree() throws Exception {
        ChartExec exec = new ChartExec();
        exec.setInputFileName("src/test/resources/test.xml");

        TestTransformer transformer = new TestTransformer();
        DefaultDistributor distributor = new DefaultDistributor();
        distributor.setDataConsumer(new DataConsumer().addDataTransformer(transformer));
        distributor.setThreadCount(3);
        exec.setBootstrapMin(3).process(distributor);

        System.out.println(transformer.getData());

        Assert.assertEquals(9, transformer.getData().size());
    }

    private class TestTransformer implements DataTransformer {

        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        public void transform(DataPipe cr) {
            data.add(cr.getDataMap());
            System.out.println("Output saw a : " + cr.getDataMap());
        }

        public List<Map<String, String>> getData() {
            return data;
        }
    }
}
