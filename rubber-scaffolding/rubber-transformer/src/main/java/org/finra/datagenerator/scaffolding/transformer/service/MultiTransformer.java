package org.finra.datagenerator.scaffolding.transformer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.finra.datagenerator.scaffolding.random.core.RubberRandom;
import org.finra.datagenerator.scaffolding.random.core.RubberRandomImpl;
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl;
import org.finra.datagenerator.scaffolding.transformer.utils.TransformerUtils;
import org.finra.datagenerator.scaffolding.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by dkopel on 11/17/16.
 */
@Service
public class MultiTransformer implements MultiTransformerService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RubberRandom rubberRandom;
    private final ObjectMapper objectMapper;
    private final TransformerService transformerService;
    private final TransformationSessionType defaultSessionType = TransformationSessionType.MERGE;

    private final String defaultBasePackage = "org.finra.datagenerator.scaffolding.transformer";

    private final Set<Class<? extends FunctionTransformation>> functionTransformerClasses = new HashSet();
    private final Set<FunctionTransformationContainer> functionTransformers = new HashSet();

    @Autowired(required = false)
    public MultiTransformer(RubberRandom rubberRandom, ObjectMapper objectMapper, TransformerService transformerService) {
        this.rubberRandom = rubberRandom;
        this.objectMapper = objectMapper;
        this.transformerService = transformerService;
        scan(defaultBasePackage);
    }

    public MultiTransformer() {
        this.rubberRandom = RubberRandomImpl.apply();
        this.objectMapper = new ObjectMapper();
        //this.transformerService = new Transformer(randomProvider, objectMapper);
        //this.transformerService = new TransformerPlus();
        this.transformerService = null;
        scan(defaultBasePackage);
    }

    @Override
    public void scan(String basePackage) {
        functionTransformerClasses.addAll(TransformerUtils.findFunctionTransformers(basePackage));
        functionTransformerClasses.forEach(ftc -> {
            try {
                defaultTransformer(ftc);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    private <V extends FunctionTransformation<V>> FunctionTransformationContainer<V> defaultTransformer(Class<? extends V> clazz) throws IllegalAccessException, InstantiationException {
        V inst = clazz.newInstance();
        FunctionTransformationContainer container = new FunctionTransformationContainer<V>("default", clazz, inst);
        this.functionTransformers.add(container);
        return container;
    }

    @Override
    public <V extends FunctionTransformation> List<FunctionTransformationContainer<V>> getFunctionTransformation(Class<V> clazz) {
        List fct = functionTransformers.stream().filter(fc -> fc.clazz.equals(clazz)).collect(Collectors.toList());
        if(fct.size() > 0) {
            return fct;
        } else if(functionTransformerClasses.contains(clazz)) {
            try {
                return Lists.newArrayList(defaultTransformer(clazz));
            } catch(Exception e) {
                throw new IllegalStateException("Unable to instantiate the function transformation with class "+clazz);
            }
        } else {
            throw new IllegalArgumentException("A class with that name that implements the FunctionTransformation interface cannot be found on the classpath.");
        }
    }

    @Override
    public <V extends FunctionTransformation> FunctionTransformationContainer<V> getFunctionTransformation(String key, Class<V> clazz) {
        List<FunctionTransformationContainer<V>> cs = functionTransformers.stream()
            .filter(ft -> ft.key.equals(key) && ft.clazz.equals(clazz))
            .map(ft -> (FunctionTransformationContainer<V>) ft)
            .collect(Collectors.toList());
        if(cs.size() > 0) {
            return cs.iterator().next();
        }
        throw new NoSuchElementException();
    }

    @Override
    public <V extends FunctionTransformation> void setFunctionTransformation(String key, Class<V> clazz) {
        setFunctionTransformation(key, clazz, new Object[]{}, new Class[]{});
    }

    @Override
    public <V extends FunctionTransformation> void setFunctionTransformation(String key, Class<V> clazz, Object[] args, Class[] argClasses) {
        V in = null;
        if(functionTransformerClasses.contains(clazz)) {
            logger.debug("Have the arguments: {} with classes {}", args, argClasses);
            try {
                in = ClassUtils.createNewInstance(clazz, args, argClasses);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                in = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        functionTransformers.add(new FunctionTransformationContainer<V>(key, clazz, in));
    }

    @Override
    public void registerTransformations(Set<TransformationsImpl> transformations) {
        transformerService.registerTransformations(transformations);
    }

    @Override
    public <T> Map<Long, ? extends Collection<TransformationContainer>> orderedTransform(Map<Long, List<TransformationContainer>> containers, Long iterations) {
        return orderedTransform(containers, iterations, new HashMap<>());
    }

    @Override
    public <T> Map<Long, ? extends Collection<TransformationContainer>> orderedTransform(Map<Long, List<TransformationContainer>> containers, Long iterations, Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> overrides) {
        final Map<Long, Collection<TransformationContainer>> nc = TransformationContext.convert(containers);
        LongStream.range(0, iterations).forEachOrdered(current -> {
            logger.debug("Invoking transformation #{}", current);
            transformerService.transform(
                new TransformationContext(
                    current,
                    nc,
                    functionTransformers,
                    defaultSessionType,
                    overrides
                )
            );
        });
        return nc;
    }
}
