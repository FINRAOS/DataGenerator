package org.finra.datagenerator.scaffolding.utils;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by dkopel on 1/5/17.
 */
public class AdvancedClassPathScanner extends ClassPathScanningCandidateComponentProvider {
    private Boolean isConcrete = true;

    public AdvancedClassPathScanner(boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    public AdvancedClassPathScanner(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

    public Boolean getConcrete() {
        return isConcrete;
    }

    public AdvancedClassPathScanner setConcrete(Boolean concrete) {
        isConcrete = concrete;
        return this;
    }

    private boolean isConcrete(AnnotatedBeanDefinition beanDefinition) {
        return isConcrete || beanDefinition.getMetadata().isConcrete();
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return isConcrete(beanDefinition) && beanDefinition.getMetadata().isIndependent();
    }

    public Set<BeanDefinition> findCandidateComponents(List<String> basePackages) {
        return basePackages
            .stream()
            .flatMap(bp -> findCandidateComponents(bp).stream())
            .collect(Collectors.toSet());
    }
}
