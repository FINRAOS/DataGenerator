package org.finra.datagenerator.scaffolding.transformer.support;

/**
 * Created by dkopel on 11/18/16.
 */
public @interface FunctionTransformation {
    String key();
    Class<? extends org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation> clazz();
}
