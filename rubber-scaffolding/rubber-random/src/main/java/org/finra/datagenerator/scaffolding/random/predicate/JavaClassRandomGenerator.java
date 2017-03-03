package org.finra.datagenerator.scaffolding.random.predicate;

/**
 * Created by dkopel on 1/27/17.
 */
@FunctionalInterface
public interface JavaClassRandomGenerator<T> {
    T apply(RandomContext rc);

    default Class[] classes() {
        return new Class[]{};
    }

    default long priority() {
        return Long.MAX_VALUE;
    }
}
