package org.finra.datagenerator.scaffolding.context


import org.finra.datagenerator.scaffolding.utils.Logging

import scala.collection.JavaConverters._
/**
  * Created by dkopel on 12/14/16.
  */
trait GlobalsProvider extends Logging {
    private var globals: Map[String, Object] = Map.empty[String, Object]

    def setGlobal(key: String, value: Object) = globals = globals + (key->value)

    def setGlobals(values: Map[String, Object]) = globals ++= values

    def getGlobals: java.util.Map[String, Object] = globals.asJava

    def lookupGlobal(key: String): Object = globals(key)
}
