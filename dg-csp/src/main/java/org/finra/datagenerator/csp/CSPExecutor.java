package org.finra.datagenerator.csp;

import org.finra.datagenerator.csp.constraints.Constraint;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class CSPExecutor {

    private ConstraintSatisfactionProblem csp;

    public CSPExecutor(ConstraintSatisfactionProblem csp) {
        this.csp = csp;
    }

    public List<PartialSolution> BFSplit(int min) {
        List<PartialSolution> bootStrap = new LinkedList<PartialSolution>();

        PartialSolution root = new PartialSolution();
        root.variables = new HashMap<String,String>();
        root.depth = 0;
        bootStrap.add(root);

        while (bootStrap.size() < min) {
            PartialSolution split = bootStrap.remove(0);

            ConstraintSatisfactionProblem.ProblemLevel level = csp.levels.get(split.depth);
            String variable = level.variable;
            Constraint constraint = level.progressCheck;

            for (String domain: level.domain) {
                split.variables.put(variable, domain);
                if (constraint.satisfied(split.variables)) {
                    PartialSolution newSplit = new PartialSolution();
                    newSplit.variables = new HashMap<String,String>(split.variables);
                    newSplit.depth = split.depth + 1;

                    bootStrap.add(newSplit);
                }
            }
        }

        return bootStrap;
    }

    public void DFS(Queue queue, PartialSolution start, Map<String, AtomicBoolean> flags) {
        DFSinternal(start.depth, start.variables, queue, flags);
    }

    private void DFSinternal(int depth, Map<String, String> variables, Queue queue, Map<String, AtomicBoolean> flags) {
        if (depth >= csp.levels.size() || !flags.isEmpty()) {
            Map<String, String> result = new HashMap<>(variables);
            queue.add(result);

            return;
        }

        ConstraintSatisfactionProblem.ProblemLevel level = csp.levels.get(depth);
        String variable = level.variable;
        Constraint constraint = level.progressCheck;

        for (String domain: level.domain) {
            variables.put(variable, domain);
            if (constraint.satisfied(variables))
                DFSinternal(depth + 1, variables, queue, flags);
        }

        variables.remove(variable);
    }
}
