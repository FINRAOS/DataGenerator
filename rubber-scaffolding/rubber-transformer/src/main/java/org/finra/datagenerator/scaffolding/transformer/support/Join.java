package org.finra.datagenerator.scaffolding.transformer.support;

import java.lang.annotation.*;

/**
 * Created by dkopel on 12/15/16.
 */
@Repeatable(org.finra.datagenerator.scaffolding.transformer.support.Joins.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Join {
    Class value();
    JoinField[] fields();
    String alias() default "";
}