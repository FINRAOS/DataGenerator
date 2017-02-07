package org.finra.datagenerator.scaffolding.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.finra.datagenerator.scaffolding.messaging.MessageHandler
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._


/**
  * Created by dkopel on 9/1/16.
  */
@Service
class RDDServiceImpl @Autowired() (private var conf: SparkConf) extends RDDService {
    @transient private val logger: Logger = LoggerFactory.getLogger(getClass)
    @transient private var sc: SparkContext = SparkContext.getOrCreate(conf)

    override def start() {
        if (sc == null || sc.isStopped) {
            conf.registerKryoClasses(Array(classOf[MessageHandler]))
            sc = SparkContext.getOrCreate(conf)
        }
    }

    override def stop() = sc.stop

    override def getSparkContext(): SparkContext = sc

    override def without[K, V](original: RDD[(K, V)], remove: K): RDD[(K, V)] = {
        original.filter(t => !t._1.equals(remove)).persist()
    }

    override def parallelize[T](list: Seq[T])(implicit evidence$1 : scala.reflect.ClassTag[T]): RDD[T] = {
        sc.parallelize(list);
    }

    override def parallelize[T](list: java.util.List[T])(implicit evidence$1 : scala.reflect.ClassTag[T]): RDD[T] = {
        parallelize(list.asScala);
    }

    override def parallelize[K, V](m: scala.collection.Map[K, V]): RDD[(K, V)] = {
        sc.parallelize(m.map(e => (e._1, e._2)).toList);
    }

    override def parallelize[K, V](map: java.util.Map[K, V]): RDD[(K, V)] = parallelize(map.asScala)
}
