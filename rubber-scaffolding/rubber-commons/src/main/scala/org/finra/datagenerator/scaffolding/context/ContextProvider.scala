package org.finra.datagenerator.scaffolding.context

import org.finra.datagenerator.scaffolding.utils.{Logging, Mapper, TypeUtils}
import org.finra.datagenerator.scaffolding.exceptions.SpelContextException
import org.finra.datagenerator.scaffolding.utils.{Logging, TypeUtils}
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.expression.spel.{SpelEvaluationException, SpelMessage}

import scala.reflect.runtime.universe._

/**
  * Created by dkopel on 12/14/16.
  */
trait ContextProvider extends Logging with GlobalsProvider {
    private val context = new StandardEvaluationContext()
    private val parser = new SpelExpressionParser()

    def setRoot(value: Any) = {
        context.setRootObject(value)
        context.setVariable("self", value)
    }

    def lookupVariable[T](key: String)(implicit tt: TypeTag[T]): T = context.lookupVariable(key).asInstanceOf[T]

    def registerVariable(key: String, value: Any) = context.setVariable(key, value)

    def registerFunction(key: String, value: java.lang.reflect.Method) = context.registerFunction(key, value)

    def registerVariables(values: Map[String, Any]) = values.foreach(t => context.setVariable(t._1, t._2))

    def parseExpression[T](expression: String)(implicit tt: TypeTag[T]): T = {
        implicit val m = TypeUtils.toManifest[T]
        logger.debug("Parsing expression: {}", expression)
        try {
            Mapper.mapper.convertValue[T](parser.parseExpression(expression).getValue(context))
        } catch {
            case e: SpelEvaluationException => {
                val code = e.getMessageCode
                val m = code match {
                    case SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL => s"The object in the expression `$expression` is null or does not have public access to the field `${e.getInserts}`"
                    case _ => e.getMessage
                }
                logger.error(m)
                throw SpelContextException(m, Option(e))
            }
            case e: Exception => {
                logger.error(e.getMessage)
                null.asInstanceOf[T]
            }
            case _ => null.asInstanceOf[T]
        }
    }

    def parseRawExpression(expression: String) = parser.parseExpression(expression).getValue(context)

    def evaluateCondition(condition: String): Boolean = {
        (condition != null && condition.length > 0 && parseExpression[Boolean](condition)) ||
            condition == null || condition.isEmpty
    }
}
object ContextProvider {
    def empty: ContextProvider = new ContextProvider() {}
}