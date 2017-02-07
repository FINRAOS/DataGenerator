package org.finra.datagenerator.scaffolding.transformer.function.impl;

import org.finra.datagenerator.scaffolding.transformer.function.Direction;
import org.finra.datagenerator.scaffolding.transformer.function.OrderedFunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.service.TransformationContext;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by dkopel on 11/21/16.
 */
public class DateTimeSequential extends OrderedFunctionTransformation<LocalDateTime> {
    private final LocalDateTime start;
    private final Duration step;
    private final Direction direction;
    private final ZoneOffset offset;
    private final AtomicLong current = new AtomicLong();

    public DateTimeSequential(LocalDateTime start, Duration step, ZoneOffset offset, Direction direction) {
        this.start = start;
        this.step = step;
        this.direction = direction;
        this.offset = offset;
        this.current.set(start.toEpochSecond(this.offset));
    }

    public DateTimeSequential(LocalDateTime start, Duration step, Direction direction) {
        this.start = start;
        this.step = step;
        this.direction = direction;
        this.offset = ZoneOffset.UTC;
        this.current.set(start.atOffset(this.offset).toInstant().toEpochMilli());
    }

    public DateTimeSequential(LocalDateTime start, Duration step) {
        this.start = start;
        this.step = step;
        this.direction = Direction.ASCENDING;
        this.offset = ZoneOffset.UTC;
        this.current.set(start.atOffset(this.offset).toInstant().toEpochMilli());
    }

    public DateTimeSequential(LocalDateTime start) {
        this.start = start;
        this.step = Duration.ofDays(1);
        this.direction = Direction.ASCENDING;
        this.offset = ZoneOffset.UTC;
        this.current.set(start.atOffset(this.offset).toInstant().toEpochMilli());
    }

    public DateTimeSequential() {
        this.start = LocalDateTime.now();
        this.step = Duration.ofDays(1);
        this.direction = Direction.ASCENDING;
        this.offset = ZoneOffset.UTC;
        this.current.set(start.atOffset(this.offset).toInstant().toEpochMilli());
    }

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public Duration getStep() {
        return Duration.ofDays(1);
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public LocalDateTime next(TransformationContext context) {
        if(direction.equals(Direction.ASCENDING)) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(current.getAndAdd(step.toMillis())), this.offset.normalized());
        } else {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(current.getAndAdd(step.toMillis() * -1)), this.offset.normalized());
        }
    }
}
