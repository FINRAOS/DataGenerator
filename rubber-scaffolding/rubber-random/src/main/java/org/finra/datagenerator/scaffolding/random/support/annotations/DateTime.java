package org.finra.datagenerator.scaffolding.random.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.finra.datagenerator.scaffolding.random.support.annotations.RandomConfigAnnotation;
/** Auto-generated **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@RandomConfigAnnotation(value=org.finra.datagenerator.scaffolding.random.randomizers.DateTimeRandomizer.class)
public @interface DateTime {
	long minEpochLongSeconds() default 0L;
	long maxEpochLongSeconds() default 0L;
	String dateYears() default "";
}