package org.finra.datagenerator.scaffolding.transformer.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.OrderImpl;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationImpl;
import org.finra.datagenerator.scaffolding.transformer.service.transformations.TransformationsImpl;
import org.finra.datagenerator.scaffolding.transformer.support.Order;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;
import org.finra.datagenerator.scaffolding.transformer.support.Transformations;
import org.finra.datagenerator.scaffolding.utils.ClassPathScanner;
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils.AnnotationAssociation;
import org.finra.datagenerator.scaffolding.utils.SimpleClassPathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finra.datagenerator.scaffolding.utils.ReflectionUtils.findAnnotationsWithAssociation;

/**
 * Created by dkopel on 9/30/16.
 */

/*
Input always represents the object that
 */
public class TransformerUtils {
    private final static Logger logger = LoggerFactory.getLogger(TransformerUtils.class);
    private final static Set<Class<FunctionTransformation>> functionTransformerClasses = new HashSet();
    private final static ExpressionParser parser = new SpelExpressionParser();


    public static <T extends java.lang.annotation.Annotation> Field getField(AnnotationAssociation<T> target, Class<T> clazz) {
        List<Field> fs = target.getAssociations()
            .stream()
            .filter(as -> as instanceof Field)
            .map(a -> ((Field) a))
            .collect(Collectors.toList());
        if(fs.size() == 1) {
            return fs.get(0);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Set<Class<FunctionTransformation>> findFunctionTransformers(String basePackage) {
        ClassPathScanner classPathScanner = new SimpleClassPathScanner(basePackage);
        classPathScanner.addIncludeFilter(new AssignableTypeFilter(FunctionTransformation.class));
        for(BeanDefinition bd : classPathScanner.findComponents()) {
            try {
                Class cl = Class.forName(bd.getBeanClassName());
                if(!Modifier.isAbstract(cl.getModifiers())) {
                    logger.debug("Adding transformer function {}", cl);
                    functionTransformerClasses.add(cl);
                } else {
                    logger.debug("Ignoring transformer function {}", cl);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return functionTransformerClasses;
    }

    public static Object parseExpression(String value, StandardEvaluationContext context) {
        logger.debug("TransformField: {}", value);
        return parser.parseExpression(value).getValue(context);
    }

    public static List<Map.Entry<Field, Transformation[]>> processTransformations(Class clazz) {
        // Find all `Transformation` annotations for the class
        return Lists.newArrayList(findAnnotationsWithAssociation(clazz, Transformations.class)
            .stream().collect(
                Collectors.toMap(
                    t -> TransformerUtils.getField(t, Transformations.class),
                    t -> t.getAnnotation().value()
                )
            ).entrySet())
            .stream()
            .collect(Collectors.toList());
    }

    public static boolean evaluateCondition(String condition, StandardEvaluationContext context) {
        return (condition != null && condition.length() > 0 && (boolean) TransformerUtils.parseExpression(condition, context))
            || condition == null || condition.isEmpty();
    }

    public static Map<Field, Set<OrderImpl>> getFields(Class clazz) {
        // Find all `Transformation` annotations for the class
        Set<Field> fields = Sets.newHashSet(clazz.getDeclaredFields());
        Map<Field, Set<OrderImpl>> tos = findAnnotationsWithAssociation(clazz, Order.class)
            .stream()
            .map(t -> {
                Order o = t.getAnnotation();
                return new AbstractMap.SimpleEntry<>(
                        TransformerUtils.getField(t, Order.class),
                        new OrderImpl(o.value(), o.condition())
                    );
                }
            )
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet()))
            );
        for(Field f : fields) {
            if(!tos.containsKey(f)) {
                tos.put(f, Sets.newHashSet());
            }
        }
        return tos;
    }

    public static Map<Field, Set<TransformationImpl>> getTransformations(Class clazz) {
        Set<Field> fields = Sets.newHashSet(clazz.getDeclaredFields());
        Map<Field, Set<TransformationImpl>> tos = findAnnotationsWithAssociation(clazz, Transformation.class)
            .stream()
            .map(t -> new AbstractMap.SimpleEntry<>(
                TransformerUtils.getField(t, Transformation.class),
                new TransformationImpl(t.getAnnotation())
            ))
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet()))
            );
        for(Field f : fields) {
            if(!tos.containsKey(f)) {
                tos.put(f, Sets.newHashSet());
            }
        }
        return tos;
    }

    public static Set<OrderImpl> getFieldOrders(Field field) {
        return getFields(field.getDeclaringClass()).get(field);
    }

    public static Set<TransformationImpl> getFieldTransformations(Field field) {
        return getTransformations(field.getDeclaringClass()).get(field);
    }

    public static Set<TransformationsImpl> getAllTransformations(Class clazz) {
        Map<Field, Set<OrderImpl>> os = getFields(clazz);
        Map<Field, Set<TransformationImpl>> ts = getTransformations(clazz);

        return os.keySet().stream().map(
            f -> new TransformationsImpl(f, os.get(f), ts.get(f))
        ).collect(Collectors.toSet());
    }

    public static List<Field> getFields(Class clazz, StandardEvaluationContext context) {
        return getFields(getFields(clazz), context);
    }

    public static List<Field> getFields(Map<Field, Set<OrderImpl>> fields, StandardEvaluationContext context) {
        return fields.entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    o1 -> o1.getKey(),
                    o1 -> o1.getValue()
                        .stream()
                        .filter(o -> TransformerUtils.evaluateCondition(o.getCondition(), context))
                        .map(o -> o.getOrder())
                        .min(Long::compare).orElseGet(()->Long.MAX_VALUE)
                )
            ).entrySet()
            .stream()
            .sorted((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
            .map(o -> o.getKey())
            .collect(Collectors.toList());
    }

    public static Optional<TransformationImpl> getTransformation(Field field, StandardEvaluationContext context) {
        return Stream.of(field.getAnnotationsByType(Transformation.class))
            .map(TransformationImpl::new)
            .sorted((o1, o2) -> o1.compareTo(o2))
            .filter(o -> TransformerUtils.evaluateCondition(o.getCondition(), context))
            .findFirst();
    }
}