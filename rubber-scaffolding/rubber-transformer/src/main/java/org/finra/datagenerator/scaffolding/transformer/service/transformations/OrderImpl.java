package org.finra.datagenerator.scaffolding.transformer.service.transformations;

/**
 * Created by dkopel on 12/1/16.
 */
public class OrderImpl implements Comparable<OrderImpl> {
    private final long order;
    private final String condition;

    public OrderImpl(long order, String condition) {
        this.order = order;
        this.condition = condition;
    }

    public OrderImpl(long order) {
        this.order = order;
        this.condition = null;
    }

    public long getOrder() {
        return order;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public int compareTo(OrderImpl o) {
        return Long.valueOf(order).compareTo(Long.valueOf(o.getOrder()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderImpl)) return false;

        OrderImpl order1 = (OrderImpl) o;

        if (order != order1.order) return false;
        return condition != null ? condition.equals(order1.condition) : order1.condition == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (order ^ (order >>> 32));
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }
}
