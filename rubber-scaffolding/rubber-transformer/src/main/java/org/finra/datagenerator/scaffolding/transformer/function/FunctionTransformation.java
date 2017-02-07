package org.finra.datagenerator.scaffolding.transformer.function;

import org.finra.datagenerator.scaffolding.transformer.service.TransformationContext;

/**
 * Created by dkopel on 11/17/16.
 */
public interface FunctionTransformation<V> {
    V next(TransformationContext context);
}