package org.finra.datagenerator.writer;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataPipe;

/**
 * Created by robbinbr on 5/28/2014.
 */
public class DefaultWriter implements DataWriter {

    protected static final Logger log = Logger.getLogger(DefaultWriter.class);
    private final OutputStream os;
    private String[] outTemplate;

    public DefaultWriter(OutputStream os) {
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
