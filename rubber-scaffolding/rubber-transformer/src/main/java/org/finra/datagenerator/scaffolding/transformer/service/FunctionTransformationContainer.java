package org.finra.datagenerator.scaffolding.transformer.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;

/**
 * Created by dkopel on 11/18/16.
 */
public class FunctionTransformationContainer<V extends FunctionTransformation> {
    public final String key;
    public final Class<? extends V> clazz;
    public final V inst;

    public FunctionTransformationContainer(String key, Class<? extends V> clazz, V inst) {
        this.key = key;
        this.clazz = clazz;
        this.inst = inst;
    }

    public FunctionTransformationContainer(Class<? extends V> clazz, V inst) {
        this.key = RandomStringUtils.randomAscii(10);
        this.clazz = clazz;
        this.inst = inst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionTransformationContainer)) return false;

        FunctionTransformationContainer that = (FunctionTransformationContainer) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
        return inst != null ? inst.equals(that.inst) : that.inst == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        result = 31 * result + (inst != null ? inst.hashCode() : 0);
        return result;
    }
}
