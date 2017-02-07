package org.finra.datagenerator.scaffolding.messaging.response

/**
  * Created by dkopel on 7/7/16.
  */
trait Response[+R] {
    val response: R
    val priority: Long
}
object Response {
    def apply[R](data: R, prty: Long=0): Response[R] = new Response[R] {
        override val response=data
        override val priority=prty
    }
}