package org.finra.datagenerator.common.Helpers

/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.finra.datagenerator.common.Helpers.ReflectionHelper._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.finra.datagenerator.common.Helpers.StringHelper.StringImplicits

/**
 * Unit tests for RetryHelper
 */
@RunWith(classOf[JUnitRunner])
class ReflectionHelperTests extends WordSpec {
  "Calling a Scala setter with a string" when {
    "the setter's type is Option[Int]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.intMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("intMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.intMaybe.nonEmpty && reflectionTestObject.intMaybe.get == 3)
      }
    }
    "the setter's type is Option[Long]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.longMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("longMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.longMaybe.nonEmpty && reflectionTestObject.longMaybe.get == 3L)
      }
    }
    "the setter's type is Option[Char]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.charMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("charMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.charMaybe.nonEmpty && reflectionTestObject.charMaybe.get == '3')
      }
    }
    "the setter's type is Option[Double]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.doubleMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("doubleMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.doubleMaybe.nonEmpty && reflectionTestObject.doubleMaybe.get == 3.0)
      }
    }
    "the setter's type is Option[Boolean]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.booleanMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("booleanMaybe", "true", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.booleanMaybe.nonEmpty && reflectionTestObject.booleanMaybe.get == true)
      }
    }
    "the setter's type is Option[Byte]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.byteMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("byteMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.byteMaybe.nonEmpty && reflectionTestObject.byteMaybe.get == 3.toByte)
      }
    }
    "the setter's type is Option[String]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.stringMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("stringMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.stringMaybe.nonEmpty && reflectionTestObject.stringMaybe.get == "3")
      }
    }
    "the setter's type is Option[Float]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.floatMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("floatMaybe", "3", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.floatMaybe.nonEmpty && reflectionTestObject.floatMaybe.get == 3.toFloat)
      }
    }
    "the setter's type is Option[java.util.Date]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.dateMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("dateMaybe", "20080405133000", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.dateMaybe.nonEmpty && reflectionTestObject.dateMaybe.get == "20080405133000".toDateTime)
      }
    }
    "the setter's type is Option[java.sql.Date]" should {
      "find the setter and convert the string value, and the current value should change." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.sqlDateMaybe.isEmpty)

        reflectionTestObject.invokeScalaSetter("sqlDateMaybe", "20080405", skipIfNotExists=false, caseInsensitive=true, forceTypeCoercion=true)
        assert(reflectionTestObject.sqlDateMaybe.nonEmpty && reflectionTestObject.sqlDateMaybe.get == "20080405".toDate)
      }
    }
  }
  "Calling a Scala getter" when {
    "the getter's type is Option[Int]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.intMaybe.isEmpty)
        reflectionTestObject.intMaybe = Some(3)
        val intMaybe: Option[Int] = reflectionTestObject.invokeScalaGetter("intMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Int]]
        assert(intMaybe.nonEmpty && intMaybe.get == 3)
      }
    }
    "the getter's type is Option[Long]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.longMaybe.isEmpty)
        reflectionTestObject.longMaybe = Some(3L)
        val longMaybe: Option[Long] = reflectionTestObject.invokeScalaGetter("longMaybe"
          ,skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Long]]
        assert(longMaybe.nonEmpty && longMaybe.get == 3L)
      }
    }
    "the getter's type is Option[Char]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.charMaybe.isEmpty)
        reflectionTestObject.charMaybe = Some('3')
        val charMaybe: Option[Char] = reflectionTestObject.invokeScalaGetter("charMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Char]]
        assert(charMaybe.nonEmpty && charMaybe.get == '3')
      }
    }
    "the getter's type is Option[Double]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.doubleMaybe.isEmpty)
        reflectionTestObject.doubleMaybe = Some(3.toDouble)
        val doubleMaybe: Option[Double] = reflectionTestObject.invokeScalaGetter("doubleMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Double]]
        assert(doubleMaybe.nonEmpty && doubleMaybe.get == 3.0)
      }
    }
    "the getter's type is Option[Boolean]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.booleanMaybe.isEmpty)
        reflectionTestObject.booleanMaybe = Some(true)
        val booleanMaybe: Option[Boolean] = reflectionTestObject.invokeScalaGetter("booleanMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Boolean]]
        assert(booleanMaybe.nonEmpty && booleanMaybe.get == true)
      }
    }
    "the getter's type is Option[Byte]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.byteMaybe.isEmpty)
        reflectionTestObject.byteMaybe = Some(3.toByte)
        val byteMaybe: Option[Byte] = reflectionTestObject.invokeScalaGetter("byteMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Byte]]
        assert(byteMaybe.nonEmpty && byteMaybe.get == 3.toByte)
      }
    }
    "the getter's type is Option[String]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.stringMaybe.isEmpty)
        reflectionTestObject.stringMaybe = Some("3")
        val stringMaybe: Option[String] = reflectionTestObject.invokeScalaGetter("stringMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[String]]
        assert(stringMaybe.nonEmpty && stringMaybe.get == "3")
      }
    }
    "the getter's type is Option[Float]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.floatMaybe.isEmpty)
        reflectionTestObject.floatMaybe = Some(3.toFloat)
        val floatMaybe: Option[Float] = reflectionTestObject.invokeScalaGetter("floatMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[Float]]
        assert(floatMaybe.nonEmpty && floatMaybe.get == 3.toFloat)
      }
    }
    "the getter's type is Option[java.util.Date]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.dateMaybe.isEmpty)
        reflectionTestObject.dateMaybe = Some("20140508133000".toDateTime)
        val dateMaybe: Option[java.util.Date] = reflectionTestObject.invokeScalaGetter("dateMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[java.util.Date]]
        assert(dateMaybe.nonEmpty && dateMaybe.get == "20140508133000".toDateTime)
      }
    }
    "the getter's type is Option[java.sql.Date]" should {
      "find the getter and return the current value." in {
        val reflectionTestObject = new ReflectionTestObject
        assert(reflectionTestObject.dateMaybe.isEmpty)
        reflectionTestObject.dateMaybe = Some("20140508".toDate)
        val dateMaybe: Option[java.sql.Date] = reflectionTestObject.invokeScalaGetter("dateMaybe"
          , skipIfNotExists=false, caseInsensitive=true).asInstanceOf[Option[java.sql.Date]]
        assert(dateMaybe.nonEmpty && dateMaybe.get == "20140508".toDate)
      }
    }
  }
}
