package org.finra.datagenerator.scaffolding.transformer.service;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by dkopel on 11/18/16.
 */
public class OutputOverride<T> {
    Class<T> clazz;
    String field;
    Predicate<TransformationContext> condition;
    Supplier<Object> action;

    public OutputOverride(Class<T> clazz, String field, Predicate<TransformationContext> condition, Supplier<Object> action) {
        this.clazz = clazz;
        this.field = field;
        this.condition = condition;
        this.action = action;
    }
}
