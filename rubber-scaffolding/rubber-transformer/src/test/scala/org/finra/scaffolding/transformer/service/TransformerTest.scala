package org.finra.scaffolding.transformer.service

import junit.framework.TestCase
import org.finra.scaffolding.config.ConfigurationUtils
import org.finra.scaffolding.random.core.RubberRandomImpl
import org.finra.scaffolding.transformer.{BigClass, GreatClass, SmallClass}
import org.finra.scaffolding.utils.Logging
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

import scala.collection.JavaConverters._
/**
  * Created by dkopel on 12/14/16.
  */

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@RunWith(classOf[SpringRunner])
@SpringBootTest
class TransformerTest extends TestCase with Logging {

    @SpringBootConfiguration
    @ComponentScan(Array("org.finra.scaffolding.random.*", "org.finra.scaffolding.transformer.*"))
    private class TransformerConfiguration {
        @Bean
        def conf = ConfigurationUtils
    }

    @Test
    def test1: Unit = {
        implicit val rr = new RubberRandomImpl
        val input = new InputTransformationContainer[BigClass]("big", classOf[BigClass], rr.generate[BigClass])
        val input2 = new InputTransformationContainer[GreatClass]("great", classOf[GreatClass], rr.generate[GreatClass])
        val output = new OutputTransformationContainer[SmallClass]("test", classOf[SmallClass])
        val s = List[TransformationContainer[_]](input, input2, output).asJavaCollection
        val cs: java.util.Map[java.lang.Long, java.util.Collection[TransformationContainer[_]]] = collection.mutable.Map(new java.lang.Long(0)->s).asJava
        val tcontext = new TransformationContext(
            new java.lang.Long(0),
            cs,
            new java.util.HashSet()
        )

        val tp = new Transformer
        tp.setGlobal("name", "dovid")
        tp.transform(tcontext)
        //tp.processInputClass(output)
    }
}
