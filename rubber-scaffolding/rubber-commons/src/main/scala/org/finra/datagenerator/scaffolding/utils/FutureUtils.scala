package org.finra.datagenerator.scaffolding.utils

import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by dkopel on 9/9/16.
  */
object FutureUtils {
    def toJavaFuture[T](future: scala.concurrent.Future[T]) = {
        new java.util.concurrent.Future[T] {
            override def isCancelled: Boolean = throw new UnsupportedOperationException

            override def get(): T = Await.result(future, Duration.Inf)

            override def get(
                                timeout: Long, unit: TimeUnit
                            ): T = Await.result(future, Duration.create(timeout, unit))

            override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

            override def isDone: Boolean = future.isCompleted
        }
    }
}
