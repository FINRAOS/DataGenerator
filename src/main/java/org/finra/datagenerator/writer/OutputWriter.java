package org.finra.datagenerator.writer;

import org.finra.datagenerator.consumer.defaults.ConsumerResult;

/**
 * Created by robbinbr on 5/28/2014.
 */
public interface OutputWriter {
    public void writeOutput(ConsumerResult cr);
}