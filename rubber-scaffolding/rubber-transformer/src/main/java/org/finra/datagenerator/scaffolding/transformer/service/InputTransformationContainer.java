package org.finra.datagenerator.scaffolding.transformer.service;

import org.finra.datagenerator.scaffolding.transformer.TransformDirection;

/**
 * Created by dkopel on 11/22/16.
 */
public class InputTransformationContainer<T> extends TransformationContainer<T> {
    protected InputTransformationContainer(String alias, Class<T> clazz, Long order) {
        super(alias, clazz, TransformDirection.INPUT, order);
    }

    protected InputTransformationContainer(String alias, T value, Long order) {
        super(alias, value, TransformDirection.INPUT, order);
    }

    protected InputTransformationContainer(String alias, Class<T> clazz, T value, Long order) {
        super(alias, clazz, value, TransformDirection.INPUT, order);
    }

    public InputTransformationContainer(String alias, Class<T> clazz) {
        super(alias, clazz, TransformDirection.INPUT, Long.MAX_VALUE);
    }

    public InputTransformationContainer(String alias, T value) {
        super(alias, value, TransformDirection.INPUT, Long.MAX_VALUE);
    }

    public InputTransformationContainer(String alias, Class<T> clazz, T value) {
        super(alias, clazz, value, TransformDirection.INPUT, Long.MAX_VALUE);
    }
}