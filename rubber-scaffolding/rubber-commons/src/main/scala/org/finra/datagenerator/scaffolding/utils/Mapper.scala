package org.finra.datagenerator.scaffolding.utils

import java.lang.reflect.ParameterizedType

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

/**
  * Created by dkopel on 27/05/16.
  */
object Mapper {
    private val objectMapper: ObjectMapper with ScalaObjectMapper = new ObjectMapper with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    def mapper = objectMapper

    def toBytes(`object`: Any): Array[Byte] = {
        try {
            return objectMapper.writeValueAsBytes(`object`)
        }
        catch {
            case e: JsonProcessingException => {
                e.printStackTrace()
            }
        }
        return null
    }

    def toString(`object`: Any): String = {
        try {
            return objectMapper.writeValueAsString(`object`)
        }
        catch {
            case e: JsonProcessingException => {
                e.printStackTrace()
            }
        }
        return null
    }

    def convert[T](bytes: Array[Byte], `type`: Class[T]): T = try {
        objectMapper.readValue(bytes, `type`)
    }


    def deserialize[T](str: String, `type`: Class[T]): T = {
        objectMapper.readValue(str, `type`)
    }

    def getClass[T](typeReference: TypeReference[T]): Class[T] = {
        return (typeReference.getType.asInstanceOf[ParameterizedType]).getRawType.asInstanceOf[Class[T]]
    }
}