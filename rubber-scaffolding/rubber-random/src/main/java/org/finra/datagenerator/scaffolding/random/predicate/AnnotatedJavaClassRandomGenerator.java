package org.finra.datagenerator.scaffolding.random.predicate;

import org.finra.datagenerator.scaffolding.config.AnnotationCapable;

/**
 * Created by dkopel on 2/14/17.
 */
public abstract class AnnotatedJavaClassRandomGenerator<T>
    extends JavaClassRandomGenerator<T>
    implements AnnotationCapable {}