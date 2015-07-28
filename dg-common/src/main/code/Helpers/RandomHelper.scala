/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Helpers

import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.JDKRandomGenerator

import scala.beans.BeanProperty
import scala.util.Random

/**
 * Configurable randomizers managed per thread, including separate randomizers based on two different levels --
 * things that should change less often (the information in the data) and things that should change more often
 * (the unique IDs that key the data).
 */
object RandomHelper {
  private val threadToRandomSeedsMap = new collection.parallel.mutable.ParHashMap[Thread, (scala.util.Random, scala.util.Random)]()
  /**
   * Generates random seeds for creating randomizers for data that doesn't need to be globally unique.
   */
  @BeanProperty var randomSeedRandomizer = new util.Random()

  /**
   * Initial part of random seed to use for randomizers for data that needs to be globally unique.
   */
  @BeanProperty var randomSeedBaseForGloballyUniqueIds: Short = (util.Random.nextInt(65535) - 32767).toShort

  /**
   * Given a unique value, combines it with configurable random seeds and creates randomizers for this thread.
   * @param uniqueValue A long limited to 48 unsigned bits (method will fail if greater)
   * @param randomSeedOption
   * @param globalRandomSeedBaseOption
   * @return
   */
  def setUpRandomSeedsForCurrentThreadBasedOnUniqueValue(uniqueValue: Long, randomSeedOption: Option[Long] = None, globalRandomSeedBaseOption: Option[Short] = None): (Random, Random) = {
    val currentThread = Thread.currentThread()
    val localRandomSeed = if (randomSeedOption.nonEmpty) randomSeedOption.get else randomSeedRandomizer.nextLong()
    val localGlobalRandomSeedBase = if (globalRandomSeedBaseOption.nonEmpty) globalRandomSeedBaseOption.get else randomSeedBaseForGloballyUniqueIds
    val seedAndGlobalSeed = (new scala.util.Random(localRandomSeed),
      new scala.util.Random(NumericHelper.concatenateTwoNumbers48BitAnd16Bit(uniqueValue, localGlobalRandomSeedBase)))
    threadToRandomSeedsMap +=
      ((currentThread,
      seedAndGlobalSeed))
    println(s"Setting up random seeds for thread ${currentThread.getId}: Seed is ${localRandomSeed}. Initial random val is ${seedAndGlobalSeed._1.nextInt()}.")
    seedAndGlobalSeed
  }

  /**
   * Remove the randomizers for the current thread.
   */
  def removeEntryForCurrentThread(): Unit = {
    val currentThread = Thread.currentThread()
    println(s"Removing thread ${currentThread.getId}. Final random val is ${threadToRandomSeedsMap(currentThread)._1.nextInt()}.")
    threadToRandomSeedsMap.remove(currentThread)
  }

  /**
   * Get the configured-seed randomizer for the current thread.
   * @return
   */
  def randWithConfiguredSeed = {
    val currentThread = Thread.currentThread()
    if (!threadToRandomSeedsMap.contains(currentThread)) {
      // In current use case this only happens on the main thread, but for other uses cases it will be default behavior.
      setUpRandomSeedsForCurrentThreadBasedOnUniqueValue(0)._1
    } else {
      threadToRandomSeedsMap(currentThread)._1
    }
  }

  /**
   * Get the configured-seed globally unique randomizer for the current thread.
   * @return
   */
  def randForGloballyUniqueIds = {
    val currentThread = Thread.currentThread()
    if (!threadToRandomSeedsMap.contains(currentThread)) {
      // In current use case this only happens on the main thread, but for other uses cases it will be default behavior.
      setUpRandomSeedsForCurrentThreadBasedOnUniqueValue(0)._2
    } else {
      threadToRandomSeedsMap(currentThread)._2
    }
  }

  /**
   * Get random alphanumeric string of maximum length.
   * @param length
   * @param isGloballyRandom
   * @return
   */
  def randomAlphanumericString(length: Int, isGloballyRandom: Boolean = false, minLength: Option[Int] = None): String = {
    randomStringFromAllowableChars(length, CharHelper.alphanumericChars, isGloballyRandom, minLength)
  }
  /**
   * Get random hexadecimal string of maximum length.
   * @param length
   * @param isGloballyRandom
   * @return
   */
  def randomHexString(length: Int, isGloballyRandom: Boolean = false, minLength: Option[Int] = None): String = {
    randomStringFromAllowableChars(length, CharHelper.hexCharsLowercase, isGloballyRandom, minLength)
  }

  /**
   * Get random string of maximum length including allowable characters 8, 9, a, and b
   * @param isGloballyRandom
   * @return
   */
  def randomHexCharFrom8ToB(isGloballyRandom: Boolean = false): Char = {
    val random = if (isGloballyRandom) randForGloballyUniqueIds else randWithConfiguredSeed
    val randomIndex = random.nextInt(CharHelper.hexCharsBetween8AndB.length)
    CharHelper.hexCharsBetween8AndB(randomIndex)
  }

