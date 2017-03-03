package org.finra.datagenerator.scaffolding.messaging

import java.util.UUID
import java.util.concurrent.{CountDownLatch, ThreadLocalRandom}

import Configuration
import org.finra.datagenerator.scaffolding.messaging.config.CompleteResponse
import org.finra.datagenerator.scaffolding.messaging.response.{Response, SimpleResponse}
import org.finra.datagenerator.scaffolding.spark.RDDService
import org.junit.runner.RunWith
import org.junit.{After, Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.{SpringJUnit4ClassRunner, SpringRunner}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by dkopel on 6/30/16.
  */
@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[Configuration]))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@transient
class MessagingServiceTest[T <: java.io.Serializable] {
    private val logger: Logger = LoggerFactory.getLogger(getClass)
    @transient private var ms: MessagingService = null
    private var rddService: RDDService = null
    @transient private[this] var _threadPoolExecutor: ThreadPoolTaskExecutor = _
    @transient private var executionContext: ExecutionContext = _

    @Autowired def threadPoolExecutor_=(value: ThreadPoolTaskExecutor): Unit = {
      _threadPoolExecutor = value
        ExecutionContext.fromExecutor(_threadPoolExecutor)
    }

    @Autowired def setMs(ms: MessagingService) = {
        this.ms = ms
    }

    @Autowired def setRddService(rddService: RDDService) = {
        this.rddService = rddService
    }

    @After
    def after(): Unit = {
        rddService.getSparkContext.stop()
    }


    @Test
    @throws[Exception]
    def simpleSparkSubscribePublish() {
        var handlers = List.empty[MessageHandler]
        val messages = 20
        var nums = List.empty[Long]
        var x: Int = 0
        while (x < messages) {
            {
                val w: Long = ThreadLocalRandom.current.nextInt(500, 1000)
                val p: Long = ThreadLocalRandom.current.nextLong(0, 1000)
                nums = w :: nums
                handlers = new MessageHandler {
                    override val priority = p
                    override def apply[T <: java.io.Serializable](v: Message[T]): Response[_] = {
                        LoggerFactory.getLogger(getClass()).info("About to wait {} before completing", w);
                        Thread.sleep(w)
                        LoggerFactory.getLogger(getClass()).info("Finished waiting {} returning value {}", w, w);
                        SimpleResponse(w)
                    }

                    override def compare(that: MessageHandler): Int = 0
                } :: handlers
            }
            {
                x += 1; x - 1
            }
        }
        ms.subscribe("test", handlers)
        logger.info("About to publish message")

        val m = Message("blach", new MessageHeaders(Option("test")))
        val f = ms.publish(m)
        logger.info("Just returned from publishing message")
        implicit val ec: ExecutionContext = executionContext

        while(!f.isCompleted) {
            logger.info("Message not done")
            Thread.sleep(200)
        }

        val responses = f.value.get.get
        val responseLongs = responses.map(rr => rr.response.asInstanceOf[Long])
        val sum: Long = nums.sum
        val returnedSum: Long = responseLongs.sum
        logger.info("Sum is {} compared to the expected value of {}", returnedSum, sum)
        Assert.assertEquals(sum, returnedSum)
        logger.info("Message is now done")
    }

