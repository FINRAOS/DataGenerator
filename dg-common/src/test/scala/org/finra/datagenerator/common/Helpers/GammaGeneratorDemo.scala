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

/**
 * Quick and dirty demo of Gamma Generator
 */
object GammaGeneratorDemo extends App {
  var gammaGenerator = new GammaDistribution(new JDKRandomGenerator() { setSeed(65536)}, 3, 2.5)
  for (i <- 1 to 10000) {
    val nextNumberInGammaDistribution = gammaGenerator.sample
    println(nextNumberInGammaDistribution) // scalastyle:ignore
  }
}
