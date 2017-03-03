package org.finra.datagenerator.scaffolding.dependency.service

import gremlin.scala._
import jdk.nashorn.internal.ir.annotations.Ignore
import junit.framework.TestCase
import org.apache.spark.SparkContext
import org.finra.datagenerator.scaffolding.dependency.Domain
import org.finra.datagenerator.scaffolding.graph.GraphService
import org.finra.datagenerator.scaffolding.spark.RDDService
import org.junit._
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
  * Created by dkopel on 10/7/16.
  */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[org.finra.datagenerator.scaffolding.graph.GraphAppConfiguration]))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FixMethodOrder(value= MethodSorters.NAME_ASCENDING)
class DependencyTest extends TestCase {
    lazy val logger: Logger = LoggerFactory.getLogger(getClass)
    @transient private var sc: SparkContext = _
    @Autowired var graphService: GraphService = _
    @Autowired var dependencyService: DependencyService = _
    private var rddService: RDDService = _;

    @Autowired
    def setRddService(rddService: RDDService) {
        this.rddService = rddService;
    }

    @Before
    def before() {
        dependencyService
        rddService.start();
        sc = rddService.getSparkContext;
    }

    @After
    def after() {
        sc.stop()
    }

    @Ignore
    @Test def test1() {
        graphService.graph.addVertex("test")
            .property("fruit", "apple")
        val t = graphService.graph.asJava.traversal()
            .V().hasLabel("test").V().next()
        Assert.assertEquals("apple", t.property("fruit").value())
    }

    @Ignore
    @Test def test2(): Unit = {
        val graph = graphService.graph

        val Founded = Key[String]("founded")
        val Distance = Key[Int]("distance")

        // create labelled vertex
        val paris = graph + "Paris"

        // create vertex with typed properties
        val london = graph + ("London", Founded -> "43 AD")

        // create labelled edges
        paris --- "OneWayRoad" --> london
        paris <-- "OtherWayAround" --- london
        paris <-- "Eurostar" --> london

        // create edge with typed properties
        paris --- ("Eurostar", Distance -> 495) --> london

        // type safe access to properties
        paris.out("Eurostar").value(Founded).head //43 AD
        paris.outE("Eurostar").value(Distance).head //495
        london.valueOption(Founded) //Some(43 AD)
        london.valueOption(Distance) //None
        paris.setProperty(Founded, "300 BC")

        val Name = Key[String]("name")
        val Age = Key[Int]("age")

        val v1 = graph + ("person", Name -> "marko", Age -> 29) asScala

        v1.keys // Set(Key("name"), Key("age"))
        v1.property(Name) // "marko"
        v1.valueMap // Map("name" → "marko", "age" → 29)
        v1.valueMap("name", "age") // Map("name" → "marko", "age" → 29)

        logger.info("Vertex keys {}", v1.keys)
        logger.info("Vertex properties {}", v1.valueMap)
        graph.graph.io(IoCore.graphml()).writeGraph("graph.xml");
    }

    @Ignore
    def test3: Unit = {
        val graph = graphService.graph
        val gDomain = Domain(label="graphs")

        // Instance of `A` should start a graph
        // Instance of `B` should be anywhere in the graph
        // Instance of `C` should
        // Instance of `Z` must be the end
    }
}
