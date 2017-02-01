package org.finra.scaffolding.transformer.function;

import org.finra.scaffolding.transformer.service.TransformationContext;

/**
 * Created by dkopel on 11/17/16.
 */
public interface FunctionTransformation<V> {
    V next(TransformationContext context);
}