package org.finra.datagenerator.scaffolding.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dkopel on 6/28/16.
 */
public class ReflectionUtils {
    public static <T extends Annotation> Collection<T> findAnnotation(Class<?> clazz, Class<T> annotation) {
        Collection<T> collected = new HashSet();
        if(clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            collected.addAll(findAnnotation(clazz.getSuperclass(), annotation));
        } else {
            for(T a : clazz.getAnnotationsByType(annotation)) {
                collected.add(a);
            }
            for(Method m : clazz.getMethods()) {
                for(T a : m.getAnnotationsByType(annotation)) {
                    collected.add(a);
                }
            }
            for(Field f : clazz.getDeclaredFields()) {
                for(T a : f.getAnnotationsByType(annotation)) {
                    collected.add(a);
                }
            }
        }
        return collected;
    }

    public static <T extends Annotation> Collection<AnnotationAssociation<T>> findAnnotationsWithAssociation(Class<?> clazz, Class<T> annotation) {
        Collection<AnnotationAssociation<T>> collected = new HashSet();
        if(clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            collected.addAll(findAnnotationsWithAssociation(clazz.getSuperclass(), annotation));
        } else {
            for(T a : clazz.getAnnotationsByType(annotation)) {
                collected.add(new AnnotationAssociation<T>(a).addAssociation(clazz));
            }
            for(Method m : clazz.getMethods()) {
                for(T a : m.getAnnotationsByType(annotation)) {
                    collected.add(new AnnotationAssociation<T>(a).addAssociation(m));
                }
            }
            for(Field f : clazz.getDeclaredFields()) {
                for(T a : f.getAnnotationsByType(annotation)) {
                    collected.add(new AnnotationAssociation<T>(a).addAssociation(f));
                }
            }
        }
        return collected;
    }

    public static class AnnotationAssociation<T extends Annotation> {
        private T annotation;
        private Set<Object> associations = new HashSet();

        public AnnotationAssociation(T annotation) {
            this.annotation = annotation;
        }

        public AnnotationAssociation addAssociation(Class clazz) {
            associations.add(clazz);
            return this;
        }

        public AnnotationAssociation addAssociation(Method method) {
            associations.add(method);
            return this;
        }

        public AnnotationAssociation addAssociation(Field field) {
            associations.add(field);
            return this;
        }

        public T getAnnotation() {
            return annotation;
        }

        public Set<Object> getAssociations() {
            return associations;
        }
    }

    public static Field getDeepProperty(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> c = clazz;
        while(c.getSuperclass() != null) {
            List<Field> fields = Stream.of(c.getDeclaredFields())
                .filter(f -> Objects.equals(f.getName(), fieldName))
                .collect(Collectors.toList());
            if(fields.size() == 1) {
                return fields.get(0);
            } else {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    public static void setField(Field f, Object obj, Object val) throws IllegalAccessException {
        boolean access = f.isAccessible();
        f.setAccessible(true);
        if(f.getType().equals(String.class) && val != null) {
            val = val.toString();
        }
        f.set(obj, val);
        f.setAccessible(access);
    }

    public static <T> T getFieldValue(Field f, Object obj) throws IllegalAccessException {
        boolean access = f.isAccessible();
        f.setAccessible(true);
        T v = (T) f.get(obj);
        f.setAccessible(access);
        return v;
    }

    public static void copyField(Object from, Object to, String field) throws IllegalAccessException {
        Class fc = from.getClass();
        Class tc = to.getClass();
        Field fOrg = org.springframework.util.ReflectionUtils.findField(fc, field);
        Field fNew = org.springframework.util.ReflectionUtils.findField(tc, field);
        boolean oaccess = fOrg.isAccessible();
        fOrg.setAccessible(true);
        Object value = fOrg.get(from);
        fOrg.setAccessible(oaccess);
        boolean naccess = fNew.isAccessible();
        fNew.setAccessible(true);
        fNew.set(to, value);
        fNew.setAccessible(naccess);
    }
}
