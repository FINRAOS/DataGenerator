package org.finra.datagenerator.scaffolding.transformer.service;

import com.google.common.collect.ImmutableMap;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl;
import org.finra.datagenerator.scaffolding.utils.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * Created by dkopel on 9/30/16.
 */
public interface TransformerService {
    TransformationSessionType defaultSessionType = TransformationSessionType.MERGE;

    Map<String, Method> defaultContextMethods = ImmutableMap.<String, Method>builder()
        .put("asList", ClassUtils.getMethod(Arrays.class, "asList", new Class[] { Object[].class }))
        .build();

    void registerTransformations(Set<TransformationsImpl> transformations);

    void registerContextFunction(String methodName, Method method);

    default List<TransformationContainer> transform(List<TransformationContainer> containers) {
        return transform(containers, defaultSessionType);
    }

    <T> Collection<T> filterData(Collection<T> data, Function<T, Boolean> filter);

    List<TransformationContainer> transform(List<TransformationContainer> container, TransformationSessionType sessionType);

    List<TransformationContainer> transform(TransformationContext tContext);

    void setGlobal(String key, Object value);

    void setGlobals(Map<String, Object> value);

    Object lookupGlobal(String key);

    Map<String, Object> getGlobals();

    String toString(Object obj);
}