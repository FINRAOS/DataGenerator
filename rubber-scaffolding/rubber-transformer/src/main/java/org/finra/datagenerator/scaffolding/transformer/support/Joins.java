package org.finra.datagenerator.scaffolding.transformer.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dkopel on 12/28/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Joins {
    Join[] value() default {};
    String condition() default "";
}
