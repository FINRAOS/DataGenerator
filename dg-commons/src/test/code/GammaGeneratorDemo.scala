import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.JDKRandomGenerator

/**
 * Quick and dirty demo of Gamma Generator
 */
object GammaGeneratorDemo extends App {
  var gammaGenerator = new GammaDistribution(new JDKRandomGenerator() { setSeed(65536)}, 3, 2.5)
  for (i <- 1 to 10000) {
    val nextNumberInGammaDistribution = gammaGenerator.sample
    println(nextNumberInGammaDistribution)
  }
}
