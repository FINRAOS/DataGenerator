package org.finra.datagenerator.scaffolding.utils

import java.io.Serializable
import java.lang.invoke.{MethodHandles, MethodType}
import java.lang.reflect.Method

import com.fasterxml.jackson.annotation.JsonIgnore

/**
  * Created by dkopel on 8/19/16.
  */
final class MethodContainer extends Serializable {
    @JsonIgnore final private var `type`: Class[_] = null
    final private var methodName: String = null
    final private var methodType: MethodType = null

    def this(`type`: Class[_], methodName: String, methodType: MethodType) {
        this()
        this.`type` = `type`
        this.methodName = methodName
        this.methodType = methodType
    }

    def this(`type`: String, methodName: String, methodType: String) {
        this()
        this.`type` = Class.forName(`type`)
        this.methodName = methodName
        this.methodType = MethodType.fromMethodDescriptorString(methodType, `type`.getClass.getClassLoader)
    }

    def this(method: Method) {
        this(method.getDeclaringClass, method.getName, MethodHandles.lookup.unreflect(method).`type`)
    }

    def getType: String = {
        return `type`.getName
    }

    def getMethodName: String = {
        return methodName
    }

    def getMethodType: String = {
        return methodType.toMethodDescriptorString
    }
}