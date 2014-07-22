package org.finra.datagenerator.reporting;

/**
 * Created by RobbinBr on 7/1/2014.
 */
public interface ReportingHost {
    public void init();
    public void cleanup();
    public String handleRequest();
}