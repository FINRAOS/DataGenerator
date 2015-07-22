package Helpers;

import scala.collection.JavaConverters;

import java.util.LinkedList;

/**
 * Helpers to use Scala code in Java.
 */
public class ScalaInJavaHelper {
    /**
     * Convert a Java LinkedList to a Scala Iterable.
     * @param linkedList Java LinkedList to convert
     * @return Scala Iterable
     */
    public static scala.collection.Iterable linkedListToScalaIterable(LinkedList<?> linkedList) {
        return JavaConverters.asScalaIterableConverter(linkedList).asScala();
    }
}
