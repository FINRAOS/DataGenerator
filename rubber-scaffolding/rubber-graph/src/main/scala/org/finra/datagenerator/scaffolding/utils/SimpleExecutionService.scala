package org.finra.datagenerator.scaffolding.utils

import java.util.Date
import java.util.concurrent._

import org.finra.datagenerator.scaffolding.execution.ExecutionService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.{ThreadPoolTaskExecutor, ThreadPoolTaskScheduler}
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.stereotype.Service

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by dkopel on 6/29/16.
  */
@Service
@ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true) object SimpleExecutionService {

    @Configuration class SchedulingConfiguration extends SchedulingConfigurer {
        private var taskScheduler: ThreadPoolTaskScheduler = null

        @Autowired def setTaskScheduler(taskScheduler: ThreadPoolTaskScheduler): SimpleExecutionService.SchedulingConfiguration = {
            this.taskScheduler = taskScheduler
            return this
        }

        def configureTasks(scheduledTaskRegistrar: ScheduledTaskRegistrar) {
            scheduledTaskRegistrar.setTaskScheduler(taskScheduler)
        }
    }

}

@Service
@ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true)
class SimpleExecutionService extends ExecutionService {
    private var taskScheduler: ThreadPoolTaskScheduler = null.asInstanceOf[ThreadPoolTaskScheduler]
    private var taskExecutor: ThreadPoolTaskExecutor = null.asInstanceOf[ThreadPoolTaskExecutor]
    private val logger: Logger = LoggerFactory.getLogger(getClass)

    @Autowired def setTaskScheduler(taskScheduler: ThreadPoolTaskScheduler): SimpleExecutionService = {
        this.taskScheduler = taskScheduler
        return this
    }

    @Autowired def setTaskExecutor(taskExecutor: ThreadPoolTaskExecutor): SimpleExecutionService = {
        this.taskExecutor = taskExecutor
        return this
    }

    def execute(runnable: Runnable) {
        logger.info("Thread pool has {} with {} active", taskExecutor.getPoolSize, taskExecutor.getActiveCount)
        taskExecutor.execute(runnable)
    }

    def submit(runnable: Runnable): Future[_] = {
        logger.info("Thread pool has {} with {} active", taskExecutor.getPoolSize, taskExecutor.getActiveCount)
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(taskExecutor)
        Future {
            taskExecutor.submit(runnable).get
        }
    }

    override def submit[V](callable: () => V): Future[V] = {
        logger.info("Thread pool has {} with {} active", taskExecutor.getPoolSize, taskExecutor.getActiveCount)
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(taskExecutor)
        Future[V] {
            taskExecutor.submit(new Callable[V] {
                override def call(): V = callable.apply()
            }).get
        }
    }

    def doWhile[V, U](future: Future[V], predicate: (Future[V]) => Boolean, callback: (V) => U): Future[U] = {
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(taskExecutor)
        Future[U] {
            while (!predicate.apply(future)) {
                try{ Thread.sleep(50) } catch {
                    case _ =>
                }
            }
            callback.apply(future.value.get.get)
        }
    }

    def schedule(runnable: Runnable, trigger: Trigger): ScheduledFuture[_] = {
        return taskScheduler.schedule(runnable, trigger)
    }

    def schedule(runnable: Runnable, date: Date): ScheduledFuture[_] = {
        return taskScheduler.schedule(runnable, date)
    }

    def scheduleAtFixedRate(runnable: Runnable, date: Date, period: Long): ScheduledFuture[_] = {
        return taskScheduler.scheduleAtFixedRate(runnable, date, period)
    }

    def scheduleAtFixedRate(runnable: Runnable, period: Long): ScheduledFuture[_] = {
        return taskScheduler.scheduleAtFixedRate(runnable, period)
    }

    def scheduleWithFixedDelay(runnable: Runnable, date: Date, delay: Long): ScheduledFuture[_] = {
        return taskScheduler.scheduleWithFixedDelay(runnable, date, delay)
    }

    def scheduleWithFixedDelay(runnable: Runnable, delay: Long): ScheduledFuture[_] = {
        return taskScheduler.scheduleWithFixedDelay(runnable, delay)
    }
}