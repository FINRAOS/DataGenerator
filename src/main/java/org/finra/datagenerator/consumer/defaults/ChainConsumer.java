package org.finra.datagenerator.consumer.defaults;

import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.writer.OutputWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class ChainConsumer implements DataConsumer {

    private final List<DataConsumer> chain = new ArrayList<DataConsumer>();
    private List<OutputWriter> ow = new ArrayList<OutputWriter>();

    public List<DataConsumer> getChain() {
        return chain;
    }

    public ChainConsumer addConsumer(DataConsumer dc) {
        chain.add(dc);
        return this;
    }

    public ChainConsumer addOutputWriter(OutputWriter ow) {
        this.ow.add(ow);
        return this;
    }

    @Override
    public void consume(ConsumerResult cr) {
        for (DataConsumer dc : chain) {
            dc.consume(cr);
        }

        for(OutputWriter oneOw : ow){
            oneOw.writeOutput(cr);
        }
   }
}
