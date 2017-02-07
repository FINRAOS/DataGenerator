package org.finra.datagenerator.scaffolding.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by dkopel on 7/5/16.
 */
@Service
public class SimpleClassPathScanner implements ClassPathScanner {
    private AdvancedClassPathScanner provider;

    public SimpleClassPathScanner() {}

    public SimpleClassPathScanner(List<String> basePackages, Boolean useDefaults) {
        this.basePackages = basePackages;
        this.provider = new AdvancedClassPathScanner(useDefaults);
    }

    public SimpleClassPathScanner(String basePackage) {
        this(Lists.newArrayList(basePackage), false);
    }

    public SimpleClassPathScanner(List<String> basePackages) {
        this(basePackages, false);
    }

    @Value("${classpathScanner.basePackages:'org.finra'}")
    private List<String> basePackages;

    @Override
    public ClassPathScanner reset() {
        provider.resetFilters(false);
        return this;
    }

    @Override
    public ClassPathScanner addIncludeFilter(TypeFilter typeFilter) {
        provider.addIncludeFilter(typeFilter);
        return this;
    }

    @Override
    public ClassPathScanner addExcludeFilter(TypeFilter typeFilter) {
        provider.addExcludeFilter(typeFilter);
        return this;
    }

    @Override
    public ClassPathScanner setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
        return this;
    }

    @Override
    public void includeConcrete() {
        provider.setConcrete(true);
    }

    @Override
    public void excludeNotConcrete() {
        provider.setConcrete(false);
    }

    @Override
    public Set<BeanDefinition> findComponents() {
        return provider.findCandidateComponents(basePackages);
    }

    @Override
    public ClassPathScanningCandidateComponentProvider getProvider() {
        return provider;
    }
}
