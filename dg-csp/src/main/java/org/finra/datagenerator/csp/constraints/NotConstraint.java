package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class NotConstraint implements Constraint {

    private Constraint not;

    public NotConstraint(Constraint not) {
        this.not = not;
    }

    public List<String> onVariables() {
        return null;
    }

    public boolean satisfied(Map<String, String> variables) {
        return !not.satisfied(variables);
    }

    public String toString() {
        return "(! " + not.toString() + ")";
    }
}
