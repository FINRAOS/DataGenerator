package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID

import org.apache.hadoop.mapred.InvalidInputException
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.util.CollectionAccumulator
import org.finra.datagenerator.scaffolding.messaging.config.ResponseConfiguration
import org.finra.datagenerator.scaffolding.messaging.response.{Response, SimpleResponse}
import org.finra.datagenerator.scaffolding.spark.RDDService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by dkopel on 9/1/16.
  */
@Service
@transient
class SubscriptionServiceImpl @Autowired()(
        @transient private val rddService: RDDService,
        @transient private val threadPoolExecutor: ThreadPoolTaskExecutor
    ) extends SubscriptionService() {
    @transient private val logger: Logger = LoggerFactory.getLogger(getClass)
    @transient private val subscriptionsTmp: String = "/tmp/subscriptions"
    @transient private val executionContext: ExecutionContext = ExecutionContext.fromExecutor(threadPoolExecutor)
    @transient private var subscriptions: RDD[(UUID, Subscription[Nothing])] = loadSubscriptions()

    def loadSubscriptions[T <: java.io.Serializable](): RDD[(UUID, Subscription[T])] = {
        var s: RDD[(UUID, Subscription[T])] = null;
        try {
            s = rddService.getSparkContext.objectFile(subscriptionsTmp);
            s.isEmpty
        } catch {
            case e: NullPointerException => s = rddService.parallelize(
                new mutable.HashMap[UUID, Subscription[T]]()
            )
            case e: InvalidInputException => s = rddService.parallelize(
                new mutable.HashMap[UUID, Subscription[T]]()
            )
        }
        s.asInstanceOf[RDD[(UUID, Subscription[T])]]
    }

    override def unsubscribe(id: UUID) = {
        logger.debug("Unsubscribing subscriber {}", id);
        subscriptions = rddService.without(subscriptions, id)
    }

    override def subscribe[T <: java.io.Serializable](subscription: Subscription[T]): UUID = {
        logger.info("Adding subscription {} with {} handlers", subscription.id, subscription.handlers.size)
        var m = Map.empty[UUID, Subscription[T]]
        m = m + (subscription.id -> subscription)
        subscription.handlers.foreach(h => h.parentId = subscription.id)
        val p = rddService.parallelize(m)
        var subs = subscriptions.asInstanceOf[RDD[(UUID, Subscription[T])]]
        subscriptions = subs.union(p).asInstanceOf[RDD[(UUID, Subscription[Nothing])]]
        subscriptions.persist(StorageLevel.MEMORY_AND_DISK);
        subscription.id
    }

    def cleanAccumulator(acculumulator: CollectionAccumulator[Response[_]]): Replies = {
        Replies(
            acculumulator.value
                .asScala
                .toList
                .sortBy(r => r.priority)
        )
    }

    override def broadcast[T <: java.io.Serializable](message: Message[T], conf: Option[ResponseConfiguration]=None): Future[Replies] = {
        logger.info("There are {} subscriptions", subscriptions.count())
        implicit val ec: ExecutionContext = executionContext
        val acc = rddService.getSparkContext.collectionAccumulator[Response[_ <: Any]]("acc")
        Future[Replies] {
            logger.info("In broadcast future for message {}", message.id)
            // Obtain matching handlers
            val subs = subscriptions.asInstanceOf[RDD[(UUID, Subscription[T])]]
            val handlers = subs.filter(t => t._2.predicate.apply(message))
                .values
                .flatMap(s => s.handlers)
                .sortBy(t => t.priority, true)

            handlers.foreach(h => println(">>>>>>> Handler: "+h.priority))

            // Wait for replies
            if (conf.isDefined && conf.get.waitForReplies) {
                var out = List.empty[Future[_]]

                handlers.collect().foreach(h => {
                    val mh = handlers.filter(
                        h1 => h.getId().equals(h1.getId())
                    )
                    out = mh.foreachAsync(h => {
                        val r = h.apply(message)
                        acc.add(
                            r match {
                                case sr: SimpleResponse[_] => SimpleResponse(r.response, h.priority)
                                case ur: Response[Any] => new Response[Any] {
                                    override val response = ur.response
                                    override val priority: Long = 0
                                }
                            }
                        )
                    }) :: out
                })

                val count = out.size
                val ctx: MessageContext = new MessageContextImpl(
                    message,
                    conf.get,
                    count,
                    () => acc.value.asScala.toList,
                    out)

                while (!conf.get.test(ctx)) {
                    logger.info("Finished {} out of {}", ctx, out.size)
                    logger.info("The accumulator has {} items", acc.value.size)
                    Thread.sleep(500)
                }
                cleanAccumulator(acc)
            } else {
                handlers.foreach(h => acc.add(h.apply(message)))
                cleanAccumulator(acc)
            }
        }

        // No need for this now but putting in a placeholder
        /*
        f onSuccess {
            case responses => for (r <- responses.asScala) println(r.getResponse)
        }
        */

        //FutureUtils.toJavaFuture[java.util.List[Response[R]]](f)
    }
}
