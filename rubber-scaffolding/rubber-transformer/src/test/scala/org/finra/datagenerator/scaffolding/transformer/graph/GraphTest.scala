package org.finra.datagenerator.scaffolding.transformer.graph

import java.nio.file.Files

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
import org.finra.datagenerator.scaffolding.transformer.{Mailman, Person, Worker}
import org.finra.datagenerator.scaffolding.graph.{GraphAppConfiguration, GraphFactoryService}
import org.finra.datagenerator.scaffolding.spark.RDDService
import org.finra.datagenerator.scaffolding.transformer.graph.nodes.{EdgeContainer, ElementContainer, VertexContainer}
import org.finra.datagenerator.scaffolding.transformer.graph.reader.GraphMLUtils
import org.finra.datagenerator.scaffolding.transformer.graph.rules._
import org.finra.datagenerator.scaffolding.transformer.graph.traverser.BreadthFirstGraphTraverser
import org.junit.runner.RunWith
import org.junit.{After, Before, Test}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.core.io.ResourceLoader
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
/**
  * Created by dkopel on 10/14/16.
  */

@ComponentScan(Array("org.finra.datagenerator.scaffolding.transformer.*", "org.finra.datagenerator.scaffolding.random.*"))
@SpringBootConfiguration
class GraphTransformerConfiguration() extends GraphAppConfiguration {
    @Bean
    def graphUtils = GraphUtils
}

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@RunWith(classOf[SpringRunner])
@SpringBootTest(classes=Array(classOf[GraphTransformerConfiguration]))
class GraphTest {
    val logger = LoggerFactory.getLogger(getClass)
    @Autowired var graphUtils = GraphUtils
    @Autowired var graphFactoryService: GraphFactoryService = _
    @Autowired var resourceLoader: ResourceLoader = _
    @Autowired var rDDServiceImpl: RDDService = _

    @Before
    def before = System.gc()

    @After
    def after = {
        rDDServiceImpl.getSparkContext.stop()
    }

    @Test
    def testGraphBuilder {
        import GraphMLUtils.toMap
        val builder = GraphMLUtils.newGraph
        builder.globals(Map("a"->1, "b"->2))
        builder.node(1, classOf[Person], Map("name"->"tom", "age"->43))
        builder.node(2, classOf[Person], Map("name"->"jim", "age"->50))
        builder.edge(100, 1, 2, classOf[Mailman], Map("role"->"carrier"))

        val tn1 = builder.byId(1)
        require(toMap(tn1)("name") equals "tom")
        require(toMap(tn1)("age") equals "43")

        require(toMap(builder.nodeByProperty("name"->"tom").head)("age")=="43")
        require(toMap(builder.nodeByProperty("name"->"jim").head)("age")=="50")
        require(builder.edgeByProperty("role"->"carrier").head.\@("id")=="100")
    }

    trait Fruit
    trait Round
    trait HasSeeds
    trait HasStem

    class Apple extends Fruit with Round with HasSeeds with HasStem {
        var name: String = _
        var color: String = _
        var weight: Int = _
    }

    def randomPerson = Map("name"->RandomStringUtils.randomAscii(10), "age"->RandomUtils.nextInt(1, 100))


