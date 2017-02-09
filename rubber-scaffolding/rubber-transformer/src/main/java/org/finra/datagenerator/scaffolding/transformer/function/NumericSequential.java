package org.finra.datagenerator.scaffolding.transformer.function;

/**
 * Created by dkopel on 11/17/16.
 */
public abstract class NumericSequential<T extends Number> extends OrderedFunctionTransformation<T> {
    @Override
    abstract protected T getStep();

}
