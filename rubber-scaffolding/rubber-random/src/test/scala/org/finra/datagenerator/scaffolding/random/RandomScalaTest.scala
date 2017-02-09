package org.finra.datagenerator.scaffolding.random

import java.util.{Calendar, Date}

import com.dovidkopel.TestLongRange
import com.fasterxml.jackson.databind.ObjectMapper
import com.mifmif.common.regex.Generex
import junit.framework.TestCase
import org.finra.datagenerator.scaffolding.config.{ConfigurationUtils, LocalConfig}
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.core.RubberRandom.{MaxRecursionCountName, RecursionEnabledName, RegexStrategyName}
import org.finra.datagenerator.scaffolding.random.core._
import org.finra.datagenerator.scaffolding.random.exceptions.RecursionDisabledException
import org.finra.datagenerator.scaffolding.random.predicate._
import org.finra.datagenerator.scaffolding.random.randomizers.LongRandomizer
import org.finra.datagenerator.scaffolding.random.support.AnnotationGenerator
import org.finra.datagenerator.scaffolding.random.types.{CollectionTypes, ParameterizedTypeReference, TypeContainer}
import org.junit.runner.RunWith
import org.junit.{Assert, Ignore, Test}
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
  * Created by dkopel on 11/25/16.
  */

@EnableAutoConfiguration
@ComponentScan(Array("org.finra.datagenerator.scaffolding.random.*"))
case class RandomConfiguration() {
    @Bean
    def conf = ConfigurationUtils
}

@RunWith(classOf[SpringJUnit4ClassRunner])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = Array(classOf[RandomConfiguration]))
class RandomScalaTest extends TestCase {
    val logger = LoggerFactory.getLogger(getClass)
    val mapper = new ObjectMapper()

    def toString(value: Any) = mapper.writeValueAsString(value)

    class TestPojo {
        var it: List[Int] = _
        var ma: Map[String, List[BigInt]] = _
        var zz: List[String] = _
    }

    @Test
    def confTest(): Unit = {
//        var r = new RubberRandomImpl
        var r = RubberRandomImpl.apply()
        val v = r.confValue(MaxRecursionCountName)
        logger.debug("val: {}", v)
        r.confs(name=Option(MaxRecursionCountName)).foreach(c => {
            logger.debug("Conf {} value {}", c.conf.name, c.getValue())
        })
    }

    @Test
    def testCustomRandomizer: Unit = {
        implicit var r = new RubberRandomImpl

        val custom = r.generate[TestLongRange]
        logger.debug("Custom randomizers: {}", mapper.writeValueAsString(custom))
        Assert.assertEquals(custom.getCustom, "foo")
        Assert.assertEquals(custom.getAnother, "this is bar")
    }

    @Test
    def testCustomRandomizerScan: Unit = {
        implicit var r = new RubberRandomImpl(
            Seq("org.finra.datagenerator.scaffolding", "com.dovidkopel")
        )

        val b = r.generate[Bar]
        logger.debug("BAR: {}", toString(b))
        Assert.assertEquals(b.getBar, "this is bar")
    }

    @Test
    def testSimpleRegex(): Unit = {
        implicit var r = new RubberRandomImpl
        val r1 = "^((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))-((?!00)\\d{2})-((?!0000)\\d{4})$"r

        val g = r.generate("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"r)
        logger.debug("Regex out: {}", toString(g))

        val g1 = r.generate(r1)
        logger.debug("Regex out: {}", toString(g1))

        r = new RubberRandomImpl
        r.setOverride(RegexStrategyName, ComplexRegex)
        val g2 = r.generate(r1)
        logger.debug("Regex out: {}", toString(g2))
    }

    @Test
    @Ignore
    def generateAnnotations: Unit = {
        val gen = new AnnotationGenerator
        gen.generateAnnotations
    }

