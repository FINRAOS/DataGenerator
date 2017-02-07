package org.finra.datagenerator.scaffolding.transformer.service;

import org.finra.datagenerator.scaffolding.transformer.TransformDirection;
import org.finra.datagenerator.scaffolding.transformer.function.FunctionTransformation;
import org.finra.datagenerator.scaffolding.transformer.service.TransformationsProvider$;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by dkopel on 11/17/16.
 */
public class TransformationContext {
    public final Long iteration;
    public final Map<Long, java.util.Collection<TransformationContainer>> containers;
    public final Set<FunctionTransformationContainer> functionTransformations;
    public final Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> overrides = new HashMap();
    public final TransformationSessionType sessionType;

    Class<?> currentClass;
    Object currentInstance;
    Field field;

    public TransformationContext(Long iteration, Map<Long, java.util.Collection<TransformationContainer>> containers, Set<FunctionTransformationContainer> functionTransformations, TransformationSessionType sessionType, Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> overrides) {
        this.iteration = iteration;
        this.containers = containers;
        this.functionTransformations = functionTransformations;
        this.overrides.putAll(overrides);
        this.sessionType = sessionType;
    }

    public TransformationContext(Long iteration, Map<Long, java.util.Collection<TransformationContainer>> containers, Set<FunctionTransformationContainer> functionTransformations) {
        this(iteration, containers, functionTransformations, TransformationsProvider$.MODULE$.defaultSessionType(), new HashMap<>());
    }

    public <S> void setCurrentInstance(S inst) {
        this.currentInstance = inst;
    }

    public <S> void setCurrentClass(Class<S> clazz) {
        this.currentClass = clazz;
    }

    public Long getIteration() {
        return iteration;
    }

    public Boolean hasFunctionTransformation(String key, Class<? extends FunctionTransformation> clazz) {
        return functionTransformations
            .stream()
            .filter(ft -> ft.key.equals(key) && ft.clazz.equals(clazz))
            .count() > 0;
    }

    public Boolean hasFunctionTransformation(Class<? extends FunctionTransformation> clazz) {
        return functionTransformations
            .stream()
            .filter(ft -> ft.clazz.equals(clazz))
            .count() > 0;
    }

    public Set<OutputOverride<?>> getOutputOverrides(Class clazz) {
        return overrides.entrySet()
            .stream()
            .filter(e -> e.getKey().test(this))
            .flatMap(e -> e.getValue().stream()
            .filter(o -> o.clazz.equals(clazz)))
            .collect(Collectors.toSet());
    }

    public Optional<OutputOverride<?>> getCurrentOutputOverride() {
        return getOutputOverrides(currentClass).stream()
            .filter(oo -> field.getName().equals(oo.field) && oo.condition.test(this))
            .findFirst();
    }

    public boolean hasOverrides() {
        return getCurrentOutputOverride().isPresent();
    }

    public OutputOverride<?> getOutputOverride(Class clazz, Field field) {
        if(overrides.containsKey(iteration)) {
            Set<OutputOverride> os = overrides.get(iteration).stream()
                .filter(o -> o.clazz.equals(clazz) && o.field.equals(field))
                .collect(Collectors.toSet());
            if(os.size() > 0) {
                return os.iterator().next();
            }
        }
        return null;
    }

    public TransformationContext addTransformationContainer(final TransformationContainer container) {
        List<TransformationContainer> cs = getCurrentIteration().stream()
            .filter(c -> !container.alias.equals(c.alias) && !container.clazz.equals(c.clazz))
            .collect(Collectors.toList());
        cs.add(container);
        this.containers.put(iteration, cs);
        return this;
    }

    public Map<String, Class> getAliases() {
        return containers.entrySet()
            .stream()
            .flatMap(e -> e.getValue().stream())
            .filter(c -> c instanceof InputTransformationContainer)
            .collect(Collectors.toMap(
                c -> c.alias,
                c -> c.clazz
            ));
    }

    public Object getIteration(Long iteration, String alias) {
        TransformationContainer tc = containers.getOrDefault(iteration, new ArrayList<>())
            .stream()
            .filter(i -> i.alias.equals(alias))
            .findFirst()
            .orElse(null);

        return (tc != null && tc.value != null) ? tc.value : null;
    }

    public List<TransformationContainer> getCurrentIteration() {
        return containers.getOrDefault(iteration, new ArrayList<>())
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public <T> T getPreviousByClass(Class<T> clazz) {
        return (T) containers.getOrDefault(iteration-1L, new HashSet<TransformationContainer>())
            .stream()
            .filter(i -> i.clazz.equals(clazz))
            .findFirst()
            .orElse(null);
    }

    public List<TransformationContainer> getIterationOutputs() {
        return getCurrentIteration()
            .stream()
            .filter(i -> i.direction.equals(TransformDirection.OUTPUT))
            .collect(Collectors.toList());
    }

    public Set<TransformationContainer> getIterationInputs() {
        return getCurrentIteration()
            .stream()
            .filter(i -> i.direction.equals(TransformDirection.INPUT))
            .collect(Collectors.toSet());
    }

    public Set<FunctionTransformationContainer> getFunctionTransformations() {
        return functionTransformations;
    }

    public Map<Predicate<TransformationContext>, Set<OutputOverride<?>>> getOverrides() {
        return overrides;
    }

    public <T> Class<T> getCurrentClass() {
        return (Class<T>) currentClass;
    }

    public <T> T getCurrentInstance() {
        return (T) currentInstance;
    }

    public static Map<Long, Collection<TransformationContainer>> convert(Map<Long, List<TransformationContainer>> inputs) {
        final AtomicLong l =  new AtomicLong();
        return inputs
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey(), e ->
                    e.getValue().stream()
                    .map(c -> {
                        if(c.direction == TransformDirection.INPUT) {
                            return new InputTransformationContainer(c.alias, c.clazz, c.value, l.getAndIncrement());
                        } else {
                            return new OutputTransformationContainer(c.alias, c.clazz, c.value, l.getAndIncrement());
                        }
                    })
                    .collect(Collectors.toList())
                )
            );
    }
}
