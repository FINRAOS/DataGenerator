/*
 * Copyright 2015 DataGenerator Contributors
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

package Helpers

import Helpers.StringHelper._

// Too lazy for now to make this a real unit test... isn't that awful. :O

/**
 * Quick and dirty demo of custom string splitting methods
 */
object StringHelperDemo extends App {
  "Hi!".splitOnChar(',').foreach(x => println(s"`$x`"))
  println()
  "       Hello     \t\n      world!".splitOnWhitespace.foreach(x => println(s"`$x`"))
  println()
  "       Hello           world!            ".splitOnWhitespace.foreach(x => println(s"`$x`"))
  println()
  "Hello           world!           \r".splitOnWhitespace.foreach(x => println(s"`$x`"))
  println()
  println()
  "Hello world ".splitOnChar(' ', removeEmptyEntries=true).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string***".splitOnString("***", removeEmptyEntries=true).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********".splitOnString("***", removeEmptyEntries=true).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********!!!".splitOnString("***", removeEmptyEntries=true).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string***********!!!".splitOnString("***", removeEmptyEntries=true).foreach(x => println(s"`$x`"))
  println()
  println()
  "Hello world ".splitOnChar(' ', removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string***".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
  println()
  "***Hello***world***this***is***my***string*********!!!".splitOnString("***", removeEmptyEntries=false).foreach(x => println(s"`$x`"))
}
