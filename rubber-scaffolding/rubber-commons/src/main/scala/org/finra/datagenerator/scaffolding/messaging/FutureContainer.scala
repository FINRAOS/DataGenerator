package org.finra.datagenerator.scaffolding.messaging

import java.util.concurrent.TimeUnit

/**
  * Created by dkopel on 9/9/16.
  */
class FutureContainer[T, V](val data: T, private val future: java.util.concurrent.Future[V]) extends java.util.concurrent.Future[V] {
    override def isCancelled: Boolean = future.isCancelled

    override def get(): V = future.get

    override def get(timeout: Long, unit: TimeUnit): V = future.get(timeout, unit)

    override def cancel(mayInterruptIfRunning: Boolean): Boolean = future.cancel(mayInterruptIfRunning)

    override def isDone: Boolean = future.isDone
}
