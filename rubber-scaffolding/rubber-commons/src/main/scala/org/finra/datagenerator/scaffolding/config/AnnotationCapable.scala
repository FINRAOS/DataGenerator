package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 1/4/17.
  */
trait AnnotationCapable {
    def name: String
    def values: Set[AnnotationField[_, _]]
}