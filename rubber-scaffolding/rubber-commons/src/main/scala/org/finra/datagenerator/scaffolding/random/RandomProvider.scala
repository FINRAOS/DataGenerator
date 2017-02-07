package org.finra.datagenerator.scaffolding.random

import org.springframework.stereotype.Service
import java.util.Random

/**
  * Created by dkopel on 9/30/16.
  */
@Service
trait RandomProvider {
    def nextObject[T](clazz: Class[T], args: Parameter[_]*): T
    def nextObject[T](clazz: Class[T]): T
    def next(bits: Int): Int
    def nextBoolean: Boolean
    def nextBytes(bytes: Array[Byte])
    def nextDouble(): Double
    def nextGaussian(): Double
    def nextFloat(): Float
    def nextInt(): Int
    def nextLong(): Long
    def setSeed(seed: Long)
}