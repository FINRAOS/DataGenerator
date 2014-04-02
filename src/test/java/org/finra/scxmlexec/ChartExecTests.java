package org.finra.scxmlexec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robbinbr on 3/3/14.
 */
public class ChartExecTests {

    private ChartExec exec;
    private DefaultDistributor distributor;

    @Before
    public void setUpChartExec() {
        exec = new ChartExec();
        exec.setInputFileName("src/test/resources/test.xml");
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
        distributor.setDataConsumer(consumer).setThreadCount(1);
        exec.setBootstrapMin(3).process(distributor);

        System.out.println(consumer.getData());

        Assert.assertEquals(9, consumer.getData().size());
    }

    private class TestConsumer implements DataConsumer {
        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        public void consume(HashMap<String, String> row) {
            data.add(row);
            System.out.println("Output saw a : " + row);
        }

        public List<Map<String, String>> getData() {
            return data;
        }
    }

}