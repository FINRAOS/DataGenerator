package org.finra.datagenerator.scaffolding.knowledge.support.util

import scala.reflect.runtime.universe._
/**
  * Created by dkopel on 9/23/16.
  */
case class SnippetAnalysis(val snippet: String, analyzed: Apply, args: Map[String, TypeContainer])
