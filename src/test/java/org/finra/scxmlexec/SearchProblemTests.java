package org.finra.scxmlexec;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by robbinbr on 3/3/14.
 */
public class SearchProblemTests {

    @Test
    public void testToJsonAndFromJson() throws Exception {
        ChartExec chartExec = new ChartExec();
        InputStream inputFileStream = new FileInputStream(new File("src/test/resources/test.xml"));
        String machineText = IOUtils.toString(inputFileStream, "UTF-8");
        chartExec.setBootstrapMin(3);
        List<SearchProblem> problems = chartExec.prepare(machineText);

        Assert.assertEquals(3, problems.size());

        SearchProblem one = problems.get(0);
        String json = one.toJson();
        System.out.println(json);
        SearchProblem oneFromJson = SearchProblem.fromJson(json);

        Assert.assertTrue(oneFromJson.getVarsOut().equals(one.getVarsOut()));
        Assert.assertTrue(oneFromJson.getInitialState().events.equals(one.getInitialState().events));
        Assert.assertTrue(oneFromJson.getInitialState().variablesAssignment.equals(one.getInitialState()
                .variablesAssignment));
    }
}