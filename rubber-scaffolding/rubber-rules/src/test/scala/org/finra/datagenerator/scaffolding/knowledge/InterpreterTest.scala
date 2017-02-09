package org.finra.datagenerator.scaffolding.knowledge

import junit.framework.TestCase
import org.apache.commons.lang3.RandomUtils
import org.finra.datagenerator.scaffolding.knowledge
import org.junit.{Assert, Test}

/**
  * Created by dkopel on 9/21/16.
  */

class InterpreterTest extends TestCase {

    def getInterpreter: IMain = {
        val s = new Settings()
        s.usejavacp.value = true
        new scala.tools.nsc.interpreter.IMain(s)
    }

    @Test
    def test1 {
        val i = getInterpreter
        i.bind("num", 4)
        i.eval("val x = 2+num")
        i.eval("val y = x^2")
        i.eval("val t = x+y")
        i.replScope.foreach(s => println(s.nameString + " : " + i.valueOfTerm(s.nameString).get))
        Assert.assertEquals(10, i.valueOfTerm("t").get)
        Assert.assertEquals(i.valueOfTerm("t").get, i.eval("t"))
    }

    @Test
    def test2: Unit = {
        val i = getInterpreter
        def a = true
        def b = false
        def c = 10 > 5
        i.bind("a", a)
        i.bind("b", b)
        i.bind("c", c)
        i.eval("val z = (a || b) && !b")
        Assert.assertEquals(true, i.valueOfTerm("z").get)
    }

    def m(x: Int, y: Int, word: String, t: Map[String, Class[_]]) =
        ((x > y && !((x - 4) < 10)) || word.length > 3)


    case class Fridge() {
        private var opened = false
        def open = opened = true
        def close = opened = false
        def isOpened = opened==true
        def isClosed = opened==false
    }

    case class GreaterThan(x: Int) {
        def test(y: Int) = y > x
        def generate = RandomUtils.nextInt(x, 10000)
    }

    def greaterThan(x: Int) = (y: Int) => x > y


    @Test
    def test3: Unit = {
        val code =
        """def m(x: Int, y: Int, word: String, t: Map[String, Class[_]]) =
         ((x > y && !((x - 4) < 100)) || word.length > 3)"""

        // ((x > y && (x >= 105) || word.length > 3)
        // x >= 105 && y < 105 && word.length > 3

        val x: Int = 0
        val y: Int = 1
        val word: String = "hello"

        x.$greater(y).$amp$amp(x.$minus(4).$less(10).unary_$bang).$bar$bar(word.length.$greater(3))

        import scala.reflect.runtime.universe._
        var t = reify {
            x.$greater(y)
        }

        case class Andrew() {
            def hi :(Andrew, String) = (this, "test")
        }
        case class Dovid() {
            def bye(a: Andrew): String = "test"
        }

        println(reify{ x > y }.tree)
        println(reify{ x + y }.tree)
        println(reify{ Dovid().bye(Andrew().hi._1)  }.tree)
        println(showRaw(reify{ Dovid().bye(Andrew().hi._1)  }.tree))
        println(showRaw(t.tree))

        val f = Fridge()

        f.isOpened

        println(greaterThan(10).apply(5))

        // a && b || (c && !d)

        Assert.assertEquals(true, knowledge.invokeSnippet(
            code,
            "m",
            Map("x" -> 4, "y" -> 3, "word" -> "hello", "t" -> Map())
        ))

        Assert.assertEquals(false, knowledge.invokeSnippet(
            code,
            "m",
            Map("x" -> 1, "y" -> 30, "word" -> "yo", "t" -> Map())
        ))

        Assert.assertEquals(false, knowledge.invokeSnippet(
            code,
            "m",
            Map("x" -> 100, "y" -> 1, "word" -> "yo", "t" -> Map())
        ))

        val analysis = knowledge.analyzeCodeSnippet(code)
        Assert.assertEquals(4, analysis.args.size)
        Assert.assertEquals(Class.forName("scala.Int"), analysis.args.get("x").get.clazz)
        Assert.assertEquals(Class.forName("scala.Int"), analysis.args.get("y").get.clazz)
        Assert.assertEquals(Class.forName("java.lang.String"), analysis.args.get("word").get.clazz)
        Assert.assertEquals(Class.forName("scala.collection.immutable.Map"), analysis.args.get("t").get.clazz)

        println(analysis.analyzed)
    }

//    def test4(): Unit = {
//        var create: Int = _
//        var cancel: Int = _
//        var exchange: Int = _
//        var n: Int = _
//        var seq: List[Int] = List.empty[Int]
//        def l = seq.length
//        var fullCancel: Int = _
//
//        create = 0 // Index 0
//        cancel > create+n // Index value of cancel must be $gt than create
//        exchange = create+1
//        fullCancel = l-1
//    }

    def test5: Unit = {
        val gt = GreaterThan(5)
        Assert.assertTrue(gt.test(10))
        Assert.assertFalse(gt.test(3))
        Range.apply(1, 10).foreach(u => {
            println(gt.generate)
        })
    }
}