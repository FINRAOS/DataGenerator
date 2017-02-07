package org.finra.datagenerator.scaffolding.transformer.limitation;

/**
 * Created by dkopel on 11/22/16.
 */
public interface Limitation<T> {
    boolean isValid(T input);
}