    @Test
    @throws[Exception]
    def simpleSparkPayloadPredicate() {
        ms.subscribe((t: Message[Andrew]) => t.body.num % 2 == 0, List(new MessageHandler {
            override def apply[T <: java.io.Serializable](message: Message[T]): Response[_] = {
                LoggerFactory.getLogger(getClass()).info("This is my handler!...I should come 4th")
                SimpleResponse(2)
            }

            override val priority: Long = 50
        }))

        ms.subscribe((t: Message[Andrew]) => t.body.num == 2, List(new MessageHandler {
            override def apply[T <: java.io.Serializable](message: Message[T]): Response[_] = {
                LoggerFactory.getLogger(getClass()).info("This is my handler!...I should come 3rd")
                Thread.sleep(500)
                SimpleResponse(5)
            }

            override val priority: Long = 20
        }))

        ms.subscribe((t: Message[Andrew]) => t.body.num == 2, List(new MessageHandler {
            override def apply[T <: java.io.Serializable](message: Message[T]): Response[_] = {
                LoggerFactory.getLogger(getClass()).info("This is my handler!...I should come next")
                SimpleResponse(10)
            }

            override val priority: Long = 10
        }))

        ms.subscribe((t: Message[Andrew]) => t.body.isInstanceOf[Andrew], List(new MessageHandler {
            override def apply[T <: java.io.Serializable](message: Message[T]): Response[_] = {
                LoggerFactory.getLogger(getClass()).info("This is my other handler!....I should come first")
                SimpleResponse(15)
            }

            override val priority: Long = 0
        }))

        ms.subscribe((t: Message[Andrew]) => t.body.num == 1, List(new MessageHandler {
            override def apply[T <: java.io.Serializable](message: Message[T]): Response[_] = {
                LoggerFactory.getLogger(getClass()).info("This never gets called")
                SimpleResponse(613)
            }

            override val priority: Long = 0
        }))

        ms.publish(new Message(Andrew(2)), new CompleteResponse(t => {
            val log = LoggerFactory.getLogger(getClass())
            val rr = t.responses.map(r => r.response)
            t.responses.foreach(r => {
                log.info("This is a reply {}", r.response)
            })
            Assert.assertTrue(rr(0) == 15)
            Assert.assertTrue(rr(1) == 10)
            Assert.assertTrue(rr(2) == 5)
            Assert.assertTrue(rr(3) == 2)
            SimpleResponse(2)
        }))

        val l = new CountDownLatch(1)
        implicit val ec: ExecutionContext = executionContext
        var f = ms.publish(new Message(Andrew(1)))
        Await.result(f, Duration.Inf)
        println(f.value.get.get)
//
//        f.onComplete((r) => {
//            val replies = r.get.responses
//            Assert.assertTrue(replies.length == 1)
//            Assert.assertTrue(replies(0) == 613)
//            l.countDown()
//        })


    }

    @Test
    @throws[Exception]
    def simpleSparkSubscribeUnsubscribe() {
        var handlers = List.empty[MessageHandler]
        val messages = 5
        var nums = List.empty[Long]
        var x: Int = 0
        while (x < messages) {
            {
                val w: Long = ThreadLocalRandom.current.nextInt(500, 1000)
                val p: Long = ThreadLocalRandom.current.nextLong(0, 1000)
                nums = w :: nums
                handlers = new MessageHandler {
                    override val priority = p
                    override def apply[T <: java.io.Serializable](v: Message[T]): Response[_] = {
                        LoggerFactory.getLogger(getClass()).info("About to wait {} before completing", w);
                        Thread.sleep(w)
                        LoggerFactory.getLogger(getClass()).info("Finished waiting {} returning value {}", w, w);
                        SimpleResponse(w)
                    }

                    override def compare(that: MessageHandler): Int = 0
                } :: handlers
            }
            {
                x += 1; x - 1
            }
        }
        val id: UUID = ms.subscribe("test", handlers)
        logger.info("About to publish message")
        var f = ms.publish(Message("this is a test object", new MessageHeaders(Option("test"))))
        logger.info("Just returned from publishing message")
        implicit val ec: ExecutionContext = executionContext
        while(!f.isCompleted) {
            logger.info("Message not done")
            Thread.sleep(200)
        }
        var responses = f.value.get.get
        val responseLongs = responses.map(rr => rr.response.asInstanceOf[Long])
        val sum: Long = nums.sum
        val returnedSum: Long = responseLongs.sum
        logger.info("Sum is {} compared to the expected value of {}", returnedSum, sum)
        Assert.assertEquals(sum, returnedSum)
        logger.info("Message is now done")
        logger.info("UNSUBSCRIBING NOW!")
        ms.unsubscribe(id)
        f = ms.publish(Message("this is a test object", new MessageHeaders(Option("test"))))
        logger.info("Just returned from publishing message")
        while(!f.isCompleted) {
            logger.info("2nd Message not done")
            Thread.sleep(200)
        }
        responses = f.value.get.get
        Assert.assertEquals(0, responses.size)
    }