    @Test
    def testAnnotations: Unit = {
        implicit var r = new RubberRandomImpl

        val min = Calendar.getInstance()
        min.setTime(new Date())
        min.add(Calendar.YEAR, -5)
        val minYear = Date.from(min.toInstant).getYear
        val max = Calendar.getInstance()
        max.setTime(new Date())
        max.add(Calendar.YEAR, 2)
        val maxYear = Date.from(max.toInstant).getYear

        val amountPred = (amount: Long) => amount >= 10 && amount < 20
        val wagePred = (wage: Long) => wage >= 50 && wage < 60

        Range.apply(0, 10).foreach(i => {
            val out = r.generate[TestLongRange]
            logger.debug("Out result: {}", mapper.writeValueAsString(out))
            Assert.assertTrue(amountPred(out.getAmount))
            Assert.assertTrue(wagePred(out.getWager))
            val currentYear = out.getBirthday.getYear
            logger.debug("Current {}, Min {}, Max {}", currentYear.toString, minYear.toString, maxYear.toString)
            Assert.assertTrue(currentYear >= minYear && currentYear <  maxYear)
        })

        var col = Seq.empty[Long]
        val iterations = 10
        Range.apply(0, iterations).foreach(i => {
            val longVal = r.generate[Long]
            logger.debug("Long value: {}", longVal)

        })
        val longCount = col.count(l => amountPred(l) || wagePred(l))
        logger.debug("Long count {} are within the smaller range out of {} iterations", longCount, iterations)
        Assert.assertTrue(longCount <= 1)
    }

    @Test
    def testPredicateDecorator(): Unit = {
        val r = new RubberRandomImpl
        val lr = new LongRandomizer

        locally {
            implicit val lc = LocalConfig(
                Seq(
                    lr.minDef(0L),
                    lr.maxDef(100L)
                )
            )(r)

            logger.debug("initial max long: {}", lc.confValue(lr.LongRandomizerMaxName));

            lc({
                logger.debug("max long: {}", lc.confValue(lr.LongRandomizerMaxName));
                val out = r.generate(classOf[java.lang.Long])
                Assert.assertTrue(out < 100L)
                Assert.assertTrue(out > 0L)
                logger.debug("Long out: {}", out)
            })(lc)
        }

        locally {
            implicit val rr: RubberRandom = r
            val out = r.generate(classOf[java.lang.Long])
            Assert.assertTrue(out < Long.MaxValue)
            Assert.assertTrue(out > Long.MinValue)
            logger.debug("Long out: {}", out)
        }
    }

    @Test(expected = classOf[RecursionDisabledException])
    def testRecursiveDisabled: Unit = {
        implicit var r = new RubberRandomImpl
        r.setOverride(RecursionEnabledName, false)
        val c = classOf[RecursiveCombinedTest]
        val out2 = r.generate(c)

        logger.debug(mapper.writeValueAsString(out2))
    }

    @Test
    def testRecursive: Unit = {
        Range(0, 10).foreach(i => {
            implicit var r = new RubberRandomImpl
            val count = 10
            r.setOverride(MaxRecursionCountName, count)
            val out2 = r.generate(classOf[RecursiveCombinedTest])
            val outS: String = mapper.writeValueAsString(out2)

            val found = "rct".r.findAllIn(outS).length
            logger.debug("Outs: {}", outS)
            logger.debug("Looking for {} instances of the word `rct` and found {}", count, found)
            Assert.assertEquals(count, found)
        })
    }

    @Test
    def testCollectionRange = {
        implicit var r = new RubberRandomImpl

        r.setOverride(CollectionTypes.CollectionRangeName, Range.inclusive(10, 10))
        val out3 = r.generate(new ParameterizedTypeReference[java.util.Map[java.lang.String, java.lang.Long]]{})
        logger.debug("Out3: {}", out3.toString)
        val outS2: String = mapper.writeValueAsString(out3)
        Assert.assertEquals(10, out3.size)
    }

