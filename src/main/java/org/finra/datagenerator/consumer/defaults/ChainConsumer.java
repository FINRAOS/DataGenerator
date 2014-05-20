package org.finra.datagenerator.consumer.defaults;

import org.finra.datagenerator.consumer.DataConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class ChainConsumer implements DataConsumer {

    private final List<DataConsumer> chain = new ArrayList<DataConsumer>();

    public List<DataConsumer> getChain() {
        return chain;
    }

    public ChainConsumer addConsumer(DataConsumer dc) {
        chain.add(dc);
        return this;
    }


    @Override
    public void consume(ConsumerResult cr) {
        for (DataConsumer dc : chain) {
            dc.consume(cr);
        }
    }
}
