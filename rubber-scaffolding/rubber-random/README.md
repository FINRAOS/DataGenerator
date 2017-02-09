# Rubber Random - User Guide

Rubber Random is a component that aims to provide the capacity to generate specific types of pseudo random data. It is used by several of the Rubber Scaffolding components and may used directly as well. For most simple types there is support right out of the box. For more complex types as well as having the ability to further restrict and configure the generation consult the developer's guide.
  
## Quick use
After adding the Rubber Random dependency to your project you will need to initialize it.

In Java:
```java
import org.finra.datagenerator.scaffolding.random.core.JavaRubberRandom;

class JavaRandomTest {
    JavaRubberRandom random = JavaRubberRandom.apply();
       
    public void test() {
        // Generate a simple primitive.
        Long l = random.generate(Long.class);
        
        //Let's look at an example of how we generate data based on a custom [POJO](https://en.wikipedia.org/wiki/Plain_Old_Java_Object):
        // Generate a POJO
        Foo f = random.generate(Foo.class);
    }
}
```

In Scala:
```scala
import org.finra.datagenerator.scaffolding.random.core._

class ScalaRandomTest {
    val random = RubberRandomImpl()
    
    def test(): Unit = random.generate(classOf[Long])
}
```
Most the code here will be in Java except where there may be distinctions.

The value of `l` is `-4049405566182728712`. Here we generated the `Long` using the seed `196542L`. Now you have seen the library generate a primitive. By default Rubber Random comes with support for:
* Primitives and their corresponding wrapper type (Like `int` and `Integer`).
* Primitive and objects arrays (`int[]` or `Integer[]` or `Date[]`)
* [Collection types](src/main/scala/org/finra/datagenerator/scaffolding/random/types/CollectionTypes.scala) 
    * Unordered collections
    * Ordered collections
    * Maps
* Generic types ([Blah](src/test/java/org/finra/scaffolding/random/Blah.java))
* Recursive types ([OtherRecursive](src/test/java/org/finra/scaffolding/random/OtherRecursive.java)) 

If the type present does not have a known generator then the type is processed according to the `GeneratingStrategy`. By default, Rubber Random will use the field types of the given class and attempt to populate an instance of the class accordingly. 
 
Here is the output after running this five times (with a seed of `196542L`):
```json
[
    {"number":488834986,"label":"umsfEnYL78HSu6h"},
    {"number":-625595649,"label":"HaMRfLxsQ5amqQWoUx5X2sWtD7n8c"},
    {"number":-14705603,"label":"KbU8ySWFccimRnUTnvL3g"},
    {"number":545374295,"label":"VlbRp5AcRgTip6"},
    {"number":1379213898,"label":"QKY07wvD1F"}
]
```
You can see here th at the `number` field may range from any valid `Long`. The `String` type by default will only print alphanumeric characters.

Generating a string from a regex is also an important capability. Rubber Random has two different regex engines. Depending on the complexity of the regular expression and how many values you wish to generate you may wish to simplify your regular expression. As of now the simpler regex engine cannot handle look behind and look ahead expressions. The simpler engine is however much more efficient and able to generate data much quicker.
```java
@Test
public void regexTest() {
    JavaRubberRandom random = JavaRubberRandom.apply();
    RegexGenerator gen = random.generateRegex("[a-zA-Z0-9]{5,10}\\@[a-zA-Z0-9]{5,10}\\.[a-z]{3}");
    String r1 = gen.next();
    String r2 = gen.next();
    logger.info("Out: {}", r1);
    logger.info("Out: {}", r2);
}
```

When you generate a collection the number of results is configurable. By default there will be between 10 and 50 results generated. You can override the range if you want to adjust this number depending on your needs.

```java
@Test
public void adjustCollectionOutput() {
    JavaRubberRandom random = JavaRubberRandom.apply();
    ConfigName cn = CollectionTypes.CollectionRangeName$.MODULE$;
    scala.collection.immutable.Range range = scala.collection.immutable.Range$.MODULE$.inclusive(5, 5);
    random.setOverride(cn, range);
    List<Integer> out = random.generate(new ParameterizedTypeReference<List<Integer>>() {});
    Assert.assertEquals(5, out.size());
}
```