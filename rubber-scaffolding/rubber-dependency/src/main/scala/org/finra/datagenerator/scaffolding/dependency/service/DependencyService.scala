package org.finra.datagenerator.scaffolding.dependency.service

import org.finra.datagenerator.scaffolding.dependency.Dependency

/**
  * Created by dkopel on 9/20/16.
  */
/*
Resolve dependencies by evaluating entities provided
dependencies.
 */
trait DependencyService {
    def resolve(dependencies: List[Dependency])

    def simplify()

    def checkForInfiniteLoops()
}