  /**
   * Get a random int between two numbers, inclusive
   * @param min Minimum, inclusive
   * @param max Maximum, inclusive
   * @param isGloballyRandom
   * @return
   */
  def randomIntInRange(min: Int, max: Int, isGloballyRandom: Boolean = false): Int = {
    val random = if (isGloballyRandom) randForGloballyUniqueIds else randWithConfiguredSeed
    min + random.nextInt(max - min + 1)
  }

  /**
   * Generate a random string from allowable characters
   * @param length
   * @param chars
   * @param isGloballyRandom
   * @return
   */
  def randomStringFromAllowableChars(length: Int, chars: Seq[Char], isGloballyRandom: Boolean = false, minLength: Option[Int] = None): String = {
    val adjustedLength =
      if (minLength.isEmpty) length
      else randomIntInRange(minLength.get, length, isGloballyRandom)

    if (adjustedLength == 0) {
      ""
    } else {
      val random = if (isGloballyRandom) randForGloballyUniqueIds else randWithConfiguredSeed
      val sb = new StringBuilder
      for (i <- 1 to adjustedLength) {
        val randomIndex = random.nextInt(chars.length)
        sb.append(chars(randomIndex))
      }
      sb.toString()
    }
  }

  /**
   * Returns true (percentage * 100)% of the time, else false
   * @param percentage A number between 0 and 1.0 (or else, if below 0 it will always evaluate false, and >= 1 will always evaluate true)
   * @return Boolean
   */
  def evaluateProbability(percentage: Double): Boolean = {
    randWithConfiguredSeed.nextDouble <= percentage
  }

  /**
   * Get the next Long from the configured-seed randomizer for the current thread, with specified maximum size (exclusive).
   * @param maxSizeExclusive
   * @return
   */
  def nextLong(maxSizeExclusive: Long): Long = {
    (randWithConfiguredSeed.nextDouble() * maxSizeExclusive).toLong
  }

  /**
   * Get the next Long from the configured-seed randomizer for the current thread, with specified minimum size (inclusive)
   * and specified maximum size (exclusive).
   * @param minSizeInclusive
   * @param maxSizeExclusive
   * @return
   */
  def nextLong(minSizeInclusive: Long, maxSizeExclusive: Long) : Long = {
    assert(minSizeInclusive >= 0)
    assert(maxSizeExclusive > minSizeInclusive)

    val scale = maxSizeExclusive - minSizeInclusive
    val returnVal = nextLong(scale) + minSizeInclusive
    assert(returnVal >= 0)
    returnVal
  }

  /**
   * Same behavior as java.util.UUID.randomUUID() except uses a random generator with configurable seed.
   * @param isGloballyRandom
   * @return
   */
  def randomUuid(isGloballyRandom: Boolean = false): String = {
    // xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx where x is any hexadecimal digit and y is one of 8, 9, A, or B (e.g., f47ac10b-58cc-4372-a567-0e02b2c3d479)
    s"${randomHexString(8, isGloballyRandom)}-${randomHexString(4, isGloballyRandom)}-4${randomHexString(3, isGloballyRandom)}-${randomHexCharFrom8ToB(isGloballyRandom)}${randomHexString(3, isGloballyRandom)}-${randomHexString(12, isGloballyRandom)}"
  }

  /**
   * Gamma generators for main thread, keyed by random and globally random seeds.
   */
  private val gammaGenerators = new collection.mutable.HashMap[(Double, Double), GammaDistribution]()

  /**
   * Get the next integer from a gamma distribution of the specified shape and scale, with optional random seed.
   * Not yet thread-aware because so far we have only needed to use this from the main thread.
   * @param gammaDistShape
   * @param gammaDistScale
   * @param randomSeedOption
   * @return
   */
  def getNextIntFromGammaDistribution(gammaDistShape: Double, gammaDistScale: Double, randomSeedOption: Option[Long] = None): Int = {
    if (!gammaGenerators.contains((gammaDistShape, gammaDistScale))) {
      val localRandomSeed = if (randomSeedOption.nonEmpty) randomSeedOption.get else randomSeedRandomizer.nextLong()
      gammaGenerators.put((gammaDistShape, gammaDistScale), new GammaDistribution(new JDKRandomGenerator() { setSeed(localRandomSeed)}, gammaDistShape, gammaDistScale))
    }

    val gammaGenerator = gammaGenerators.get((gammaDistShape, gammaDistScale)).get
    val nextValInGamma = gammaGenerator.sample

    gammaGenerators((gammaDistShape, gammaDistScale)) = gammaGenerator // Not sure if this is necessary, just paranoid.

    math.round(nextValInGamma).toInt
  }
}
