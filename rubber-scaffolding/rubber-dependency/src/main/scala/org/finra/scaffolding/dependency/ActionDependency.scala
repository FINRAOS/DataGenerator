package org.finra.scaffolding.dependency

/**
  * Created by dkopel on 9/20/16.
  */
trait ActionDependency extends Dependency with (() => Any)