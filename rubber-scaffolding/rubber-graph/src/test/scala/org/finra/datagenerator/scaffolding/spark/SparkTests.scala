package org.finra.datagenerator.scaffolding.spark

import java.io.Serializable
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.finra.datagenerator.scaffolding.graph.GraphAppConfiguration
import org.junit._
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.FileSystemUtils

import scala.collection.mutable.ListBuffer
import scala.collection.{Seq, mutable};

/**
 * Created by dkopel on 9/1/16.
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(classOf[SpringRunner])
@SpringBootTest(classes=Array(classOf[GraphAppConfiguration]))
@FixMethodOrder(value= MethodSorters.NAME_ASCENDING)
class SparkTests extends Serializable {
    lazy val logger: Logger = LoggerFactory.getLogger(getClass())
    @transient private var sc: SparkContext = _
    private lazy val cacheName: String = "cache"
    private final lazy val tmpFile: String = "/tmp/numbers";
    private var rddService: RDDService = _;

    @Autowired
    def setRddService(rddService: RDDService) {
        this.rddService = rddService;
    }

    @Before
    def before() {
        rddService.start();
        sc = rddService.getSparkContext;
        sc.getConf.registerKryoClasses(Array(classOf[NumberComparison]))
    }

    @After
    def after() {
        sc.stop();
    }

    class NumberComparison(val operator: Char, val num: Int) extends ((Int)=>Boolean) {
        override def apply(integer: Int): Boolean = {
            operator match {
                case '<' => num < integer
                case '>' => num > integer
                case '=' => num == integer
                case _ => false
            }
        }

        def canEqual(other: Any): Boolean = other.isInstanceOf[NumberComparison]

        override def equals(other: Any): Boolean = other match {
            case that: NumberComparison =>
                (that canEqual this) &&
                    operator == that.operator &&
                    num == that.num
            case _ => false
        }

        override def hashCode(): Int = {
            val state = Seq(operator, num)
            state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
        }

        override def toString = s"NumberComparison($operator, $num)"
    }

    @Test
    @Ignore
    def test1() = {
        // Delete tmp file
        FileSystemUtils.deleteRecursively(Paths.get(tmpFile).toFile())

        var predicates: collection.immutable.List[NumberComparison] = collection.immutable.List.empty[NumberComparison]
        var x: AtomicInteger = new AtomicInteger(-10);
        while(x.get() < 0) {
            var y: Int = x.getAndIncrement();
            predicates = new NumberComparison('<', y) :: predicates
        }

        var rdd1: RDD[NumberComparison] = rddService.parallelize(predicates)
        rdd1.cache()
        logger.info("There are {} predicates", rdd1.count);
        predicates = collection.immutable.List.empty[NumberComparison]

        x.set(10)
        while(x.get() < 20) {
            var y: Int = x.getAndIncrement()
            predicates = new NumberComparison('>', y) :: predicates
        }

        var rdd2: RDD[NumberComparison] = rdd1.union(rddService.parallelize(predicates))
        rdd2.cache()

        Assert.assertEquals(20, rdd2.count())

        rdd2.collect().foreach(println)

        var filtered: RDD[NumberComparison] = rdd2.filter(nc => nc.apply(10))
        filtered.cache
        Assert.assertEquals(19, filtered.count());

        var notTrue: RDD[NumberComparison] = rdd2.filter(nc => !nc.apply(10))
        Assert.assertEquals(1, notTrue.count())

        Assert.assertEquals(List(4, 5), List(1,2,3,4,5).filterNot(i => List(1,2,3).contains(i)))

        var notFiltered: RDD[NumberComparison] = rdd2.subtract(filtered);
        notFiltered.cache
        notFiltered.collect().foreach(u => println("Not filtered: "+u+ " "+u.apply(10)))

        Assert.assertEquals(2, notFiltered.count());
        Assert.assertEquals(18, filtered.subtract(notFiltered).count());

        filtered.collect().foreach(i => {
            logger.info("Found predicate directly on spark with number {} and operator {}", i.num, i.operator);
        });
        rdd2.setName(cacheName);
        //rdd2.persist(StorageLevel.MEMORY_AND_DISK());
        rdd2.saveAsObjectFile(tmpFile);
    }

    @Test
    @Ignore
    def test2() {
        var loadedRdd: RDD[NumberComparison] = sc.objectFile(tmpFile);
        loadedRdd.setName(cacheName);
        loadedRdd.persist(StorageLevel.MEMORY_AND_DISK);

        sc.getPersistentRDDs.foreach(r => logger.info("Found RDD with name {}", r._2.name));

        logger.info("Loaded rdd from disk with {} items", loadedRdd.count());

        var filtered1: RDD[NumberComparison] = loadedRdd.filter((nc) => nc.apply(10));
        filtered1.collect().foreach(i => {
            logger.info("Found predicate directly on spark with number {} and operator {}", i.num, i.operator);
        });
    }

    @Test
    @Ignore
    def test3() {
        var map: mutable.Map[UUID, Integer] = new mutable.HashMap();
        var nums: Seq[(UUID, Int)] = new ListBuffer[(UUID, Int)];
        for(x <- Range.apply(0, 20)) {
            val id: UUID = UUID.randomUUID();
            nums :+ ((id, x));
            map.put(id, x);
        }
        var pairs: RDD[(UUID, Int)]  = sc.parallelize(nums);
        map.foreach(e => Assert.assertEquals(e._2, getValue(pairs, e._1)));
        map.foreach(e => Assert.assertTrue(pairs.lookup(e._1).contains(e._2)));
    }

    def getValue(pairs: RDD[(UUID, Int)] , key: UUID): Integer = {
        return pairs.filter((t) => t._1.equals(key)).values.first();
    }
}
