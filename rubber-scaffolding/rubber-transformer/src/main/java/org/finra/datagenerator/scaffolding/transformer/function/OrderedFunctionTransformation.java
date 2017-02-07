package org.finra.datagenerator.scaffolding.transformer.function;

/**
 * Created by dkopel on 11/18/16.
 */
public abstract class OrderedFunctionTransformation<V> implements FunctionTransformation<V> {
    abstract protected V getStart();
    abstract protected Object getStep();
    abstract protected Direction getDirection();
}
