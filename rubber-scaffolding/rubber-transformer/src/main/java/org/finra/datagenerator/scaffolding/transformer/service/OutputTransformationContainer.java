package org.finra.datagenerator.scaffolding.transformer.service;

import org.finra.datagenerator.scaffolding.transformer.TransformDirection;

/**
 * Created by dkopel on 11/22/16.
 */
public class OutputTransformationContainer<T> extends TransformationContainer<T> {
    protected OutputTransformationContainer(String alias, Class<T> clazz, Long order) {
        super(alias, clazz, TransformDirection.OUTPUT, order);
    }

    protected OutputTransformationContainer(String alias, T value, Long order) {
        super(alias, value, TransformDirection.OUTPUT, order);
    }

    protected OutputTransformationContainer(String alias, Class<T> clazz, T value, Long order) {
        super(alias, clazz, value, TransformDirection.OUTPUT, order);
    }

    protected OutputTransformationContainer(String alias, Class<T> clazz, T value, Long order, Boolean join) {
        super(alias, clazz, value, TransformDirection.OUTPUT, order, join);
    }

    public OutputTransformationContainer(String alias, Class<T> clazz) {
        super(alias, clazz, TransformDirection.OUTPUT, Long.MAX_VALUE);
    }

    public OutputTransformationContainer(String alias, Class<T> clazz, Boolean join) {
        super(alias, clazz, TransformDirection.OUTPUT, Long.MAX_VALUE, join);
    }

    public OutputTransformationContainer(String alias, T value) {
        super(alias, value, TransformDirection.OUTPUT, Long.MAX_VALUE);
    }

    public OutputTransformationContainer(String alias, Class<T> clazz, T value) {
        super(alias, clazz, value, TransformDirection.OUTPUT, Long.MAX_VALUE);
    }
}
