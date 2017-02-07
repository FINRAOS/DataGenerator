package org.finra.datagenerator.scaffolding.transformer.function.impl;

import org.finra.datagenerator.scaffolding.transformer.function.Direction;
import org.finra.datagenerator.scaffolding.transformer.function.NumericSequential;
import org.finra.datagenerator.scaffolding.transformer.service.TransformationContext;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dkopel on 11/18/16.
 */
public class LongSequential extends NumericSequential<Long> {
    private final Long start;
    private final Long step;
    private final AtomicLong current;
    private final Direction direction;

    public LongSequential(Long start, Long step, Direction direction) {
        Assert.notNull(start, "A starting value is required.");
        Assert.notNull(step, "An incremental step value is required.");
        Assert.notNull(direction, "An iteration direction is required.");
        this.start = start;
        this.step = step;
        this.direction = direction;
        this.current = new AtomicLong(start);
    }

    public LongSequential(Long start, Long step) {
        this(start, step, Direction.ASCENDING);
    }

    public LongSequential() {
        this.start = 0L;
        this.step = 1L;
        this.current = new AtomicLong(start);
        this.direction = Direction.ASCENDING;
    }

    @Override
    public Long getStart() {
        return start;
    }

    @Override
    public Long getStep() {
        return step;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Long next(TransformationContext context) {
        if(direction.equals(Direction.ASCENDING)) {
            return current.getAndAdd(step);
        } else {
            return current.getAndAdd(step * -1);
        }
    }
}
