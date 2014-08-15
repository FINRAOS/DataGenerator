package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class AmongstConstraint implements Constraint {

    private List<String> tokens;
    private String variable;

    public AmongstConstraint(String variable, List<String> tokens) {
        this.variable = variable;
        this.tokens = tokens;
    }

    public List<String> onVariables() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean satisfied(Map<String, String> variables) {
        return tokens.contains(variables.get(variable));
    }

    public String toString() {
        return "(@ " + variable + " {" + tokens.toString() + "})";
    }
}
