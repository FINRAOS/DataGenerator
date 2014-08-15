package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/8/14
 */
public interface Constraint {
    public List<String> onVariables();
    public boolean satisfied(Map<String, String> variables);
}
