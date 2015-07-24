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
