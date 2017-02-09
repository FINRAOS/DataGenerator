package org.finra.datagenerator.scaffolding.random.core

import org.finra.datagenerator.scaffolding.random.JavaPrimitiveRandomizer

/**
  * Created by dkopel on 12/6/16.
  */
trait JavaPrimitives {
    private var _seed: Long = 0L
    private var _jpr: JavaPrimitiveRandomizer = JavaPrimitives.jpr

    def seed(seed: Long) = {
        _seed = seed
        _jpr = new JavaPrimitiveRandomizer(seed)
    }

    def seed: Long = _seed

    def jpr: JavaPrimitiveRandomizer = _jpr
}
object DefaultJavaPrimitives extends JavaPrimitives {
    private var _seed: Long = 0L
    private var _jpr: JavaPrimitiveRandomizer = JavaPrimitives.jpr

    override def seed(seed: Long): Unit = {
        _seed = seed
        _jpr = new JavaPrimitiveRandomizer(seed)
    }

    override def seed: Long = _seed

    override def jpr: JavaPrimitiveRandomizer = _jpr
}
object JavaPrimitives {
    val jpr = new JavaPrimitiveRandomizer()
}