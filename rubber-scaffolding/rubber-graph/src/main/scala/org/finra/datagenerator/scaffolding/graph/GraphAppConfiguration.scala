package org.finra.datagenerator.scaffolding.graph

import org.apache.spark.SparkConf
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.scheduling.concurrent.{ThreadPoolTaskExecutor, ThreadPoolTaskScheduler}

/**
  * Created by dkopel on 11/14/16.
  */
@SpringBootConfiguration
@ComponentScan(Array("org.finra.datagenerator.scaffolding.graph", "org.finra.datagenerator.scaffolding.spark", "org.finra.datagenerator.scaffolding.random"))
class GraphAppConfiguration {
    @Value("${executor.coreSize:4}") private val coreSize: Int = 0
    @Value("${executor.maxSize:10}") private val maxSize: Int = 0
    @Value("${scheduler.poolSize:2}") private val poolSize: Int = 0

    @Bean
    @ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true) def threadPoolTaskExecutor: ThreadPoolTaskExecutor = {
        val pool: ThreadPoolTaskExecutor = new ThreadPoolTaskExecutor
        pool.setCorePoolSize(coreSize)
        pool.setMaxPoolSize(maxSize)
        return pool
    }

    @Bean
    @ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true) def threadPoolTaskScheduler: ThreadPoolTaskScheduler = {
        val taskScheduler: ThreadPoolTaskScheduler = new ThreadPoolTaskScheduler
        taskScheduler.setPoolSize(poolSize)
        return taskScheduler
    }

    @Bean def sparkConf(): SparkConf = {
        new SparkConf().setMaster("local[*]").setAppName("RubberSpark")
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    }
}
