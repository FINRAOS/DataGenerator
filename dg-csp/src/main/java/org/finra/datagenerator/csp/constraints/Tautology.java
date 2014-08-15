package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/13/14
 */
public class Tautology implements Constraint {
    @Override
    public List<String> onVariables() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean satisfied(Map<String, String> variables) {
        return true;
    }

    public String toString() {
        return "true";
    }
}
