package org.finra.datagenerator.scaffolding.random.support.annotations;

import org.finra.datagenerator.scaffolding.random.predicate.RandomGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dkopel on 1/10/17.
 */
@Target(value={ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomRandomizer {
    Class<? extends RandomGenerator<?>> value();
    long priority() default Long.MAX_VALUE-1;
}