package org.finra.datagenerator.scaffolding.execution

import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler

import scala.concurrent.Future

/**
  * Created by dkopel on 6/29/16.
  */
trait ExecutionService extends TaskExecutor with TaskScheduler {
    def submit[V <: Object](callable: () => V): Future[V]

    /*
    Wait for condition until predicate returns true
    Then return output from callable with supplied future value
     */
    def doWhile[V, U](future: Future[V], predicate: (Future[V]) => Boolean, callback: (V) => U): Future[U]
}