package org.finra.datagenerator.csp.engine;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: k24364 Marshall Peters
 * Date: 8/21/14
 */
public interface Frontier {
    void searchForScenarios(Queue queue, Map<String, AtomicBoolean> flags);
}
