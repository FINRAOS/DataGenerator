package org.finra.datagenerator.scaffolding.utils;

/**
 * Created by dkopel on 10/28/16.
 */
public class ClassInfo {
    /**
     * fully qualified classname in a format suitable for Class.forName
     */
    public String className;

    public Class<?> clazz;

    /**
     * bytecode for the class
     */
    public byte[] bytes;

    public ClassInfo(String aClassName, byte[] aBytes) {
        className = aClassName.replace('/', '.');
        // className = aClassName.replace('.', '/');
        bytes = aBytes;
    }

    @Override
    public String toString() {
        return className;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof ClassInfo)
            && ((ClassInfo)obj).className.equals(this.className)) {
            return true;
        }
        return false;
    }
}

