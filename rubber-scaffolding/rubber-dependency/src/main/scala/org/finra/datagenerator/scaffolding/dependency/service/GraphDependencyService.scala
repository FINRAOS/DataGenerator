package org.finra.datagenerator.scaffolding.dependency.service

import org.finra.datagenerator.scaffolding.dependency.Dependency
import org.finra.datagenerator.scaffolding.graph.GraphService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
  * Created by dkopel on 10/7/16.
  */
@Service
class GraphDependencyService @Autowired()(graphService: GraphService) extends DependencyService {

    override def resolve(dependencies: List[Dependency]): Unit = ???

    override def simplify(): Unit = ???

    override def checkForInfiniteLoops(): Unit = ???
}