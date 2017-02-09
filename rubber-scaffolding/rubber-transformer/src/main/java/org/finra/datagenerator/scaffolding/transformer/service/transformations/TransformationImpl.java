package org.finra.datagenerator.scaffolding.transformer.service.transformations;

import org.finra.datagenerator.scaffolding.transformer.limitation.Limitation;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;
import org.finra.datagenerator.scaffolding.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dkopel on 11/29/16.
 */
public class TransformationImpl implements Comparable<TransformationImpl> {
    protected TransformationImpl(boolean emptyString, boolean isNull, String value, FunctionTransformationImpl function, String condition, Limitation[] limits, long order) {
        this.emptyString = emptyString;
        this.isNull = isNull;
        this.value = value;
        this.function = function;
        this.condition = condition;
        this.limits = limits;
        this.order = order;
    }

    public static Field toField(Class clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransformationImpl(Transformation transformation) {
        this.emptyString = transformation.emptyString();
        this.isNull = transformation.isNull();
        this.value = transformation.value();
        this.function = new FunctionTransformationImpl(transformation.function());
        this.condition = transformation.condition();

        Limitation[] lis = new Limitation[transformation.limits().length];
        this.limits = Stream.of(transformation.limits()).map(l -> {
                try {
                    return ClassUtils.createNewInstance(l.value(), l.args(), l.classes());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                return null;
            }
        ).collect(Collectors.toList()).toArray(lis);
        this.order = transformation.order();
    }

    private final long order;

    private final boolean emptyString;

    private final boolean isNull;

    private final String value;

    private final FunctionTransformationImpl function;

    private final String condition;

    private final Limitation[] limits;

    public boolean isEmptyString() {
        return emptyString;
    }

    public boolean isNull() {
        return isNull;
    }

    public String getValue() {
        return value;
    }

    public FunctionTransformationImpl getFunction() {
        return function;
    }

    public String getCondition() {
        return condition;
    }

    public <T> Limitation<T>[] getLimits() {
        return limits;
    }

    public long getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransformationImpl)) return false;

        TransformationImpl that = (TransformationImpl) o;

        if (emptyString != that.emptyString) return false;
        if (isNull != that.isNull) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (function != null ? !function.equals(that.function) : that.function != null) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(limits, that.limits);

    }

    @Override
    public int hashCode() {
        int result = (emptyString ? 1 : 0);
        result = 31 * result + (isNull ? 1 : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(limits);
        return result;
    }

    @Override
    public int compareTo(TransformationImpl o) {
        return Long.valueOf(getOrder()).compareTo(Long.valueOf(o.getOrder()));
    }
}
