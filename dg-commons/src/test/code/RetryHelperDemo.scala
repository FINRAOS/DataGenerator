import Helpers.{RandomHelper, RetryHelper}

/**
 * Quick and dirty demo of RetryHelper
 */
object RetryHelperDemo extends App {

  RetryHelper.retry(200, Seq(classOf[IllegalArgumentException]))()({
    val prob = RandomHelper.evaluateProbability(0.9)
    println(s"Prob is: $prob")
    if (prob) {
      throw new IllegalArgumentException() // NullPointerException() will cause it to fail.
    }
  })
}
