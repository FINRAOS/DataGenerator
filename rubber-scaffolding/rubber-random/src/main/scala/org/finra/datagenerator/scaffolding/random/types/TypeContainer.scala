package org.finra.datagenerator.scaffolding.random.types

import java.lang.reflect.{ParameterizedType, Type, TypeVariable, WildcardType}

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.{Logging, TypeUtils}
import org.finra.datagenerator.scaffolding.random.exceptions.{MaxRecursiveException, RecursionDisabledException}
import org.finra.datagenerator.scaffolding.utils.TypeUtils

import scala.reflect.runtime.universe._

/**
  * Created by dkopel on 11/28/16.
  */
class TypeContainer[T](
                          val clazz: Class[T],
                          var types: Seq[TypeContainer[T]],
                          val ptc: Option[TypeContainer[T]]
                      )(implicit conf: Configuration)
extends Logging {
    var iteration: Long = 0
    private var values = Seq.empty[Any]
    var stack: Stack = synchronized(this) {
        if (ptc.isDefined) new Stack(ptc.get.stack.get)(conf)+this
        else new Stack()(conf)+this
    }

    def stackSize = if (stack != null) stack.size else 0

    override def toString: String = {
        var s = s"[${clazz.getName}]"
        if (types.length > 0) {
            s = s + " -> ("
            types.foreach(t => s = s + t.toString)
            s = s + ")"
        }
        if(stack != null) {
            s = s + s" Values(${values.size}) Stack: ($stackSize)"
        }

        s
    }

    def apply[S](value: S): S = {
        values = values :+ value
        logger.debug("Adding value \"{}\" with iteration {} to values now with {} values", value.toString, iteration.toString, values)
        value
    }

    def apply(): Seq[Any] = values

    private def typesEqual(tc: TypeContainer[_]): Boolean = {
        types.equals(tc.types) || types.contains(null) || tc.types.contains(null)
    }

    override def equals(obj: scala.Any): Boolean = {
        obj match {
            case tc: TypeContainer[T] =>
                if (tc.clazz.equals(clazz) && typesEqual(tc)) true
                else false
            case _ => false
        }
    }
}

object TypeContainer extends Logging {
    implicit def apply[T](implicit tt: TypeTag[T], conf: Configuration): TypeContainer[T] = apply(tt.tpe, Option.empty)

    def apply[T](tpe: scala.reflect.runtime.universe.Type, tc: Option[TypeContainer[T]])(implicit conf: Configuration): TypeContainer[T] = {
        var tts = Seq.empty[TypeContainer[T]]
        val c: Class[T] = TypeUtils.mirror.runtimeClass(tpe).asInstanceOf[Class[T]]
        val ntt = new TypeContainer[T](c, tts, tc)
        tpe.typeArgs.foreach(t => {
            tts = tts ++ Seq(TypeContainer[T](t, Option(ntt)))
        })
        ntt.types = tts
        ntt
    }

    def apply[T](tpe: Type, tc: Option[TypeContainer[T]]=Option.empty)(implicit conf: Configuration): TypeContainer[T] = {
        try {
            tpe match {
                case ptr: ParameterizedTypeReference[T] => apply(ptr.tpe, tc)(conf)
                case pt: ParameterizedType =>
                    var tts = Seq.empty[TypeContainer[T]]
                    val tt = new TypeContainer[T](pt.getRawType.asInstanceOf[Class[T]], tts, tc)(conf)
                    pt.getActualTypeArguments.foreach(ta => tts = tts ++ Seq(TypeContainer(ta, Option(tt))(conf)))
                    tt.types = tts
                    tt
                case cs: Class[T] => new TypeContainer(cs, Seq.empty, tc)(conf)
                case tv: TypeVariable[_] => null
                case wildcard: WildcardType => null
            }
        } catch {
            case e: MaxRecursiveException =>
                logger.warn("Max recursion reach!")
                throw e
            case e: RecursionDisabledException =>
                logger.warn("Recursion is disabled!")
                throw e
        }
    }
}