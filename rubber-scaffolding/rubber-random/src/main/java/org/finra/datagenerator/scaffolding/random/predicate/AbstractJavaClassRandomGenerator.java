package org.finra.datagenerator.scaffolding.random.predicate;

import scala.runtime.AbstractFunction1;

/**
 * Created by dkopel on 2/14/17.
 */
public abstract class AbstractJavaClassRandomGenerator<T>
    extends AbstractFunction1<RandomContext, T>
    implements RandomGenerator<T> {}
