package org.finra.datagenerator.scaffolding.random.randomizers

import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config.AnnotationField
import org.finra.datagenerator.scaffolding.random.predicate.{ClassRandomGenerator, RandomContext}

/**
  * Created by dkopel on 12/6/16.
  */
class LongRandomizer extends ClassRandomGenerator[Long]
    with Configurable with AnnotationCapable with Logging {
    override def apply(rc: RandomContext): Long = {
        val min = rc.conf.conf[Long](LongRandomizerMinName).getValue()
        val max = rc.conf.conf[Long](LongRandomizerMaxName).getValue()
        logger.debug("Min {}, Max {}", min, max)

        rc.jpr.longs.nextLong(min, max)
    }

    object LongRandomizerMinName extends ConfigName("longRandomizerMin")
    object LongRandomizerMaxName extends ConfigName("longRandomizerMax")

    val minDef: ConfigDefinition[Long] = ConfigDefinition[Long](
        LongRandomizerMinName,
        Some(Long.MinValue)
    )

    val maxDef: ConfigDefinition[Long] = ConfigDefinition[Long](
        LongRandomizerMaxName,
        Some(Long.MaxValue)
    )

    private val defs = Seq(
        minDef,
        maxDef
    )

    override def configBundle: ConfigBundle = {
        ConfigBundle(
            getClass,
            defs.map(d => (d.name, d)).toMap
        )
    }

    override def name: String = "LongRange"

    override def values: Set[AnnotationField[_, _]] = Set(
        AnnotationField("min", minDef, classOf[Long], classOf[Long]),
        AnnotationField("max", maxDef, classOf[Long], classOf[Long])
    )

    override def classes: Array[Class[_]] = Array(classOf[java.lang.Long], classOf[Long])
}