    @Test
    @throws[Exception]
    def simpleSparkSubscribePublishPrioritizingHandlers() {
        var x: Int = 0
        var handlers = List.empty[MessageHandler]
        val messages = 5
        var nums = List.empty[Long]
        // Priority, Value
        var priorities = List.empty[(Long, Long)]
        while (x < messages) {
            val w: Long = ThreadLocalRandom.current.nextInt(500, 1000)
            val p: Long = ThreadLocalRandom.current.nextLong(0, 1000)
            nums = w :: nums
            priorities = (p, w) :: priorities
            handlers = new MessageHandler {
                override val priority = p
                override def apply[T <: java.io.Serializable](v: Message[T]): Response[_] = {
                    val logger = LoggerFactory.getLogger(getClass)
                    logger.info("About to wait {} before completing with priority {}", w, p.toString)
                    Thread.sleep(x*2000)
                    logger.info("Finished waiting {} returning value {} with priority {}", w.toString, w.toString, p.toString)
                    SimpleResponse(w)
                }

                override def compare(that: MessageHandler): Int = 0
            } :: handlers
            x += 1
        }
        ms.subscribe("test", handlers)
        logger.info("About to publish message")
        val l = new CountDownLatch(1)
        val f = ms.publish(Message("this is a test object", new MessageHeaders(Option("test"))), CompleteResponse(
            (replies) => {
                logger.info("Now in response!")
                val responses = replies.responses
                priorities = priorities.sortBy(_._1)
                x = 0
                while (x < priorities.size) {

                    logger.info("For priority {} checking for value {} with {}",
                        priorities(x)._1.toString, priorities(x)._2.toString, responses(x).response.toString)
                    Assert.assertEquals(priorities(x)._2, responses(x).response)
                    x += 1
                }
                l.countDown()
                SimpleResponse(20)
            }
        ))
        logger.info("Just returned from publishing message")
        implicit val ec: ExecutionContext = executionContext
        l.await()
    }

    /*
    @Test def testIgniteAccess() {
        val handlers: util.List[MessageHandler[T, R]] = new util.ArrayList[MessageHandler[T, R]]
        val messages: Int = 20
        val nums: util.List[Long] = new util.ArrayList[_]
        var x: Int = 0
        while (x < messages) {
            {
                val w: Long = ThreadLocalRandom.current.nextInt(500, 1000)
                val priority: Long = ThreadLocalRandom.current.nextLong(0, 1000)
                nums.add(w)
                handlers.add(new MessageHandlerImpl[T, R]((m) -> {
                    LoggerFactory.getLogger(getClass()).debug("About to wait {} before completing", w);
                    try {
                        Thread.sleep(w);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoggerFactory.getLogger(getClass()).debug("Finished waiting {} returning value {}", w, w);

                    final Ignite ignite = Ignition.localIgnite();
                    ignite.compute().broadcast(() -> {
                        logger.info("IM COMING FROM IGNITE WITHIN A HANDLER EXECUTED ON SPARK!!!");
                    });

                    return new SimpleResponse(w);
                }, priority))
            }
            {
                x += 1; x - 1
            }
        }
        ms.subscribe("test", handlers)
        logger.info("About to publish message")
        val f: Future[Tuple2[util.List[Response[Long]], Response[R]]] = ms.quickPublish(new Message[_]("this is a test object", UUID.randomUUID, System.currentTimeMillis, new MessageHeaders("test", null)))
        logger.info("Just returned from publishing message")
        while (!f.isDone) {
            {
                logger.info("Message not done")
                Thread.sleep(200)
            }
        }
        val responses: util.List[Response[Long]] = f.get._1
        val responseLongs: LongStream = responses.stream.mapToLong(r -> r.getResponse())
        val sum: Long = nums.stream.mapToLong(l -> l).sum
        val returnedSum: Long = responseLongs.sum
        logger.info("Sum is {} compared to the expected value of {}", returnedSum, sum)
        Assert.assertEquals(sum, returnedSum)
        logger.info("Message is now done")
    }
    */
}