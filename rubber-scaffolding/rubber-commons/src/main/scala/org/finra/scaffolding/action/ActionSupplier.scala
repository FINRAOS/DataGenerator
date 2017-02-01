package org.finra.scaffolding.action

import org.finra.scaffolding.messaging.response.Response


/**
  * Created by dkopel on 8/25/16.
  */
trait ActionSupplier[R <: Serializable] extends Action with (() => R) {
    var response: Response[R]
}