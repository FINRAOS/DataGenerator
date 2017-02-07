package org.finra.datagenerator.scaffolding.dependency

/**
  * Created by dkopel on 9/20/16.
  */
trait ActionDependency extends Dependency with (() => Any)