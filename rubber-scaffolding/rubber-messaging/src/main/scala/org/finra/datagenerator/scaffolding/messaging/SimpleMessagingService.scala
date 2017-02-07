package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID

import org.finra.datagenerator.scaffolding.execution.ExecutionService
import org.finra.datagenerator.scaffolding.messaging.config.ResponseConfigurationBuilder
import org.finra.datagenerator.scaffolding.messaging.response.Response
import org.finra.datagenerator.scaffolding.messaging.routing.TopicMatcher
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.util.Assert

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by dkopel on 6/30/16.
  */
@Service
@ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true)
object SimpleMessagingService {
    val SUBSCRIPTIONS: String = "subscriptions"
}

@Service
@ConditionalOnProperty(prefix = "execution", name = Array("mode"), havingValue = "single", matchIfMissing = true)
class SimpleMessagingService[T <: java.io.Serializable] @Autowired() (
                                                                         @transient private val threadPoolExecutor: ThreadPoolTaskExecutor,
                                                                         @transient private var executionService: ExecutionService,
                                                                         @transient private var subscriptionService: SubscriptionService,
                                                                         private var matcher: TopicMatcher
                                                                     ) extends MessagingService with Serializable {
    @transient final private val logger: Logger = LoggerFactory.getLogger(getClass)
    @transient private val executionContext: ExecutionContext = ExecutionContext.fromExecutor(threadPoolExecutor)

    def subscribe(topic: String, handlers: List[MessageHandler]): UUID = {
        val id: UUID = UUID.randomUUID
        val subscription = Subscription[T](id, (t) => {
            LoggerFactory.getLogger(getClass).info("The message has the topic {}", t.headers.topic.get)
            matcher.matches(topic, t.headers.topic.get)
        }, handlers)
        subscriptionService.subscribe(subscription)
        id
    }

    def subscribe[A <: java.io.Serializable](predicate: (Message[A])=>Boolean, handlers: List[MessageHandler]): UUID = {
        val id: UUID = UUID.randomUUID
        val s = Subscription(id, predicate, handlers)
        subscriptionService.subscribe(s)
        id
    }

    def unsubscribe(id: UUID) = {
        logger.debug("Unsubscribing message subscriber {}", id)
        subscriptionService.unsubscribe(id)
    }

    private def broadcast[A <: java.io.Serializable](message: Message[A]): Future[Seq[Response[_]]] = {
        implicit val ec: ExecutionContext = executionContext
        Future {
            val f = subscriptionService.broadcast(message)
            Await.result(f, Duration.Inf)
            f.value.get.get.responses
        }
    }

    // (Raw responses, processed response)
    private def broadcast[A <: java.io.Serializable](message: Message[A], builder: ResponseConfigurationBuilder): Future[Reply] = {
        val conf = builder.build(message)
        executionService.doWhile[Replies, Reply](
            // Do broadcast
            subscriptionService.broadcast(message, Option(conf)),
            // While not true
            // Wait for broadcast to collect
            (t: Future[Replies]) => t.isCompleted,
            // Callback
            (responses: Replies) => Reply(responses, conf.apply(responses))
        )
    }

    private def checkMessage(message: Message[_]) = {
        Assert.notNull(message)
        Assert.notNull(message.headers)
    }

    override def publish[A <: java.io.Serializable](message: Message[A]): Future[Seq[Response[_]]] = {
        logger.info("Publishing message with topic {}", message.headers.getTopic)
        checkMessage(message)
        // Get all matching handlers
        implicit val ec: ExecutionContext = executionContext
        broadcast(message)
    }

    override def publish[A <: java.io.Serializable](
                            message: Message[A],
                            builder: ResponseConfigurationBuilder
                        ): Reply = {
        checkMessage(message)
        val reply = broadcast(message, builder)
        Await.result(reply, Duration.Inf)
    }
}