    @Test
    def testTypeTagToTypeContainer(): Unit = {
        implicit var r = new RubberRandomImpl
        val out = r.generate[Map[String, List[Long]]]
        logger.debug("Out: {}", out)

        val out2 = r.generate[scala.collection.mutable.Map[String, List[Long]]]
        logger.debug("Out 2: {}", out2)

        val out3 = r.generate[java.util.Map[String, List[Long]]]
        logger.debug("Out 3: {}", out3)
    }

    @Test
    def testBigInt: Unit = {
        implicit var r = new RubberRandomImpl
        val out = r.generate[BigInt]
        logger.debug("Out: {}", out)

        val out1 = r.generate[java.math.BigInteger]
        logger.debug("Out: {}", out1)

        val out2 = r.generate[BigDecimal]
        logger.debug("Out: {}", out2)

        val out3 = r.generate[java.math.BigDecimal]
        logger.debug("Out: {}", out3)
    }

    @Test
    def testDate: Unit = {
        implicit var r = new RubberRandomImpl
        val out = r.generate[java.util.Date]
        logger.debug("Out: {}", out)
    }

    @Test
    def testRegex: Unit = {
        val rr = new RegexRandomizer();
        val pZip = "((\\d{5})([- ]\\d{4})?)"
        val pSSN = "((?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))-((?!00)\\d{2})-((?!0000)\\d{4})"

//        implicit val r = new RubberRandomPlusImpl
//        logger.debug("count {}", r.countFromRegex(pZip.r))
//        val s1: String = r.generateFromRegex(pZip.r)
//        logger.debug("random out {}", s1)
//
//        val ii = r.iterateFromRegex[Iterator[String]](pZip.r)
//        Range(1, 10)
//            .foreach(i => {
//                logger.debug("iteration {} {}", i, ii.next())
//            })


        Range(1, 10)
            .foreach(i => {
                val t1 = System.nanoTime()
                val zip1 = rr.generateFromRegex(pSSN)
                val t2 = System.nanoTime()
                logger.debug("Output: {} dur {}", zip1, t2-t1)
            })



        val t4 = System.nanoTime()
        val g = new Generex(pSSN)
        //logger.debug("Possibilities: {}", g.matchedStringsSize())
        //val all = g.getMatchedStrings(100000)
        val t5 = System.nanoTime()
        logger.debug("setup dur {}", t5-t4)
        //logger.debug("All {} dur {}", all.size(), t5-t4)

        Range(1, 10)
            .foreach(i => {
                val t3 = System.nanoTime()

                val zip2 = g.random()
                val t4 = System.nanoTime()
                logger.debug("Generx output {} dur {}", zip2, t4-t3)
            })

//        val ssn1 = rr.generateFromRegex(pSSN)
//        logger.debug("SSN: {}", ssn1)

    }


    @Test
    def testRecursiveCollection: Unit = {
        implicit var r = new RubberRandomImpl

        r.setOverride(CollectionTypes.CollectionRangeName, Range.inclusive(10, 10))
        r.setOverride(MaxRecursionCountName, 5)
        val out3 = r.generate(classOf[OtherRecursive])
        logger.debug("Out3: {}", out3.toString)
        val outS2: String = mapper.writeValueAsString(out3)
    }

    // Ignoring recursive stack overflow test for
    @Test(expected = classOf[StackOverflowError])
    @Ignore
    def testRecursiveStackOverflow: Unit = {
        implicit var r = new RubberRandomImpl
        r.setOverride(MaxRecursionCountName, Int.MaxValue)
        val c = classOf[RecursiveCombinedTest]
        val out2 = r.generate(c)
        val outS: String = mapper.writeValueAsString(out2)
        logger.debug(outS)
        Assert.assertEquals(Long.MaxValue, "rct".r.findAllIn(outS).length)
    }

    @Test
    def testSimpleList: Unit = {
        implicit var r = new RubberRandomImpl
        val c = classOf[TestThing]
        val out2 = r.generate(c)
        logger.debug(mapper.writeValueAsString(out2))
    }

