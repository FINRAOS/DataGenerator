package org.finra.datagenerator.scaffolding.transformer.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dkopel on 9/27/16.
 */

/*
long order() - The order that the transformation should be invoked.
                This is processed in ascending order.
Transformation[] value() - The transformations to evaluate and apply to the specified target
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transformations {
    Transformation[] value();
}