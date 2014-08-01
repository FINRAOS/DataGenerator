package org.finra.datagenerator.writer;

import org.finra.datagenerator.consumer.DataPipe;

/**
 * Created by robbinbr on 5/28/2014.
 */
public interface DataWriter {
    public void writeOutput(DataPipe cr);
}