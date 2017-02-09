package org.finra.datagenerator.scaffolding.utils;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.*;

/**
 * Created by dkopel on 6/28/16.
 */
public class ClassUtils {
    private static Logger logger = LoggerFactory.getLogger("ClassUtils");

    public static Method getMethod(Class clazz, String method, Class[] params) {
        try {
            return clazz.getDeclaredMethod(method, params);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static Class<?> getTargetClass(Object obj) {
        if (AopUtils.isAopProxy(obj)) {
            return AopUtils.getTargetClass(obj);
        } else {
            return (obj instanceof Class) ? (Class) obj : obj.getClass();
        }
    }

    public static <T> T getTargetObject(Object candidate) {
        try {
            if (AopUtils.isAopProxy(candidate) && (candidate instanceof Advised)) {
                return (T) ((Advised) candidate).getTargetSource().getTarget();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to unwrap proxied object.", e);
        }
        // else
        return (T) candidate;
    }

    public static <T> T createNewInstance(Class<T> clazz, Object[] arguments, Class[] classes) throws InstantiationException {
        try {
            if(arguments.length == 0) {
                logger.debug("No arguments provided, creating a default {}", clazz.getName());
                return clazz.newInstance();
            } else {
                Object[] output = new Object[arguments.length];
                for(int x=0; x<arguments.length; x++) {
                    if(classes[x].isInstance(arguments[x])) {
                        output[x] = arguments[x];
                    } else if(org.springframework.util.ClassUtils.isPrimitiveOrWrapper(classes[x])) {
                        Class cl = classes[x];
                        logger.debug("Class {} is a primitive", cl);

                        if(cl.isPrimitive() || org.apache.commons.lang3.ClassUtils.getAllSuperclasses(cl).contains(Number.class)) {
                            logger.debug("Class {} is a number", classes[x]);
                            String val = String.valueOf(arguments[x]);
                            Number num = NumberUtils.createNumber(val);

                            switch(classes[x].getSimpleName()) {
                                case "long":
                                case "Long":
                                    output[x] = num.longValue();
                                    break;
                                case "double":
                                case "Double":
                                    output[x] = num.doubleValue();
                                    break;
                                case "float":
                                case "Float":
                                    output[x] = num.floatValue();
                                    break;
                                case "short":
                                case "Short":
                                    output[x] = num.shortValue();
                                    break;
                                case "byte":
                                case "Byte":
                                    output[x] = num.byteValue();
                                    break;
                                case "int":
                                case "Integer":
                                    output[x] = num.intValue();
                                    break;
                            }
                        } else if(org.springframework.util.ClassUtils.hasConstructor(classes[x], String.class)) {
                            output[x] = classes[x].getConstructor(String.class).newInstance(arguments[x]);
                        }
                    }
                }
                logger.debug("{} arguments provided, creating with {} values and {} classes {}",
                    arguments.length, clazz.getName(),
                    output, classes
                );

                return ConstructorUtils.invokeConstructor(clazz, output, classes);
            }
        } catch (NoSuchMethodException e) {
            logger.error("Couldn't find a constructor to invoke");
        } catch (IllegalAccessException e) {
            logger.error("Something went wrong with {}", e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Something went wrong with {}", e.getMessage());
        }
        return null;
    }

    public static Boolean hasIntendedInterface(Class clazz, Class iface) {
        return org.apache.commons.lang3.ClassUtils.getAllInterfaces(clazz).contains(iface);
    }

    public static Boolean hasIntendedInterface(Object obj, Class iface) {
        return findImplementsInterface(obj.getClass(), iface) != null;
    }

    public static Class findImplementsInterface(Class clazz, Class iface) {
        // Type parameters
        for(TypeVariable tt : clazz.getTypeParameters()) {
            for(Type ttt : tt.getBounds()) {
                if(ttt instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) ttt;
                    Class ptc = (Class) pt.getRawType();

                    if(hasIntendedInterface(ptc, iface)) {
                        logger.info("The class {} was a types parameter", ptc.getName());
                        return ptc;
                    }
                }
            }
        }

        // Interface
        if(clazz.getGenericInterfaces().length > 0) {
            for(Type t : clazz.getGenericInterfaces()) {
                ParameterizedType pt = (ParameterizedType) t;
                if(pt.getActualTypeArguments().length > 0) {
                    for(Type tt : pt.getActualTypeArguments()) {
                        TypeVariable tv = (TypeVariable) tt;
                        if(tv.getBounds().length > 0) {
                            for(Type ttt : tv.getBounds()) {
                                Class cb = (Class) ttt;
                                if(hasIntendedInterface(cb, iface)) {
                                    logger.info("The class {} was found in a generic", cb.getName());
                                    return cb;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}