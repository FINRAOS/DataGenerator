package org.finra.datagenerator.scaffolding.transformer.service.transformations;

import com.google.common.collect.Lists;
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.limitation.Limitation;
import org.finra.datagenerator.scaffolding.transformer.support.Transformation;
import org.finra.datagenerator.scaffolding.utils.ClassUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dkopel on 11/29/16.
 */
public class TransformationsImpl {

    public static class MultiBuilder {
        Set<TransformationsImpl> ts = new HashSet();

        public Builder withField(Field field) {
            return new Builder(this, field);
        }

        public Set<TransformationsImpl> build() {
            return ts;
        }
    }

    public static class Builder {
        private final MultiBuilder multiBuilder;
        private Field field;
        private Set<OrderImpl> orders = new HashSet();
        private Set<TransformationImpl> value = new HashSet();

        private Builder(MultiBuilder multiBuilder, Field field) {
            this.multiBuilder = multiBuilder;
            this.field = field;
        }

        public Builder withOrder(long order, String condition) {
            this.orders.add(new OrderImpl(order, condition));
            return this;
        }

        public Builder withOrder(OrderImpl order) {
            this.orders.add(order);
            return this;
        }

        public TransformationsBuilder withTransformations() {
            return new TransformationsBuilder(this);
        }

        public Builder withTransformations(Transformation... ts) {
            for(Transformation t : ts) {
                this.value.add(new TransformationImpl(t));
            }
            return this;
        }

        public TransformationsImpl get() {
            return new TransformationsImpl(field, orders, value);
        }

        public MultiBuilder build() {
            if(orders.size() == 0) {
                orders.add(new OrderImpl(Long.MAX_VALUE));
            }

            multiBuilder.ts.add(new TransformationsImpl(field, orders, value));
            return multiBuilder;
        }

        public static class TransformationsBuilder {
            private final Builder builder;
            private List<TransformationImpl> transformations = new ArrayList();

            private TransformationsBuilder(Builder builder) {
                this.builder = builder;
            }

            public TransformationBuilder withTransformation() {
                return new TransformationBuilder(this);
            }

            public Builder withTransformation(Transformation... ts) {
                for(Transformation t : ts) {
                    transformations.add(new TransformationImpl(t));
                }
                return builder;
            }

            public Builder build() {
                builder.value.addAll(transformations);
                return builder;
            }
        }

        public static class TransformationBuilder {
            private final TransformationsBuilder builder;

            private TransformationBuilder(TransformationsBuilder builder) {
                this.builder = builder;
            }

            private long order = Long.MAX_VALUE;

            private boolean emptyString = false;

            private boolean isNull = false;

            private String value = "";

            private FunctionTransformationImpl function = null;

            private String condition = "";

            private List<Limitation> limits = new ArrayList();

            public TransformationBuilder withValue(String value) {
                Assert.notNull(value);
                this.emptyString = false;
                this.isNull = false;
                this.value = value;
                this.function = null;
                return this;
            }

            public TransformationBuilder withOrder(long order) {
                this.order = order;
                return this;
            }

            public TransformationBuilder withEmptyString() {
                this.emptyString = true;
                this.isNull = false;
                this.value = "";
                this.function = null;
                return this;
            }

            public TransformationBuilder withNull() {
                this.isNull = true;
                this.emptyString = false;
                this.value = "";
                this.function = null;
                return this;
            }

            public TransformationBuilder withFunctionTransformation(String key, Class<? extends FunctionTransformation> clazz) {
                Assert.notNull(key);
                Assert.notNull(clazz);
                this.function = new FunctionTransformationImpl(key, clazz);
                return this;
            }

            public TransformationBuilder withCondition(String condition) {
                this.condition = condition;
                return this;
            }

            public TransformationBuilder withLimitation(Class<? extends Limitation> value, String[] args, Class[] classes) throws InstantiationException {
                LimitationImpl li = new LimitationImpl(value, args, classes);
                Limitation limit = ClassUtils.createNewInstance(li.getValue(), li.getArgs(), li.getClasses());
                limits.add(limit);
                return this;
            }

            public TransformationBuilder withLimitions(Limitation... limitions) {
                limits.addAll(Lists.newArrayList(limitions));
                return this;
            }

            public TransformationsBuilder build() {
                TransformationImpl ti = new TransformationImpl(
                    emptyString,
                    isNull,
                    value,
                    function,
                    condition,
                    limits.toArray(new Limitation[limits.size()]),
                    order
                );

                builder.transformations.add(ti);
                return builder;
            }

        }
    }

    public static Builder singleBuilder(Field field) {
        MultiBuilder m = new MultiBuilder();
        return m.withField(field);
    }

    public static MultiBuilder multiBuilder() {
        return new MultiBuilder();
    }

    public TransformationsImpl(Field field, Set<OrderImpl> orders, Set<TransformationImpl> value) {
        this.field = field;
        this.orders = orders;
        this.value = value;
    }

    private final Field field;

    private final Set<OrderImpl> orders;

    private final Set<TransformationImpl> value;

    public Field getField() {
        return field;
    }

    public Set<OrderImpl> getOrders() {
        return orders;
    }

    public Set<TransformationImpl> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransformationsImpl)) return false;

        TransformationsImpl that = (TransformationsImpl) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (orders != null ? !orders.equals(that.orders) : that.orders != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
