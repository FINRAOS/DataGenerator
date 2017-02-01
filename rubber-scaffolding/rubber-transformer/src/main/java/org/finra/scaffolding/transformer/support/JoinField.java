package org.finra.scaffolding.transformer.support;

/**
 * Created by dkopel on 12/15/16.
 */
public @interface JoinField {
    String key() default "";
    String field() default "";
}
