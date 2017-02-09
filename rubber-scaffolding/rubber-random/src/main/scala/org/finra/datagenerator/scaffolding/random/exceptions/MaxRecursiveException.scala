package org.finra.datagenerator.scaffolding.random.exceptions

/**
  * Created by dkopel on 12/8/16.
  */
case class MaxRecursiveException(count: Int) extends IllegalArgumentException {
    override def getMessage: String = s"Exceeding defined limit of $count recursions"
}
