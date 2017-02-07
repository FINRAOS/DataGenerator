package org.finra.datagenerator.scaffolding.random.randomizers

import org.finra.datagenerator.scaffolding.config.{ConfigDefinition, ConfigName, Configurable}
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.predicate.RandomContext

/**
  * Created by dkopel on 12/12/16.
  */
class BigDecimalRandomizer extends (RandomContext=>java.lang.Number) with Configurable {
    override def apply(rc: RandomContext): java.lang.Number = {
        val bits = rc.conf.conf[Integer](BigDecimalBitsName).getValue()
        val scaleRange = rc.conf.conf[Range](BigDecimalScaleName).getValue()

        val bigInt = new java.math.BigInteger(bits, rc.jpr.random)
        val scale: Int = scaleRange(rc.jpr.ints.nextInt(0, scaleRange.size-1))
        val bd = new java.math.BigDecimal(bigInt, scale)

        if(rc.tc.clazz.equals(classOf[java.math.BigDecimal])) bd
        else BigDecimal(bd)
    }

    object BigDecimalBitsName extends ConfigName("bigDecimalBits")
    object BigDecimalScaleName extends ConfigName("bigDecimalScale")

    val bdBitsDef = ConfigDefinition[Integer](
        BigDecimalBitsName,
        Some(128)
    )

    val bdScaleDef = ConfigDefinition[Range](
        BigDecimalScaleName,
        Some(Range(-20, 20))
    )

    override def configBundle: ConfigBundle = ConfigBundle(
        getClass,
        Seq(bdBitsDef, bdScaleDef)
    )
}