import org.lanyard.dist.cont.Gamma
import org.lanyard.random.{RNG, Ranq1}

/**
 * Quick and dirty demo of Gamma Generator
 */
object GammaGeneratorDemo extends App {
  var gammaGenerator = Gamma(3, 2.5)
  var randomGenerator: RNG = Ranq1(67576)
  for (i <- 1 to 10000) {
    val tuple = gammaGenerator.random(randomGenerator)
    val nextNumberInGammaDistribution = tuple._1
    randomGenerator = tuple._2
    println(nextNumberInGammaDistribution)
  }
}
