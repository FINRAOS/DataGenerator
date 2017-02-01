package org.finra.scaffolding.random.support.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/** Auto-generated **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@RandomConfigAnnotation(value=org.finra.scaffolding.random.randomizers.FloatRandomizer.class)
public @interface FloatRange {
	float min() default 0F;
	float max() default 0F;
}