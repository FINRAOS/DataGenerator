package org.finra.datagenerator.scaffolding.transformer.support;

import org.finra.datagenerator.scaffolding.transformer.TransformDirection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dkopel on 9/30/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TransformClass {
    String label();
    Class type();
    TransformDirection direction();
}
