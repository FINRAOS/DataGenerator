package org.finra.datagenerator.csp;

import com.google.gson.Gson;
import org.finra.datagenerator.csp.constraints.Constraint;
import org.finra.datagenerator.engine.Frontier;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Marshall Peters
 * Date: 8/22/14
 */
public class CSPFrontier implements Frontier {
    private ConstraintSatisfactionProblem csp;
    private CSPPossibleState root;

    private Gson GSON;

    public CSPFrontier(ConstraintSatisfactionProblem csp, CSPPossibleState root) {
        this.csp = csp;
        this.root = root;

        GSON = new Gson();
    }

    public void searchForScenarios(Queue queue, AtomicBoolean flag) {
        dfs(root.depth, root.variables, queue, flag);
    }

    private void dfs(int depth, Map<String, String> variables, Queue queue, AtomicBoolean flag) {
        if (flag.get() == true) {
            return;
        }

        if (depth >= csp.levels.size()) {
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
                dfs(depth + 1, variables, queue, flag);
        }

        variables.remove(variable);
    }

    public Frontier fromJson(String json) {
        return GSON.fromJson(json, CSPFrontier.class);
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
