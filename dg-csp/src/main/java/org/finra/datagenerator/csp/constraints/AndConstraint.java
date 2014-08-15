package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class AndConstraint implements Constraint {

    private Constraint one, two;

    public AndConstraint(Constraint one, Constraint two) {
        this.one = one;
        this.two = two;
    }

    public List<String> onVariables() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean satisfied(Map<String, String> variables) {
        if (!one.satisfied(variables))
            return false;

        if (!two.satisfied(variables))
            return false;

        return true;
    }

    public String toString() {
        return "(&& " + one.toString() + " " + two.toString() + ")";
    }
}
