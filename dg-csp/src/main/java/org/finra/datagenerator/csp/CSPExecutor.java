package org.finra.datagenerator.csp;

import org.finra.datagenerator.csp.constraints.Constraint;
import org.finra.datagenerator.csp.engine.Engine;
import org.finra.datagenerator.csp.engine.Frontier;
import org.finra.datagenerator.csp.parse.CSPParser;
import org.finra.datagenerator.distributor.SearchDistributor;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/11/14
 */
public class CSPExecutor implements Engine {

    private ConstraintSatisfactionProblem csp;
    private int bootStrap;

    public CSPExecutor(ConstraintSatisfactionProblem csp) {
        this.csp = csp;
    }

    private List<CSPPossibleState> bfs(int min) {
        List<CSPPossibleState> bootStrap = new LinkedList<CSPPossibleState>();

        CSPPossibleState root = new CSPPossibleState();
        root.variables = new HashMap<String,String>();
        root.depth = 0;
        bootStrap.add(root);

        while (bootStrap.size() < min) {
            CSPPossibleState split = bootStrap.remove(0);

            ConstraintSatisfactionProblem.ProblemLevel level = csp.levels.get(split.depth);
            String variable = level.variable;
            Constraint constraint = level.progressCheck;

            for (String domain: level.domain) {
                split.variables.put(variable, domain);
                if (constraint.satisfied(split.variables)) {
                    CSPPossibleState newSplit = new CSPPossibleState();
                    newSplit.variables = new HashMap<String,String>(split.variables);
                    newSplit.depth = split.depth + 1;

                    bootStrap.add(newSplit);
                }
            }
        }

        return bootStrap;
    }

    public void process(SearchDistributor distributor) {
        List<Frontier> frontiers = new LinkedList<Frontier>();

        for (CSPPossibleState p: bfs(bootStrap)) {
            Frontier frontier = new CSPFrontier(csp, p);
        }

        distributor.distribute(frontiers);
    }

    public void setModelByInputFileStream(InputStream inputFileStream) {
        Reader reader = new InputStreamReader(inputFileStream);
        CSPParser parse = new CSPParser(reader);
        csp = parse.parse();
    }

    public void setModelByText(String model) {
        Reader reader = new StringReader(model);
        CSPParser parse = new CSPParser(reader);
        csp = parse.parse();
    }

    public Engine setBootstrapMin(int min) {
        bootStrap = min;
        return this;
    }
}
