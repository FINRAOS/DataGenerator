package org.finra.datagenerator.scaffolding.transformer.service;

import org.finra.datagenerator.scaffolding.transformer.TransformDirection;

/**
 * Created by dkopel on 11/21/16.
 */
public abstract class TransformationContainer<T> implements Comparable<TransformationContainer<T>> {
    public final String alias;
    public final Class<T> clazz;
    public final T value;
    public final TransformDirection direction;
    public final Long order;
    public final Boolean join;

    public TransformationContainer(String alias, Class<T> clazz, TransformDirection direction, Long order) {
        this.alias = alias;
        this.clazz = clazz;
        this.value = null;
        this.direction = direction;
        this.order = order;
        this.join = false;
    }

    public TransformationContainer(String alias, Class<T> clazz, TransformDirection direction, Long order, Boolean join) {
        this.alias = alias;
        this.clazz = clazz;
        this.value = null;
        this.direction = direction;
        this.order = order;
        this.join = join;
    }

    public TransformationContainer(String alias, T value, TransformDirection direction, Long order) {
        this.alias = alias;
        this.clazz = (Class<T>) value.getClass();
        this.value = value;
        this.direction = direction;
        this.order = order;
        this.join = false;
    }

    public TransformationContainer(String alias, T value, TransformDirection direction, Long order, Boolean join) {
        this.alias = alias;
        this.clazz = (Class<T>) value.getClass();
        this.value = value;
        this.direction = direction;
        this.order = order;
        this.join = join;
    }

    public TransformationContainer(String alias, Class<T> clazz, T value, TransformDirection direction, Long order) {
        this.alias = alias;
        this.clazz = clazz;
        this.value = value;
        this.direction = direction;
        this.order = order;
        this.join = false;
    }

    public TransformationContainer(String alias, Class<T> clazz, T value, TransformDirection direction, Long order, Boolean join) {
        this.alias = alias;
        this.clazz = clazz;
        this.value = value;
        this.direction = direction;
        this.order = order;
        this.join = join;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransformationContainer)) return false;

        TransformationContainer<?> that = (TransformationContainer<?>) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (direction != that.direction) return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        return join != null ? join.equals(that.join) : that.join == null;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (join != null ? join.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(TransformationContainer<T> o) {
        int j = join.compareTo(o.join);
        if(j != 0) return j;
        return order.compareTo(o.order);
    }
}
