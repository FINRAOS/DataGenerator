package org.finra.datagenerator.csp.constraints;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class EqualConstraint implements Constraint {

    private String one, two;

    public EqualConstraint(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public List<String> onVariables() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean satisfied(Map<String, String> variables) {
        return variables.get(one).equals(variables.get(two));
    }

    public String toString() {
        return "(== " + one + " " + two + ")";
    }
}
