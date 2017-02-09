package org.finra.datagenerator.scaffolding.action

import java.util.function.Consumer

/**
  * Created by dkopel on 8/25/16.
  */
trait ActionConsumer[T <: Serializable] extends Action with ((T) => Unit) with Serializable {
    var body: T
}