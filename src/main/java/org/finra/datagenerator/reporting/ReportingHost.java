package org.finra.datagenerator.reporting;

/**
 * Created by RobbinBr on 7/1/2014.
 */
public interface ReportingHost {

    void init();

    void cleanup();

    String handleRequest();
}
