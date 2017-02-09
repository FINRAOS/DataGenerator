package org.finra.datagenerator.scaffolding.transformer.service.transformations;

import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;

/**
 * Created by dkopel on 11/29/16.
 */
public class FunctionTransformationImpl {
    protected FunctionTransformationImpl(String key, Class<? extends FunctionTransformation> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    protected FunctionTransformationImpl(org.finra.datagenerator.scaffolding.transformer.support.FunctionTransformation func) {
        this.key = func.key();
        this.clazz = func.clazz();
    }

    private final String key;

    private final Class<? extends FunctionTransformation> clazz;

    public String getKey() {
        return key;
    }

    public Class<? extends FunctionTransformation> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionTransformationImpl)) return false;

        FunctionTransformationImpl that = (FunctionTransformationImpl) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return clazz != null ? clazz.equals(that.clazz) : that.clazz == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        return result;
    }
}
