package org.finra.datagenerator.consumer.defaults;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class ConsumerResult {
    private final Map<String, String> dataMap = new HashMap<String, String>();
    private Map<String, AtomicBoolean> flags;
    private final long maxNumberOfLines;

    public ConsumerResult(long maxNumberOfLines, Map<String, AtomicBoolean> flags) {
        this.maxNumberOfLines = maxNumberOfLines;
        this.flags = flags;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public Map<String, AtomicBoolean> getFlags() {
        return flags;
    }

    public long getMaxNumberOfLines() {
        return maxNumberOfLines;
    }

    public String getPipeDelimited(){
        StringBuilder b = new StringBuilder(1024);
        for (Map.Entry<String, String> entry : getDataMap().entrySet()) {
            if (b.length() > 0) {
                b.append('|');
            }
            b.append(entry.getValue());
        }

        return b.toString();
    }
}