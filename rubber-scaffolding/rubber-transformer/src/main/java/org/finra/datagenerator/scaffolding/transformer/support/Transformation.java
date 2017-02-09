package org.finra.datagenerator.scaffolding.transformer.support;

import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;

import java.lang.annotation.*;

/**
 * Created by dkopel on 9/27/16.
 */

/**
 * boolean emptyString() - When set to `true` the value will be an empty string ""
 * boolean isNull() - When set to `true` the value will be NULL
 * String value() - Uses the value of the SpEL expression as the value
 */
@Repeatable(org.finra.datagenerator.scaffolding.transformer.support.Transformations.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transformation {
    long order() default 0L;
    boolean emptyString() default false;
    boolean isNull() default false;
    String value() default "";
    org.finra.datagenerator.scaffolding.transformer.support.FunctionTransformation function() default @org.finra.datagenerator.scaffolding.transformer.support.FunctionTransformation(key="", clazz=FunctionTransformation.class);
    String condition() default "";
    org.finra.datagenerator.scaffolding.transformer.support.Limitation[] limits() default {};
}
