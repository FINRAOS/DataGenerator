package org.finra.scxmlexec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by robbinbr on 3/3/14.
 */
public class DataGeneratorExecutorTests {

    private DataGeneratorExecutor executor;
    private Set<String> varsOut;
    private Map<String, String> initialVarsMap;
    private List<String> initialEvents;

    @Before
    public void setUpExecutor() throws ModelException, SAXException, IOException {
        executor = new DataGeneratorExecutor(FileUtils.readFileToString(new File("src/test/resources/test.xml")));

        varsOut = new HashSet<String>();
        varsOut.addAll(Arrays.asList(new String[]{"var_out_RECORD_TYPE", "var_out_REQUEST_IDENTIFIER",
                "var_out_MANIFEST_GENERATION_DATETIME"}));

        initialVarsMap = new HashMap<String, String>();
        initialEvents = new ArrayList<String>();
        executor.resetStateMachine(varsOut, initialVarsMap, initialEvents);
    }

    @Test
    public void testFindEvents() throws ModelException, SCXMLExpressionException, IOException {
        List<String> positive = new ArrayList<String>();
        List<String> negative = new ArrayList<String>();

        String state1 = "start";
        String state2 = "RECORD_TYPE";
        String state3 = "REQUEST_IDENTIFIER";
        String state4 = "MANIFEST_GENERATION_DATETIME";

        executor.findEvents(positive, negative);
        Assert.assertTrue(state1 + "-" + state2 + "-" + state2 + " not found in positive events",
                positive.contains(state1 + "-" + state2 + "-" + state2));

        List eList = new ArrayList<String>();
        eList.add(state2);
        executor.fireEvents(eList);
        executor.findEvents(positive, negative);

        Assert.assertTrue(state2 + "-" + state3 + "-" + state3 + " not found in positive events",
                positive.contains(state2 + "-" + state3 + "-" + state3));

        eList = new ArrayList<String>();
        eList.add(state3);
        executor.fireEvents(eList);
        executor.findEvents(positive, negative);

        Assert.assertTrue(state3 + "-" + state4 + "-" + state4 + " not found in positive events",
                positive.contains(state3 + "-" + state4 + "-" + state4));

        eList = new ArrayList<String>();
        eList.add(state4);
        executor.fireEvents(eList);
        executor.findEvents(positive, negative);

        Assert.assertTrue(positive.size() == 0);
    }

    @Test
    public void testBFSOneLevel() throws ModelException, SCXMLExpressionException, SAXException, IOException {
        List<PossibleState> statesAfterBFS = executor.searchForScenarios(varsOut, initialVarsMap, initialEvents, 5,
                10000, 50, 3);
        Assert.assertEquals(3, statesAfterBFS.size());
        Assert.assertEquals("a", statesAfterBFS.get(0).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("b", statesAfterBFS.get(1).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("c", statesAfterBFS.get(2).variablesAssignment.get("var_out_RECORD_TYPE"));
    }

    @Test
    public void testBFSTwoLevels() throws ModelException, SCXMLExpressionException, SAXException, IOException {
        List<PossibleState> statesAfterBFS = executor.searchForScenarios(varsOut, initialVarsMap, initialEvents, 5,
                10000, 50, 9);
        Assert.assertEquals(9, statesAfterBFS.size());
        Assert.assertEquals("a", statesAfterBFS.get(0).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("1", statesAfterBFS.get(0).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("a", statesAfterBFS.get(1).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("2", statesAfterBFS.get(1).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("a", statesAfterBFS.get(2).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("3", statesAfterBFS.get(2).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("b", statesAfterBFS.get(3).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("1", statesAfterBFS.get(3).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("b", statesAfterBFS.get(4).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("2", statesAfterBFS.get(4).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("b", statesAfterBFS.get(5).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("3", statesAfterBFS.get(5).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("c", statesAfterBFS.get(6).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("1", statesAfterBFS.get(6).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("c", statesAfterBFS.get(7).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("2", statesAfterBFS.get(7).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
        Assert.assertEquals("c", statesAfterBFS.get(8).variablesAssignment.get("var_out_RECORD_TYPE"));
        Assert.assertEquals("3", statesAfterBFS.get(8).variablesAssignment.get("var_out_REQUEST_IDENTIFIER"));
    }

//    @Test
//    public void testBFSBigMin() throws ModelException, SCXMLExpressionException, SAXException, IOException {
//        List<PossibleState> statesAfterBFS = executor.searchForScenarios(varsOut, initialVarsMap, initialEvents, 5,
// 10000, 50, 100);
//        Assert.assertEquals(9, statesAfterBFS.size());
//    }
}