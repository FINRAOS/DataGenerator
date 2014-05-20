package org.finra.datagenerator.consumer.defaults;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by RobbinBr on 5/18/2014.
 */
public class OutputStreamConsumer implements DataConsumer {

    private static OutputStream os;
    protected static final Logger log = Logger.getLogger(OutputStreamConsumer.class);

    public OutputStreamConsumer(OutputStream os) {
        this.os = os;
    }

    @Override
    public void consume(ConsumerResult cr) {

        for(String result : cr.getRowResults()){
            try {
                os.write(result.getBytes());
            } catch (IOException e) {
                log.error(e);
            }
        }
    }
}
