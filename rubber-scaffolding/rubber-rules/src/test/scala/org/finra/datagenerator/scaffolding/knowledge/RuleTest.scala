package org.finra.datagenerator.scaffolding.knowledge

import org.finra.datagenerator.scaffolding.utils.Logging
import org.junit.runner.RunWith
import org.junit.{Assert, Test}
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner


/**
  * Created by dkopel on 10/5/16.
  */


@SpringBootConfiguration
@ComponentScan(Array("org.finra.scaffolding.knowledge"))
class RConf {}

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@RunWith(classOf[SpringRunner])
@SpringBootTest(classes=Array(classOf[RConf]))
class RuleTest extends Logging {


    @Test
    def test0: Unit = {
        val gt = GreaterThan(20)
        val gt1 = GreaterThan(20)("other")
        implicit val kc = SimpleKnowledgeContext()

        kc.data += "num"->4
        kc.data += "other"->10

        Assert.assertTrue(gt(10).test)
        Assert.assertFalse(gt(30).test)
        Assert.assertTrue(gt("other").test)
        Assert.assertTrue(gt1.test)
        kc.data += "other"->30
        Assert.assertFalse(gt1.test)
    }

    @Test
    def testRuleUnbound: Unit = {
        val gt = GreaterThan(20)
        val gt1 = GreaterThan(20)("other")
        implicit val kc = SimpleKnowledgeContext()

        kc.data += "num"->4
        kc.data += "other"->10

        val cs = Map[String, CriteriaContainer]("gt"->gt, "gt1"->gt1)
        val r = RuleCompiler.quickRule(cs)
        var x = 0
        try {
            r.evaluate
        } catch {
            case e: IllegalArgumentException => x += 1
        }
        Assert.assertTrue(x==1)
    }

    @Test
    def testEval(): Unit = {
        val gt = GreaterThan(20)
        val gt1 = GreaterThan(20)("other")
        implicit val kc = SimpleKnowledgeContext()

        kc.data += "num"->4
        kc.data += "other"->10

        val cs = Map[String, CriteriaContainer]("gt"->gt, "gt1"->gt1)
        val r = RuleCompiler.quickRule(cs)

        Assert.assertFalse(cs("gt").test(400))
        Assert.assertTrue(cs("gt").test(10))

        r.criteria("gt").bind(400)
        Assert.assertFalse(r.evaluate)
        r.criteria("gt").bind(10)
        Assert.assertTrue(r.evaluate)
        r.criteria("gt").bind("ninja")
        kc.data += "ninja"->400
        Assert.assertFalse(r.evaluate)
        kc.data += "ninja"->15
        Assert.assertTrue(r.evaluate)

        kc.data += "num"->30
    }

    @Test
    def testRuleEvalDur: Unit = {
        val gt = GreaterThan(20)
        val gt1 = GreaterThan(20)("other")
        implicit val kc = SimpleKnowledgeContext()

        kc.data += "num"->4
        kc.data += "other"->10

        val startCompile = System.currentTimeMillis()
        val cs = Map[String, CriteriaContainer]("gt"->gt, "gt1"->gt1)
        val clazz = RuleCompiler.compile(cs)
        val endCompile = System.currentTimeMillis()
        val compileDuration = endCompile - startCompile
        logger.info("Compilation took {}ms", compileDuration)

        var startLoad = System.currentTimeMillis()
        val factory = RuleCompiler.factory(clazz)
        val r = factory.newInstance(cs)
        var endLoad = System.currentTimeMillis()
        val loadDuration = endLoad - startLoad
        logger.info("Load took {}ms", loadDuration)
    }

    @Test
    def test1() {
        implicit val kc = SimpleKnowledgeContext()

        val gt = GreaterThan(20.0D) // 20 is greater than the value of `num`
        val lt = LessThan(2) // 2 is less than the value of `num`
        val cs = Map[String, CriteriaContainer]("gt"->gt, "lt"->lt)
        val r = RuleCompiler.quickRule(cs)

        kc.data += "num"->4
        r.criteria("gt").bind("num")
        r.criteria("lt").bind(20)
        Assert.assertTrue(r.evaluate)

        kc.data += "num"->(30 + 3/4)
        Assert.assertFalse(r.evaluate)

        Assert.assertTrue(RuleCompiler.quickRule(criteria=cs, code=Option("gt || lt")).evaluate)

        val gt1 = GreaterThan(-20.0, "num")
        val cs1 = Map[String, CriteriaContainer]("gt1"->gt1, "lt"->lt("other"))
        kc.data += "num"->5
        kc.data += "other"->10
        Assert.assertFalse(RuleCompiler.quickRule(criteria=cs1, code=Option("gt1")).evaluate)
        Assert.assertTrue(RuleCompiler.quickRule(criteria=cs1, code=Option("!gt1 && lt")).evaluate)

//        kc.data += "test" -> 40
//        val si = SequentialIncrement(10, 20 ,30, "test")
//        val cs2 = Map[String, CriteriaContainer]("si"->si)
//        Assert.assertTrue(RuleCompiler.quickRule(criteria=cs2).evaluate)
//
//        kc.data += "test" -> 5
//        Assert.assertFalse(RuleCompiler.quickRule(criteria=cs2).evaluate)
//
//        kc.data += "test" -> 40
//        kc.data += "test2" -> -50
//        val si2 = (x: Any)=>SequentialIncrement("test2", 10, 20 ,30, "test")
//        val cs3 = Map[String, CriteriaContainer]("si"->si2)
//        Assert.assertTrue(RuleCompiler.quickRule(criteria=cs3).evaluate)

        kc.extend
    }


    def addOne(x: Double) = x+1
    def isGreaterThan(x: Double, y: Double) = x > y

    def test6: Unit = {


        val y = 4.0D

        import scala.reflect.runtime.universe._

        val x :Double = 4.0D
        //Assert.assertTrue(isGreaterThan(addOne(x), y))
        println(com.google.common.collect.Range.atLeast(y.asInstanceOf[java.lang.Double]))
        println(showRaw(reify{isGreaterThan(addOne(x), y)}.tree))


        class Workflow(funcs: Map[String, (AnyVal) => AnyVal], order: Seq[String]) {
            var results = Map.empty[String, AnyVal]

            def apply(): Unit = {
                order.foreach(o => results += o->funcs(o).apply())
            }
        }

    }
}
