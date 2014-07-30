package org.finra.datagenerator.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class DataPipe {

    private final Map<String, String> dataMap = new HashMap<String, String>();
    private DataConsumer dataConsumer = null;

    public DataPipe() {
        dataConsumer = new DataConsumer();

    }

    public DataPipe(DataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    public DataPipe(long maxNumberOfLines, Map<String, AtomicBoolean> flags) {
        this.dataConsumer = new DataConsumer().setMaxNumberOfLines(maxNumberOfLines).setFlags(flags);
    }


    public DataConsumer getDataConsumer() {
        return this.dataConsumer;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public String getPipeDelimited(String[] outTemplate) {
        StringBuilder b = new StringBuilder(1024);

        for (String var : outTemplate) {
            if (b.length() > 0) {
                b.append('|');
            }
            b.append(getDataMap().get(var));
        }

        return b.toString();
    }
}