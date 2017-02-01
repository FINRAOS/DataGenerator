package org.finra.scaffolding.transformer.support;

import java.lang.annotation.*;

/**
 * Created by dkopel on 12/1/16.
 */
@Repeatable(org.finra.scaffolding.transformer.support.TransformationOrders.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Order {
    long value() default Long.MAX_VALUE;
    String condition() default "";
}
