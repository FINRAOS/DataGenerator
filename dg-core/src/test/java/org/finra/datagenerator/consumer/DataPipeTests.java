package org.finra.datagenerator.consumer;

import org.junit.Assert;
import org.junit.Test;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 7/2/2014.
 */
public class DataPipeTests {

    @Test
    public void testDataMap() {
        DataPipe thePipe = new DataPipe();
        thePipe.getDataMap().put("var1", "var1val");
        thePipe.getDataMap().put("var2", "var2val");
        thePipe.getDataMap().put("var3", "var3val");
        thePipe.getDataMap().put("var4", "var4val");
        thePipe.getDataMap().put("var5", "var5val");

        Assert.assertEquals(5, thePipe.getDataMap().size());

        Assert.assertEquals("var1val", thePipe.getDataMap().get("var1"));
        Assert.assertEquals("var2val", thePipe.getDataMap().get("var2"));
        Assert.assertEquals("var3val", thePipe.getDataMap().get("var3"));
        Assert.assertEquals("var4val", thePipe.getDataMap().get("var4"));
        Assert.assertEquals("var5val", thePipe.getDataMap().get("var5"));
    }

    @Test
    public void testGetPipeDelimited() {
        DataPipe thePipe = new DataPipe();
        thePipe.getDataMap().put("var1", "var1val");
        thePipe.getDataMap().put("var2", "var2val");
        thePipe.getDataMap().put("var3", "var3val");
        thePipe.getDataMap().put("var4", "var4val");
        thePipe.getDataMap().put("var5", "var5val");

        String[] outTemplate = new String[]{
                "var1", "var2", "var3", "var4", "var5"
        };

        Assert.assertEquals(5, thePipe.getDataMap().size());

        Assert.assertEquals("var1val|var2val|var3val|var4val|var5val", thePipe.getPipeDelimited(outTemplate));
    }

    @Test
    public void testDefaultDataConsumerAccess() {
        DataPipe thePipe = new DataPipe();
        DataConsumer dc = thePipe.getDataConsumer();
        dc.setFlags(new Hashtable<String, AtomicBoolean>());

        Assert.assertNotNull(dc);
        Assert.assertNotNull(dc.getFlags());
        Assert.assertEquals(10000, dc.getMaxNumberOfLines());
    }

    @Test
    public void testCustomDataConsumerAccess() {
        DataConsumer dc = new DataConsumer();
        DataPipe thePipe = new DataPipe(dc);

        dc.setReportingHost("localhost:8080");
        dc.setMaxNumberOfLines(100000);

        Assert.assertEquals("localhost:8080", thePipe.getDataConsumer().getReportingHost());
        Assert.assertEquals(100000, thePipe.getDataConsumer().getMaxNumberOfLines());
    }
}
