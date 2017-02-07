package org.finra.datagenerator.scaffolding.transformer.service

import java.time.ZoneOffset
import java.util.Date

import org.finra.datagenerator.scaffolding.config.{ConfigName, ConfigurationUtils}
import org.finra.datagenerator.scaffolding.transformer.{InputA, InputB, InputC, OutputC}
import org.finra.datagenerator.scaffolding.transformer.join.JoinKeys
import org.finra.datagenerator.scaffolding.transformer.service.{InputTransformationContainer, OutputTransformationContainer, TransformationContainer, TransformationContext}
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config.ConfigurationUtils
import org.finra.datagenerator.scaffolding.transformer.function.impl.DateTimeSequential
import org.finra.datagenerator.scaffolding.transformer.{InputA, InputB, InputC, OutputC}
import org.junit.runner.RunWith
import org.junit.{Assert, Test}
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

import scala.collection.JavaConverters._

/**
  * Created by dkopel on 12/20/16.
  */
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@RunWith(classOf[SpringRunner])
@SpringBootTest
class JoinServiceTest extends Logging {

    @SpringBootConfiguration
    @ComponentScan(Array("org.finra.datagenerator.scaffolding.random.*", "org.finra.datagenerator.scaffolding.transformer.*"))
    private[transformer] class Conf {
        @Bean
        def getConfigUtil = ConfigurationUtils
    }

    @Test
    def testJoinKeys: Unit = {
        // Exact same
        Assert.assertTrue(
            JoinKeys(Map("k"->1, "v"->null)).equals(
                JoinKeys(Map("k"->1, "v"->null))
            )
        )

        // That missing key with null value...its okay
        Assert.assertTrue(
            JoinKeys(Map("k"->10, "v"->null)).equals(
                JoinKeys(Map("k"->10))
            )
        )

        // This missing key, that present with null value...its okay
        Assert.assertTrue(
            JoinKeys(Map("k"->10)).equals(
                JoinKeys(Map("k"->10, "v"->null))
            )
        )

        // Different values
        Assert.assertFalse(
            JoinKeys(Map("k"->1, "v"->null)).equals(
                JoinKeys(Map("k"->10, "v"->null))
            )
        )

        // Different values
        Assert.assertFalse(
            JoinKeys(Map("k"->1)).equals(
                JoinKeys(Map("k"->10))
            )
        )

        // One keypair matches, each lacking keys
        Assert.assertTrue(
            JoinKeys(Map("k"->1, "v"->null, "a"->1, "b"->3)).equals(
                JoinKeys(Map("k"->1, "v"->null, "z"->2, "v"->4))
            )
        )
    }

    @Test
    def getJoinKeysTest: Unit = {
        val tt = new Transformer
        var inputsa = List.empty[InputA]
        var inputsb = List.empty[InputB]
        var inputsc = List.empty[InputC]
        val dts = new DateTimeSequential
        //val rr = tt.random


        tt.random.setOverride(ConfigName.apply("longRandomizerMin"), 100L)
        tt.random.setOverride(ConfigName.apply("longRandomizerMax"), 200L)
        for(i <- 0 until 20) {
                val d = new Date(dts.next(null).toEpochSecond(ZoneOffset.UTC))
                val a = tt.random.generate(classOf[InputA])(tt)
                val b = tt.random.generate(classOf[InputB])(tt)
                val c = tt.random.generate(classOf[InputC])(tt)

                a.id = i.toLong
                b.bar = i.toLong
                c.foo = i.toLong
                a.date = d
                b.date1 = d
                c.date2 = d

                if(i == 1) {
                    val e = tt.random.generate(classOf[InputA])(tt)
                    e.id = i.toLong
                    e.date = null
                    inputsa = e :: inputsa
                }

                inputsa = a :: inputsa
                inputsb = b :: inputsb
                inputsc = c :: inputsc
        }

        var oc = new OutputTransformationContainer[OutputC]("output", classOf[OutputC], true)

        val containers = List[TransformationContainer[_]](
            new InputTransformationContainer[java.util.List[InputA]]("ia", inputsa.asJava),
            new InputTransformationContainer[java.util.List[InputB]]("ib", inputsb.asJava),
            new InputTransformationContainer[java.util.List[InputC]]("ic", inputsc.asJava),
            oc
        ).asJavaCollection

        val iC = collection.mutable.Map(
            new java.lang.Long(0L)->containers
        ).asJava

        val context: TransformationContext = new TransformationContext(
            new java.lang.Long(0L),
            iC,
            Set.empty.asJava
        )

        tt.setContext(context)
        tt.getJoinKeys(oc)(context, tt)
        val out = tt.join(context, tt)

        logger.debug("Out: {}", tt.toString(out))

        //logger.debug(" Containers {}", tt.toString(context.containers))
    }
}