    @Test
    def testTraverser() {
        logger.debug("Beginning test traverser")

        val builder = GraphMLUtils.newGraph
        builder.globals(Map("a"->1, "b"->2))
        builder.node(1, classOf[Person], randomPerson)
        builder.node(2, classOf[Person], randomPerson)
        builder.node(3, classOf[Person], randomPerson)
        builder.node(4, classOf[Person], randomPerson)
        builder.node(5, classOf[Person], randomPerson)
        builder.node(6, classOf[Person], randomPerson)
        builder.edge(100, 1, 2, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(101, 2, 3, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(102, 3, 4, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(103, 1, 5, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(104, 2, 6, classOf[Mailman], Map("role"->"carrier"))

        logger.debug("Loading graph")
        val reader = GraphUtils.loadGraph(builder.graph)
        implicit val graph = reader.graph
        val traverser = BreadthFirstGraphTraverser()

        require(reader.globals("a") == "1")

        val queue = collection.mutable.Queue(1, 100, 103, 2, 5, 101, 104, 3, 6, 102, 4)

        while(traverser.hasNext) {
            val id = traverser.next.id()
            val tid = queue.dequeue

            logger.info("Id {}, Ordered Id {}", id, tid)
            require(id.toString equals tid.toString)
        }
    }

    @Test
    def testSimpleRead: Unit = {
        val f = "really_simple.graphml"
        val input = scala.io.Source.fromInputStream(resourceLoader.getResource(s"classpath:$f").getInputStream)
        val output = Files.createTempFile("", f)
        Files.write(output.toFile.toPath, input.mkString.getBytes)
        val g = graphFactoryService.existingGraph(f).graph
        println("Vertices : "+g.V.count().head())
    }

    @Test
    def testSimple {
        logger.info("Test simple");
        val simple = (
        <graphml>
            <graph>
                <node id="1">
                    <data key="_clazz">org.finra.datagenerator.scaffolding.transformer.Person</data>
                    <data key="age">30</data>
                    <data key="name">Steve</data>
                    // No gender set
                </node>
                <node id="2">
                    <data key="_clazz">org.finra.datagenerator.scaffolding.transformer.Person</data>
                    <data key="name">Tom</data>
                    // No age or gender set
                </node>
                <node id="3">
                    <data key="_clazz">org.finra.datagenerator.scaffolding.transformer.Person</data>
                    <data key="name">John</data>
                    // No age or gender set
                </node>
                <edge id="99" source="1" target="2">
                    <data key="_clazz">org.finra.datagenerator.scaffolding.transformer.FriendsWith</data>
                </edge>
                <edge id="100" source="1" target="3">
                    <data key="_clazz">org.finra.datagenerator.scaffolding.transformer.FriendsWith</data>
                </edge>
            </graph>
        </graphml>
            )
        val graphReader = graphUtils.loadGraph(simple)
        val generator = graphUtils.generateScenario(graphReader)

        generator.rules.append(VertexTransformerRule(
            VertexRuleCondition(classOf[Person], (t: VertexContainer[_]) => t.properties("name").equals("Tom")),
            FieldRuleTarget(classOf[Person], "age"),
            (t: VertexContainer[_]) => 50
        ))

        val out = generator.generate()
        out.vertices.foreach(v => println(v.id()+": "+toMap(v).filterKeys(k => !ElementContainer.IGNORE.contains(k))))

        val v1 = out.graph.V(1).head()
        // Overrides worked
        require("Steve" equals v1.property("name").value())
        require(30 equals v1.property("age").value())

        val v2 = out.graph.V(2).head()
        require("Tom" equals v2.property("name").value())
        // Set via a rule
        require(50 equals v2 .property("age").value())

        val v3 = out.graph.V(3).head()
        require("John" equals v3.property("name").value())
    }

    @Test
    def testNodeRepo {
        logger.info("Test Node Repo")
        val builder = GraphMLUtils.newGraph
        builder.node(1, classOf[Person], randomPerson)
        builder.node(2, classOf[Person], randomPerson)
        builder.node(3, classOf[Person], randomPerson)
        builder.node(4, classOf[Person], randomPerson)
        builder.node(5, classOf[Person], randomPerson)
        builder.node(6, classOf[Person], randomPerson)
        builder.edge(100, 1, 2, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(101, 2, 3, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(102, 3, 4, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(103, 1, 5, classOf[Mailman], Map("role"->"carrier"))
        builder.edge(104, 2, 6, classOf[Mailman], Map("role"->"carrier"))

        val reader = GraphUtils.loadGraph(builder.graph)
        implicit val graph = reader.graph

        logger.info("Number of nodes {}", reader.nodeRepository.nodes.size)

        require(reader.nodeRepository.vertices.size == 6)
        require(reader.nodeRepository.edges.size == 5)
        require(reader.nodeRepository.nodes.size == 11)
    }

    @Test
    def simpleTest {
        val graphReader = graphUtils.loadGraphResource("classpath:simple.graphml")
        val generator = graphUtils.generateScenario(graphReader)
        val objectMapper = new ObjectMapper();



        val r1 = VertexTransformerRule(
            // Class or predicate
            VertexRuleCondition(classOf[Mailman]),
            // Target
            FieldRuleTarget(classOf[Mailman], "route"),
            // Action or output
            (t: VertexContainer[_]) => "SW"
        )

        generator.rules.append(r1)


        generator.rules.append(EdgeTransformerRule(
            EdgeRuleCondition(classOf[Person]),
            FieldRuleTarget(classOf[Person], "name"),
            (t: EdgeContainer[_]) => "SW"
        ))

        generator.rules.append(VertexTransformerRule(
            VertexRuleCondition[Worker](classOf[Worker], v => v.properties("name").toString == "lop"),
            FieldRuleTarget(classOf[Worker], "age"),
            (t: VertexContainer[_]) => 1000
        ))

//        graphReader.globals.foreach(t => println(s"${t._1}: ${t._2}"))

//        GraphUtils.orderByOuts.foreach(println)


//        val start = GraphUtils.findStartingVertex
//        val sv = g.V(start.id)
//
//        println(start.id)

        val out = generator.generate()
        System.out.println("Vertices rules: "+generator.vertexRules.length);
        println(out)
        out.vertices.foreach(v => println(toMap(v).filterKeys(k => !ElementContainer.IGNORE.contains(k))))
    }
}
