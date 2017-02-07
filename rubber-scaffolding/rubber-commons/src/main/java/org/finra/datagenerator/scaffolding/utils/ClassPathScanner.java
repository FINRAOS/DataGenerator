package org.finra.datagenerator.scaffolding.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;

import java.util.List;
import java.util.Set;

/**
 * Created by dkopel on 7/5/16.
 */
public interface ClassPathScanner {
    ClassPathScanner reset();
    ClassPathScanner addIncludeFilter(TypeFilter typeFilter);
    ClassPathScanner addExcludeFilter(TypeFilter typeFilter);
    ClassPathScanner setBasePackages(List<String> basePackages);
    void includeConcrete();
    void excludeNotConcrete();
    Set<BeanDefinition> findComponents();
    ClassPathScanningCandidateComponentProvider getProvider();
}
