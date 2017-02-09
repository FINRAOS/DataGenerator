package org.finra.datagenerator.scaffolding.spark

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.Map

/**
 * Created by dkopel on 9/1/16.
 */

trait RDDService {
    def getSparkContext: SparkContext;
    def start()
    def stop()
    def without[K, V] (original: RDD[(K, V)], remove: K): RDD[(K, V)]
    def parallelize[T] (list : Seq[T])(implicit evidence$1 : scala.reflect.ClassTag[T]): RDD[T]
    def parallelize[T] (list : java.util.List[T])(implicit evidence$1 : scala.reflect.ClassTag[T]): RDD[T]
    def parallelize[K, V] (map : Map[K, V]): RDD[(K, V)]
    def parallelize[K, V] (map : java.util.Map[K, V]): RDD[(K, V)]
}