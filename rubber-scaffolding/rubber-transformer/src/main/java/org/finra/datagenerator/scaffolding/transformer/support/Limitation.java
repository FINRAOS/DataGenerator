package org.finra.datagenerator.scaffolding.transformer.support;

/**
 * Created by dkopel on 11/22/16.
 */
public @interface Limitation {
    Class<? extends org.finra.datagenerator.scaffolding.transformer.limitation.Limitation> value() default org.finra.datagenerator.scaffolding.transformer.limitation.Limitation.class;
    String[] args() default {};
    Class[] classes() default {};
}