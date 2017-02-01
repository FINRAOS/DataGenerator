package org.finra.scaffolding.action

import org.finra.scaffolding.messaging.response.Response

/**
  * Created by dkopel on 8/25/16.
  */
trait ActionFunction[T, R] extends Action with Function[T, R] {
    var body: T
    var response: Response[R]
}