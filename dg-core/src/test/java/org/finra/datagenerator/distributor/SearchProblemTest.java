package org.finra.datagenerator.distributor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.finra.datagenerator.exec.ChartExec;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by robbinbr on 3/3/14.
 */
public class SearchProblemTest {

    @Test
    public void testToJsonAndFromJson() throws FileNotFoundException, IOException,
            NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ChartExec chartExec = new ChartExec();
        InputStream inputFileStream = new FileInputStream(new File("src/test/resources/test.xml"));
        chartExec.setInputFileStream(inputFileStream);
        String machineText = IOUtils.toString(inputFileStream, "UTF-8");
        chartExec.setBootstrapMin(3);

        Method prepareMethod = ChartExec.class.getDeclaredMethod("prepare", String.class);
        Assert.assertNotEquals(null, prepareMethod);
        prepareMethod.setAccessible(true);
        try {
            List<SearchProblem> problems = (List<SearchProblem>) prepareMethod.invoke(chartExec, machineText);

            Assert.assertEquals(3, problems.size());

            SearchProblem one = problems.get(0);
            String json = one.toJson();
            System.out.println(json);
            SearchProblem oneFromJson = SearchProblem.fromJson(json);

            Assert.assertTrue(oneFromJson.getVarsOut().equals(one.getVarsOut()));
            Assert.assertTrue(oneFromJson.getInitialState().getEvents().equals(one.getInitialState().getEvents()));
            Assert.assertTrue(oneFromJson.getInitialState().getVariablesAssignment().equals(one.getInitialState()
                    .getVariablesAssignment()));
        } catch (Exception e) {
            System.out.println("Error\n");
            e.printStackTrace();
            throw e;
        }
    }
}
