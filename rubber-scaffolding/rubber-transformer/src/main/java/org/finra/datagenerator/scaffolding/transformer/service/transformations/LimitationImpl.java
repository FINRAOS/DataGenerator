package org.finra.datagenerator.scaffolding.transformer.service.transformations;

import org.finra.datagenerator.scaffolding.transformer.limitation.Limitation;

import java.util.Arrays;

/**
 * Created by dkopel on 11/29/16.
 */
public class LimitationImpl {
    protected LimitationImpl(Class<? extends Limitation> value, String[] args, Class[] classes) {
        this.value = value;
        this.args = args;
        this.classes = classes;
    }

    private final Class<? extends Limitation> value;

    private final String[] args;

    private final Class[] classes;

    public Class<? extends Limitation> getValue() {
        return value;
    }

    public String[] getArgs() {
        return args;
    }

    public Class[] getClasses() {
        return classes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimitationImpl)) return false;

        LimitationImpl that = (LimitationImpl) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(args, that.args)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(classes, that.classes);

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + Arrays.hashCode(classes);
        return result;
    }
}
