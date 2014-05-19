package org.finra.datagenerator.exec;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.defaults.ConsumerResult;
import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 3/3/14.
 */
public class BigChartExecTests {

    private ChartExec exec;
    private DefaultDistributor distributor;

    @Before
    public void setUpChartExec() {
        exec = new ChartExec();
        exec.setInputFileName("src/test/resources/big.xml");
        distributor = new DefaultDistributor();
    }

    @Test
    public void testProcess() throws Exception {
        TestConsumer consumer = new TestConsumer();
        distributor.setDataConsumer(consumer);
        exec.setBootstrapMin(3).process(distributor);

        System.out.println(consumer.getData());

        Assert.assertEquals(9, consumer.getData().size());
    }

    @Test
    public void testProcessParallel() throws Exception {
        TestConsumer consumer = new TestConsumer();
        distributor.setThreadCount(3).setDataConsumer(consumer);
        exec.setBootstrapMin(3).process(distributor);

        System.out.println(consumer.getData());

        Assert.assertEquals(9, consumer.getData().size());
    }

    private class TestConsumer implements DataConsumer {

        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        public void consume(ConsumerResult cr) {
            data.add(cr.getDataMap());
            System.out.println("Output saw a : " + cr.getDataMap());
        }

        public List<Map<String, String>> getData() {
            return data;
        }
    }

}
