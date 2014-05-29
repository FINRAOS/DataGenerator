package org.finra.datagenerator.writer;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.defaults.ConsumerResult;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by robbinbr on 5/28/2014.
 */
public class DefaultWriter implements OutputWriter {

    protected static final Logger log = Logger.getLogger(DefaultWriter.class);
    private OutputStream os;

    public DefaultWriter(OutputStream os){
        this.os = os;
    }

    @Override
    public void writeOutput(ConsumerResult cr) {
        try {
            os.write(cr.getPipeDelimited().getBytes());
        } catch (IOException e) {
            log.error("IOException in DefaultConsumer",e);
        }
    }
}
