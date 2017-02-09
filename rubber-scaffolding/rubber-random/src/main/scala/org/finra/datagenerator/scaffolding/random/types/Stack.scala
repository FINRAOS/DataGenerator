package org.finra.datagenerator.scaffolding.random.types

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.random.core.RubberRandom.{MaxRecursionCountName, RecursionEnabledName}
import org.finra.datagenerator.scaffolding.random.exceptions.{MaxRecursiveException, RecursionDisabledException}

/**
  * Created by dkopel on 11/28/16.
  */
class Stack(private val initial: List[Any]=List.empty)(implicit conf: Configuration) extends Logging {
    private var _stack = initial
    private var counts = Map.empty[Any, Long]

    def add(values: Seq[Any]): Stack = {
        values.foreach(v => add(v))
        this
    }

    def add(value: Any): Stack = {
        if(!_stack.contains(value)) {
            val c = _stack.count(p => _stack.contains(p))
            counts += value->c
            if(conf.conf(RecursionEnabledName).getValue()) {
                val max = conf.conf[Int](MaxRecursionCountName).getValue()
                logger.debug("The max count for recursion is {} and \"c\" is {}", max, c)
                if(c >= max) {
                    throw new MaxRecursiveException(max)
                } else {
                    _stack = value :: _stack
                }
                logger.debug("The value {} is now in the stack {} times", value, c)
            } else {
                throw new RecursionDisabledException()
            }
        } else {
            _stack = value :: _stack
        }
        this
    }

    def get: List[Any] = _stack

    def +(value: Any): Stack = {
        add(value)
    }

    def ++(value: Seq[Any]): Stack = {
        add(value)
    }

    def size: Int = _stack.size

    override def toString: String = {
        s"Stack of ${size}"
    }

    def canAdd(value: Any): Boolean = {
        if(!_stack.contains(value)) {
            val c = _stack.count(p => _stack.contains(p))
            counts += value->c
            if(conf.conf(RecursionEnabledName).getValue()) {
                val max = conf.conf[Int](MaxRecursionCountName).getValue()
                !(c >= max)
            }
            else false
        }
        true
    }
}
object Stack {
    def empty = new Stack(Nil)(null)
}