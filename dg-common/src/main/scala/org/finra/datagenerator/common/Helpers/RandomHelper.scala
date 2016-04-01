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

package org.finra.datagenerator.common.Helpers

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
  /**
   * Implicit methods for a random generator
   * @param random Random number generator
   */
  implicit class RandomExtensions(val random: Random) extends AnyVal {
    /**
     * Get next long between 0 (inclusive) and max (exclusive)
     * @return Long >= 0 and < max
     * @param maxSizeExclusive Max number (exclusive)
     */
    def nextLong(maxSizeExclusive: Long): Long = {
      var bits: Long = 0L
      var result: Long = 0L
      do {
        bits = (random.nextLong << 1) >>> 1
        result = bits % maxSizeExclusive
      } while (bits - result + (maxSizeExclusive - 1) < 0L)
      result
    }

    /**
     * Get the next Long with specified minimum size (inclusive)
     * and specified maximum size (exclusive).
     * @param minSizeInclusive Min size (inclusive), >= 0
     * @param maxSizeExclusive Max size (exclusive)
     * @return Random long in range [min, max)
     */
    def nextLong(minSizeInclusive: Long, maxSizeExclusive: Long) : Long = {
      require(minSizeInclusive >= 0)
      require(maxSizeExclusive > minSizeInclusive)

      val scale = maxSizeExclusive - minSizeInclusive
      val returnVal = nextLong(scale) + minSizeInclusive
      require(returnVal >= 0)
      returnVal
    }

    /**
     * Generate a random alphabetic string from allowable characters
     * @param length Length of string to generate; acts as max length if minLength is also specified
     * @param minLengthMaybe If specified, then length is randomized between minLength and length
     * @return Random string containing only alphabetic characters
     */
    def randomAlphabeticString(length: Int, minLengthMaybe: Option[Int] = None): String = {
      randomStringFromAllowableChars(length, CharHelper.alphabeticChars, minLengthMaybe)
    }
    /**
     * Generate a random alphanumeric string from allowable characters
     * @param length Length of string to generate; acts as max length if minLength is also specified
     * @param minLengthMaybe If specified, then length is randomized between minLength and length
     * @return Random string containing only alphanumeric characters
     */
    def randomAlphanumericString(length: Int, minLengthMaybe: Option[Int] = None): String = {
      randomStringFromAllowableChars(length, CharHelper.alphanumericChars, minLengthMaybe)
    }
    /**
     * Generate a random hexadecimal string from allowable characters
     * @param length Length of string to generate; acts as max length if minLength is also specified
     * @param minLengthMaybe If specified, then length is randomized between minLength and length
     * @return Random hexadecimal string (lowercase)
     */
    def randomHexString(length: Int, minLengthMaybe: Option[Int] = None): String = {
      randomStringFromAllowableChars(length, CharHelper.hexCharsLowercase, minLengthMaybe)
    }

    /**
     * Get random string of maximum length including allowable characters 8, 9, a, and b
     * @return Random hex char from 8 to b (lowercase)
     */
    def randomHexCharFrom8ToB: Char = {
      val randomIndex = random.nextInt(CharHelper.hexCharsBetween8AndB.length)
      CharHelper.hexCharsBetween8AndB(randomIndex)
    }

    /**
     * Get a random int between two numbers, inclusive
     * @param min Minimum, inclusive
     * @param max Maximum, inclusive
     * @return Random int in range
     */
    def randomIntInRange(min: Int, max: Int): Int = {
      min + random.nextInt(max - min + 1)
    }

    /**
     * Generate a random string from allowable characters
     * @param length Length of string to generate; acts as max length if minLength is also specified
     * @param chars Allowable characters
     * @param minLengthMaybe If specified, then length is randomized between minLength and length
     * @return Random string containing only the allowed characters
     */
    def randomStringFromAllowableChars(length: Int, chars: Seq[Char], minLengthMaybe: Option[Int] = None): String = {
      val adjustedLength =
        if (minLengthMaybe.isEmpty) {
          length
        }
        else {
          randomIntInRange(minLengthMaybe.get, length)
        }

      if (adjustedLength == 0) {
        ""
      } else {
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
     * @return Whether or not probability passed
     */
    def evaluateProbability(percentage: Double): Boolean = {
      random.nextDouble <= percentage
    }

    /**
     * Same behavior as java.util.UUID.randomUUID().
     * @return Random UUID
     */
    def randomUuid: String = {
      // xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx where x is any hexadecimal digit and y is one of 8, 9, A, or B (e.g., f47ac10b-58cc-4372-a567-0e02b2c3d479)
      s"${randomHexString(8)}-${randomHexString(4)}-4${randomHexString(3)
      }-${randomHexCharFrom8ToB}${randomHexString(3)}-${randomHexString(12)}"
    }
  }

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
   * @param randomSeedMaybe Random seed for non-globally unique values (typical use case, this seed should be same across runs)
   * @param globalRandomSeedBaseMaybe Random seed for non-globally unique values (typical use case, this seed should be same across runs)
   * @return 2-Tuple of randomizers for this thread
   */
  def setUpRandomSeedsForCurrentThreadBasedOnUniqueValue(uniqueValue: Long, randomSeedMaybe: Option[Long] = None
        , globalRandomSeedBaseMaybe: Option[Short] = None): (Random, Random) = {
    val currentThread = Thread.currentThread()
    val localRandomSeed = if (randomSeedMaybe.nonEmpty) randomSeedMaybe.get else randomSeedRandomizer.nextLong()
    val localGlobalRandomSeedBase = if (globalRandomSeedBaseMaybe.nonEmpty) globalRandomSeedBaseMaybe.get else randomSeedBaseForGloballyUniqueIds
    val seedAndGlobalSeed = (new scala.util.Random(localRandomSeed),
      new scala.util.Random(NumericHelper.concatenateTwoNumbers48BitAnd16Bit(uniqueValue, localGlobalRandomSeedBase)))
    threadToRandomSeedsMap +=
      ((currentThread,
      seedAndGlobalSeed))
    println(s"Setting up random seeds for thread ${currentThread.getId // scalastyle:ignore
      }: Seed is ${localRandomSeed}. Initial random val is ${seedAndGlobalSeed._1.nextInt()}.")
    seedAndGlobalSeed
  }

  /**
   * Remove the randomizers for the current thread.
   */
  def removeEntryForCurrentThread(): Unit = {
    val currentThread = Thread.currentThread()
    println(s"Removing thread ${currentThread.getId}. Final random val is ${threadToRandomSeedsMap(currentThread)._1.nextInt()}.") // scalastyle:ignore
    threadToRandomSeedsMap.remove(currentThread)
  }

  /**
   * Get the configured-seed randomizer for the current thread.
   * @return Randomizer for values that are not intended to be globally unique (e.g., across multiple application runs)
   */
  def randWithConfiguredSeed: Random = {
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
   * @return Randomizer for values that are intended to be globally unique (e.g., across multiple application runs)
   */
  def randForGloballyUniqueIds: Random = {
    val currentThread = Thread.currentThread()
    if (!threadToRandomSeedsMap.contains(currentThread)) {
      // In current use case this only happens on the main thread, but for other uses cases it will be default behavior.
      setUpRandomSeedsForCurrentThreadBasedOnUniqueValue(0)._2
    } else {
      threadToRandomSeedsMap(currentThread)._2
    }
  }

  /**
   * Gets the specified randomizer
   * @param isGloballyRandom Whether or not to get the globally unique randomizer
   * @return Randomizer
   */
  def getRandomizer(isGloballyRandom: Boolean = false): Random = {
    if (isGloballyRandom) {
      randForGloballyUniqueIds
    } else {
      randWithConfiguredSeed
    }
  }

  /**
   * Generate a random alphabetic string from allowable characters
   * @param length Length of string to generate; acts as max length if minLength is also specified
   * @param isGloballyRandom Whether or not to use global randomizer
   * @param minLengthMaybe If specified, then length is randomized between minLength and length
   * @return Random string containing only alphabetic characters
   */
  def randomAlphabeticString(length: Int, isGloballyRandom: Boolean = false, minLengthMaybe: Option[Int] = None): String = {
    getRandomizer(isGloballyRandom).randomAlphabeticString(length, minLengthMaybe)
  }
  /**
   * Generate a random alphanumeric string from allowable characters
   * @param length Length of string to generate; acts as max length if minLength is also specified
   * @param isGloballyRandom Whether or not to use global randomizer
   * @param minLengthMaybe If specified, then length is randomized between minLength and length
   * @return Random string containing only alphanumeric characters
   */
  def randomAlphanumericString(length: Int, isGloballyRandom: Boolean = false, minLengthMaybe: Option[Int] = None): String = {
    getRandomizer(isGloballyRandom).randomAlphanumericString(length, minLengthMaybe)
  }
  /**
   * Generate a random hexadecimal string from allowable characters
   * @param length Length of string to generate; acts as max length if minLength is also specified
   * @param isGloballyRandom Whether or not to use global randomizer
   * @param minLengthMaybe If specified, then length is randomized between minLength and length
   * @return Random hexadecimal string (lowercase)
   */
  def randomHexString(length: Int, isGloballyRandom: Boolean = false, minLengthMaybe: Option[Int] = None): String = {
    getRandomizer(isGloballyRandom).randomHexString(length, minLengthMaybe)
  }

  /**
   * Get random string of maximum length including allowable characters 8, 9, a, and b
   * @param isGloballyRandom Whether or not to use randomizer for globally unique values
   * @return Random hex char from 8 to b (lowercase)
   */
  def randomHexCharFrom8ToB(isGloballyRandom: Boolean = false): Char = {
    getRandomizer(isGloballyRandom).randomHexCharFrom8ToB
  }

  /**
   * Get a random int between two numbers, inclusive
   * @param min Minimum, inclusive
   * @param max Maximum, inclusive
   * @param isGloballyRandom Whether or not to use randomizer for globally unique values
   * @return Random int in range
   */
  def randomIntInRange(min: Int, max: Int, isGloballyRandom: Boolean = false): Int = {
    getRandomizer(isGloballyRandom).randomIntInRange(min, max)
  }

  /**
   * Generate a random string from allowable characters
   * @param length Length of string to generate; acts as max length if minLength is also specified
   * @param chars Allowable characters
   * @param isGloballyRandom Whether or not to use global randomizer
   * @param minLengthMaybe If specified, then length is randomized between minLength and length
   * @return Random string containing only the allowed characters
   */
  def randomStringFromAllowableChars(length: Int, chars: Seq[Char], isGloballyRandom: Boolean = false, minLengthMaybe: Option[Int] = None): String = {
    getRandomizer(isGloballyRandom).randomStringFromAllowableChars(length, chars, minLengthMaybe)
  }

  /**
   * Returns true (percentage * 100)% of the time, else false
   * @param percentage A number between 0 and 1.0 (or else, if below 0 it will always evaluate false, and >= 1 will always evaluate true)
   * @return Whether or not probability passed
   */
  def evaluateProbability(percentage: Double): Boolean = {
    getRandomizer(false).evaluateProbability(percentage)
  }

  /**
   * Get the next Long from the configured-seed randomizer for the current thread, with specified maximum size (exclusive).
   * @param maxSizeExclusive Max size (exclusive)
   * @return Random long in range [0, maxsize)
   */
  def nextLong(maxSizeExclusive: Long): Long = {
    getRandomizer(false).nextLong(maxSizeExclusive)
  }

  /**
   * Get the next Long from the configured-seed randomizer for the current thread, with specified minimum size (inclusive)
   * and specified maximum size (exclusive).
   * @param minSizeInclusive Min size (inclusive), >= 0
   * @param maxSizeExclusive Max size (exclusive)
   * @return Random long in range [min, max)
   */
  def nextLong(minSizeInclusive: Long, maxSizeExclusive: Long) : Long = {
    getRandomizer(false).nextLong(minSizeInclusive, maxSizeExclusive)
  }

  /**
   * Same behavior as java.util.UUID.randomUUID() except uses a random generator with configurable seed.
   * @param isGloballyRandom Whether or not to use the globally random seeded genernator or the regular one
   * @return Random UUID
   */
  def randomUuid(isGloballyRandom: Boolean = false): String = {
    getRandomizer(isGloballyRandom).randomUuid
  }

  /**
   * Gamma generators for main thread, keyed by random and globally random seeds.
   */
  private val gammaGenerators = new collection.mutable.HashMap[(Double, Double), GammaDistribution]()

  /**
   * Get the next integer from a gamma distribution of the specified shape and scale, with optional random seed.
   * Not yet thread-aware because so far we have only needed to use this from the main thread.
   * @param gammaDistShape Gamma distribution shape
   * @param gammaDistScale Gamma distribution scala
   * @param randomSeedOption A random seed to use, or None (default)
   * @return Next int from gamma distribution
   */
  def getNextIntFromGammaDistribution(gammaDistShape: Double, gammaDistScale: Double, randomSeedOption: Option[Long] = None): Int = {
    if (!gammaGenerators.contains((gammaDistShape, gammaDistScale))) {
      val localRandomSeed = if (randomSeedOption.nonEmpty) randomSeedOption.get else randomSeedRandomizer.nextLong()
      gammaGenerators.put((gammaDistShape, gammaDistScale), new GammaDistribution(
        new JDKRandomGenerator() { setSeed(localRandomSeed)}, gammaDistShape, gammaDistScale))
    }

    val gammaGenerator = gammaGenerators.get((gammaDistShape, gammaDistScale)).get
    val nextValInGamma = gammaGenerator.sample

    math.round(nextValInGamma).toInt
  }
}
