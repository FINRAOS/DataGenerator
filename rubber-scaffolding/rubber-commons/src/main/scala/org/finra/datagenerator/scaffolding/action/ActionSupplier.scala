package org.finra.datagenerator.scaffolding.action

import org.finra.datagenerator.scaffolding.messaging.response.Response


/**
  * Created by dkopel on 8/25/16.
  */
trait ActionSupplier[R <: Serializable] extends Action with (() => R) {
    var response: Response[R]
}