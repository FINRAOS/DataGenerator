package org.finra.datagenerator.scaffolding.transformer.service;

import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by dkopel on 11/17/16.
 */
public interface MultiTransformerService {
    void scan(String basePackage);

    <V extends FunctionTransformation> void setFunctionTransformation(String key, Class<V> clazz, Object[] args, Class[] argClasses);

    <V extends FunctionTransformation> void setFunctionTransformation(String key, Class<V> clazz);

    void registerTransformations(Set<TransformationsImpl> transformations);

    <V extends FunctionTransformation> FunctionTransformationContainer<V> getFunctionTransformation(String key, Class<V> clazz);

    <V extends FunctionTransformation> List<FunctionTransformationContainer<V>> getFunctionTransformation(Class<V> clazz);

    <T> Map<Long, ? extends Collection<TransformationContainer>> orderedTransform(
        Map<Long, List<TransformationContainer>> containers,
        // Iterations: number of iterations to invoke
        Long iterations
    );

    <T> Map<Long, ? extends Collection<TransformationContainer>> orderedTransform(
        Map<Long, List<TransformationContainer>> containers,
        // Iterations: number of iterations to invoke
        Long iterations,
        // Overrides
        Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> overrides
    );
}
