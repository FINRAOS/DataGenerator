package org.finra.datagenerator.scaffolding.random;


/**
 * Created by dkopel on 11/28/16.
 */
public class Buffet<V> {
    private V hungry;

    private Boolean isFull;

    public V getHungry() {
        return hungry;
    }

    public Boolean getFull() {
        return isFull;
    }
}
