package org.finra.datagenerator.scaffolding.random.core

import com.mifmif.common.regex.Generex
import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.random.RegexRandomizer
import org.finra.datagenerator.scaffolding.random.core.RubberRandom.RegexStrategyName

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._
import scala.util.matching.Regex

/**
  * Created by dkopel on 12/12/16.
  */
trait RegexGenerator {
    val regex: Regex
    val jpr: JavaPrimitives
    implicit def regexToString(regex: Regex): java.lang.String = regex.toString
    def next: String
}
trait RegexStrategy
object ComplexRegex extends RegexStrategy
object SimpleRegex extends RegexStrategy
class RubberRegexRandom(implicit rubberRandom: RubberRandom) {
    def generate(regex: Regex): RegexGenerator = {
        rubberRandom.conf[RegexStrategy](RegexStrategyName).getValue() match {
            case SimpleRegex => new SimpleRegex(regex, rubberRandom)
            case ComplexRegex => new ComplexRegex(regex, rubberRandom)
        }
    }
}
class SimpleRegex(val regex: Regex, override val jpr: JavaPrimitives) extends RegexGenerator {
    val generex = new Generex(regex, jpr.jpr.random)
    override def next: String = generex.random()

    def iterateFromRegex[T](regex: Regex)(implicit typeTag: TypeTag[T]): Iterator[T] = {
        val i = generex.iterator()
        new Iterator[T] {
            override def hasNext: Boolean = i.hasNext
            override def next(): T = i.next().asInstanceOf[T]
        }
    }

    def generateFromRegex[T](regex: Regex, limit: Int)(implicit typeTag: TypeTag[T]): List[T] = generex.getMatchedStrings(limit).asScala.toList.asInstanceOf[List[T]]

    def countFromRegex(regex: Regex)(implicit conf: Configuration): Long = generex.matchedStringsSize()
}
class ComplexRegex(val regex: Regex, override val jpr: JavaPrimitives) extends RegexGenerator {
    override def next: String = new RegexRandomizer(jpr.jpr.random).generateFromRegex(regex)
}