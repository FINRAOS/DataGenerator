/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.finra.datagenerator.test;

import org.apache.log4j.Logger;
import org.finra.datagenerator.generation.DataSet;
import org.finra.datagenerator.generation.DataSetVariable;
import org.finra.datagenerator.input.PropertySpec;
import org.finra.datagenerator.input.VariableSpec;
import org.junit.Test;

/**
 *
 * @author mosama
 */
public class DataSetTest {

    private static final Logger LOG = Logger.getLogger(DataSetTest.class);

    @Test
    public void createVariableTests() {
        DataSet dataSet = new DataSet();

        VariableSpec vSpec = new VariableSpec("var");
        vSpec.addPropertySpec(new PropertySpec("vals")
                .addNegativeValue("neg")
                .addPositiveValue("pos")
        );
        dataSet.createVariable(new VariableSpec("var"), "varalias");
        DataSetVariable var = dataSet.get("varalias");

        LOG.info("var value="+var.toString());
    }
}
