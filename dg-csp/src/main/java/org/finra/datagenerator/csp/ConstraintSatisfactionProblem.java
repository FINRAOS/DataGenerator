package org.finra.datagenerator.csp;

import org.finra.datagenerator.csp.constraints.Constraint;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/8/14
 */
public class ConstraintSatisfactionProblem {

    public ArrayList<ProblemLevel> levels;

    public ConstraintSatisfactionProblem() {
        levels = new ArrayList<>();
    }

    public class ProblemLevel {
        public String variable;
        public List<String> domain;
        public Constraint progressCheck;

        public String toString() {
            return variable + " " + domain.toString() + " " + progressCheck.toString();
        }
    }

    public void addLevel(String variable, List<String> domain, Constraint progressCheck) {
        ProblemLevel level = new ProblemLevel();
        level.variable = variable;
        level.domain = domain;
        level.progressCheck = progressCheck;

        levels.add(0, level);
    }

    public String toString() {
        String result = "";
        for (ProblemLevel level: levels) {
            result += level.toString();
            result += "\n";
        }
        result += "END";

        return result;
    }

}
