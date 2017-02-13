package org.finra.datagenerator.scaffolding.random.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.finra.datagenerator.scaffolding.random.support.annotations.RandomConfigAnnotation;
/** Auto-generated **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@RandomConfigAnnotation(value=org.finra.datagenerator.scaffolding.random.randomizers.LongRandomizer.class)
public @interface LongRange {
	long min() default 0L;
	long max() default 0L;
}