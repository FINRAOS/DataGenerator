package org.finra.datagenerator.writer;

import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by robbinbr on 5/28/2014.
 */
public class DefaultWriter implements DataWriter {

    protected static final Logger log = Logger.getLogger(DefaultWriter.class);
    private OutputStream os;
    private String[] outTemplate;

    public DefaultWriter(OutputStream os, String[] outTemplate) {
        this.os = os;
    }

    @Override
    public void writeOutput(DataPipe cr) {
        try {
            os.write(cr.getPipeDelimited(outTemplate).getBytes());
        } catch (IOException e) {
            log.error("IOException in DefaultConsumer", e);
        }
    }
}