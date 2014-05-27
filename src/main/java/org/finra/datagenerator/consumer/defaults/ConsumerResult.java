package org.finra.datagenerator.consumer.defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class ConsumerResult {

    private final List<String> rowResults = new ArrayList<String>();
    private final Map<String, String> dataMap = new HashMap<String, String>();
    private final AtomicBoolean exitFlag;
    private final long maxNumberOfLines;

    public ConsumerResult(long maxNumberOfLines, AtomicBoolean exitFlag) {
        this.maxNumberOfLines = maxNumberOfLines;
        this.exitFlag = exitFlag;
    }

    public List<String> getRowResults() {
        return rowResults;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public AtomicBoolean getExitFlag() {
        return exitFlag;
    }

    public long getMaxNumberOfLines() {
        return maxNumberOfLines;
    }
}
