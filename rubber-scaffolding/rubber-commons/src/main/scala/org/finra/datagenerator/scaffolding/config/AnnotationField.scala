package org.finra.datagenerator.scaffolding.config

import com.fasterxml.jackson.databind.ObjectMapper

/**
  * Created by dkopel on 1/4/17.
  */
case class AnnotationField[+T, U](
                                    name: String,
                                    link: ConfigDefinition[T],
                                    outputType: Class[_ <: T],
                                    inputType: Class[U],
                                    convertOverride: Option[U=>T] = Option.empty
                                ) {
    def convert(input: U): T = {
        if(convertOverride.isDefined) convertOverride.get(input)
        else AnnotationField.convert(this, input)
    }
}
object AnnotationField {
    val mapper = new ObjectMapper()

    implicit def convert[T, U](ann: AnnotationField[T, U], input: U): T = mapper.convertValue(input, ann.outputType)
}