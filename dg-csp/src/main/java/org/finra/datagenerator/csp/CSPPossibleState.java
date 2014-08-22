package org.finra.datagenerator.csp;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: K24364 Marshall Peters
 * Date: 8/14/14
 */
public class CSPPossibleState {
    public Map<String, String> variables;
    public int depth;

    public String toString() {
        return "<" + variables.toString() + " " + depth + ">";
    }
}