    @Test
    def testSimpleMap: Unit = {
        implicit var r = new RubberRandomImpl

        r.registerPredicate(CustomPredicate,
            CustomRandomPredicate(
                rc => {
                    logger.info("Current iteration {}", rc.tc.iteration)
                    if(rc.parent.isDefined) {
                        logger.info("Parent iteration {}", rc.parent.get.iteration)
                        if(rc.parent.get.iteration == 4 && classOf[java.util.Map[_, _]].equals(rc.parent.get.clazz)) {
                            logger.debug("The parent of this iteration is 4 and is a {}", rc.parent.get.clazz)
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                },
                (rc: RandomContext) => {
                    logger.debug("For the 4th iteration of the map requesting data of {}", rc.tc.clazz)
                    if(rc.tc.clazz.equals(classOf[java.lang.String])) {
                        "string"
                    } else if(rc.tc.clazz.equals(classOf[java.lang.Integer])) {
                        10
                    } else {
                        null
                    }
                },
                0L,
                java.util.UUID.randomUUID()
            )
        )
        val c = classOf[TestMap]
        val out2: TestMap = r.generate(c)
        Assert.assertEquals(out2.getMm.get("string"), 10)
        logger.debug(mapper.writeValueAsString(out2))
    }

    @Test
    def testList: Unit = {
        implicit var r = new RubberRandomImpl
        val out = r.generate(new ParameterizedTypeReference[java.util.List[java.util.Map[java.lang.String, java.lang.Long]]] {})
        logger.debug(mapper.writeValueAsString(out))
    }

    @Test
    def test1(): Unit = {
        implicit var r = new RubberRandomImpl
        var s = r.predicates.filter(p => p(TypeContainer(classOf[Long])))
        Assert.assertEquals(1, s.size)
        Assert.assertTrue(r.generate(classOf[Long]).isValidLong)

        val predLong1 = ClassRandomPredicate(10L, 100, classOf[Long])
        r.registerPredicate(CustomPredicate, predLong1)
        s = r.predicates.filter(p => p(TypeContainer(classOf[Long])))
        Assert.assertEquals(2, s.size)
        logger.debug("s {}", s.size)
        Assert.assertEquals(10L, r.generate(classOf[Long]))
        Assert.assertEquals(10L, r.generate(classOf[Long]))

        val predLong2 = ClassRandomPredicate(500L, 50, classOf[Long])
        r.registerPredicate(CustomPredicate, predLong2)
        s = r.predicates.filter(p => p(TypeContainer(classOf[Long])))
        Assert.assertEquals(3, s.size)
        logger.debug("s {}", s.size)
        Assert.assertEquals(500L, r.generate(classOf[Long]))
        Assert.assertEquals(500L, r.generate(classOf[Long]))

        r.deregisterPredicate(CustomPredicate, predLong2)
        s = r.predicates.filter(p => p(TypeContainer(classOf[Long])))
        Assert.assertEquals(2, s.size)
        logger.debug("s {}", s.size)
        Assert.assertEquals(10L, r.generate(classOf[Long]))
        Assert.assertEquals(10L, r.generate(classOf[Long]))

        val gp = r.generate(classOf[GreatPojo])
        logger.debug(mapper.writeValueAsString(gp))

        val bl = r.generate(classOf[org.finra.datagenerator.scaffolding.random.Food])
        logger.debug(mapper.writeValueAsString(bl))

        val ap = r.generate(new ParameterizedTypeReference[org.finra.datagenerator.scaffolding.random.Blah[Buffet[java.util.UUID]]]() {})
        logger.debug(mapper.writeValueAsString(ap))

        r.seed(7033304L)
        val ii1a = r.generate(classOf[Integer])
        val ii1b = r.generate(classOf[Integer])
        r.seed(7033304L)
        val ii2a = r.generate(classOf[Integer])
        val ii2b = r.generate(classOf[Integer])
        Assert.assertEquals(ii1a, ii2a)
        Assert.assertEquals(ii1b, ii2b)
    }
}
