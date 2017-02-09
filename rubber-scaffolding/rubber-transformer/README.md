# Rubber Transformer


## Introduction
The purpose of this tool is for facilitate simple linear transformation. 
That means transformations that do not have complex dependencies. This tool is very straightforward and does its job well.

A transformation consists of inputs and outputs. The inputs are provided to the service so that they may directly influence the transformation of the outputs.

The simplest way to invoke a transformation is with this method:

```java
List<TransformationContainer> transform(List<TransformationContainer> containers);
```

There are two types of [`TransformationContainer`](src/main/java/org/finra/scaffolding/transformer/service/TransformationContainer.java)'s, the [`InputTransformationContainer`](src/main/java/org/finra/scaffolding/transformer/service/InputTransformationContainer.java) and the [`OutputTransformationContainer`](src/main/java/org/finra/scaffolding/transformer/service/OutputTransformationContainer.java). The container is used to provide an alias to the inputs and outputs. An alias is a simple way that you may reference defined data.

Let's create a few simple classes and use them to illustrate what this library may provide.

* [BigClass](src/test/java/org/finra/scaffolding/transformer/BigClass.java)
* [GreatClass](src/test/java/org/finra/scaffolding/transformer/GreatClass.java)
* [SmallClass](src/test/java/org/finra/scaffolding/transformer/SmallClass.java)
* [AnotherClass](src/test/java/org/finra/scaffolding/transformer/AnotherClass.java)

Now we are going to define some variables that will be used in the examples:
```java
public class TestClass {
    // ...
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RandomProvider random = new RubberRandom();
    private final Transformer t = new Transformer(random, objectMapper);
    private final MultiTransformerService mt = new MultiTransformer(random, objectMapper, t);   
    // ...
}
```

## Your first transformation

Now let's create a random instance of `BigClass` and of `GreatClass`.
```java
public class TestClass {
    // ...
    final BigClass bg = random.nextObject(BigClass.class);
    final GreatClass gc = random.nextObject(GreatClass.class);
    // ...
}

```
If you serialize those instances you may see values similar to these:
BigClass:
```json
{"id":-552742142,"name":"JWAcPfopXdDxuiYGmEswaPoSzHkS","num":-2374009424711251484}
```

GreatClass:
```json
 {"time":188,"amt":203.0}
```
Now with those as our input lets perform a simple transformation using this code:
```java
// ...
List<TransformationContainer> outputs = t.transform(
        Lists.<TranformationContainer>newArrayList(
            new InputTransformationContainer("big", bg),
            new InputTransformationContainer("great", gc),
            new OutputTransformationContainer("sc", SmallClass.class),
            new OutputTransformationContainer("ac", AnotherClass.class)
        )
 );
// ...
```

You can see that we are using the [`InputTransformationContainer`](src/main/java/org/finra/scaffolding/transformer/service/InputTransformationContainer.java) for the instances of `BigClass` as well as `GreatClass`. We are using the [`OutputTransformationContainer`](src/main/java/org/finra/scaffolding/transformer/service/OutputTransformationContainer.java) for `SmallClass` and `AnotherClass`. This means that we are passing in two objects as out inputs and expecting to create corresponding instances one for each of the outputs.
 
Let's look at the output for the two outputs (using the inputs listed above):

SmallClass:
```json
 {"fruit":"-552742142SPICY-2374009424711251484","tomorrow":288,"weather":["SUNNY","HOT"],"mood":"HAPPY","goOnTrip":true,"thisWillBeNull":null,"id":-1700263452,"seq":null}
``` 

AnotherClass:
```json
{"name":"JWAcPfopXdDxuiYGmEswaPoSzHkS","secretName":"JWAcPfopXdDxuiYGmEswaPoSzHkS-2374009424711251484","seq":-9038127419389782366,"prevName":"OsYcJtLkZZKwCFMtuJB"}
```
Now let's open up the source code for `SmallClass` and walk through some of what is going on. Not everything will be explained right away.
```java
public class TestClass {
    // ...
    @Order(4)
    @Transformation("#big.id+'SPICY'+#big.num")
    private String fruit;
    // ...
}
```    
What this means is take the value of the variable with the alias "big" and the property "id" which in out case is "-552742142". Append the text "SPICY". Now append the value "num" from the variable with the "big" alias; "-2374009424711251484". If you look at our output it correctly reflects this value.

The language used in the [`@Transformation`](src/main/java/org/finra/datagenerator/scaffolding/transformer/support/Transformation.java) annotation is called [SpEL](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html) and is an expression language that is provided by the Spring Framework. You will want to familiarize yourself with the syntax since SpEL is used in a number of cases throughout this library.

Let's look at the next two fields:

```java
public class TestClass {
    // ...
    @Order(0)
    @Transformation("#great.time+100")
    private Long tomorrow;
    
    @Order(1)
    @Transformation(condition = "tomorrow > 200", value="{'SUNNY', 'HOT'}", order=0)
    @Transformation(condition = "tomorrow < 150 && tomorrow > 50", value="{'COLD', 'RAIN'}", order=1)
    @Transformation(value="{'CLOUDY'}", order=2)
    private Weather[] weather;
    // ...
}
```

### Order
For the `tomorrow` field we see that there is an `@Order(0)` annotation, and for the `weather` field we see that there is an `@Order(1)` annotation. When the system is evaluating the [`@Transformations`](src/main/java/org/finra/datagenerator/scaffolding/transformer/support/Transformations.java) for a given class it sorts the fields beginning with the lowest order ending with the highest order. Fields that have the `@Order` annotation omitted are all given the value `Long.MAX_VALUE`, which will make them have the lost sort priority

### Conditions
The other thing to note is the `condition` attribute on the `weather` field. Only one [`@Transformation`](src/main/java/org/finra/datagenerator/scaffolding/transformer/support/Transformations.java) will be executed on a given object instance. The transformations are evaluated in order in which they are defined. A transformation that has no `condition` is equivalent to a `true` condition. In the unit tests there different scenarios so that each one of the transformations may be used for the `weather` field. The condition uses the same SpEL syntax as mentioned earlier. 

### NULL
If you want a value to be NULL and not randomly generated based on its type, then you can use the `isNull` attribute:
```java
public class TestClass {
    // ...
    @Transformation(isNull = true)
    private String thisWillBeNull;
    // ...
}
```

### Empty String
If you want a value to be an empty string and not randomly generated you can use the `emptyString` attribute:
```java
public class TestClass {
    // ...
    @Transformation(emptyString = true, condition = "#iteration % 2 == 0")
    private String thisWillBeEmpty;
    // ...
}

```

### Limitations
If you wish to restrict certain types of data from being assigned you may choose to use a limitation. As of now a limitation does not change the data that is being generated, it simply will throw an exception if the limit is breached. This was not created for the purpose of validation, it is more or less used to verify inputted data is always true to the limits.

### Custom Context Method
There may be times where you wish to eliminate boilerplate code. The expression context has the capacity to register static methods to be used in conditions, values and the like. You must pass in the alias you will use to access the method and the [`Method`](https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html). Here is a simple example:
 
 ```java
 // ...
 Transformer t = new Transformer();
 // ...
 try {
     t.registerContextFunction("asList", Arrays.class.getDeclaredMethod("asList", new Class[] { Object[].class }));
 } catch (NoSuchMethodException e) {
     e.printStackTrace();
 }
 // ...
 ```
 
 Here we are registering the `asList` that comes from the Java `Arrays.class`. It's a simple method that can be used to convert a Java array into a `List<?>`. It takes a varargs argument which is represented as the `Object[].class` class. You must use the [`Class.getDeclaredMethod()`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredMethod-java.lang.String-java.lang.Class...-) to obtain the `Method` neccessary. The first parameter is the name of the method that belongs to the class. The second parameter is a `Class[]` (Class array) representing the parameter types in order from left to right. Order matters.
 

A limitations is a simple interface that can be implemented. Right now there is one limit, the [`MaxLength`](src/main/java/org/finra/scaffolding/transformer/limitation/MaxLength.java) limitation.
Take a look at the `InvalidClass` object and the `testValidation()` and `testValidation2()` tests for more details.

## Multi-Transformation
A multi-transformation is made to facilitate the creation of several iterations of the same data structures.
```java
// ...
Map m = new HashMap();

LongStream.range(0, 10L).forEach(i -> {
    BigClass bg = random.nextObject(BigClass.class);
    GreatClass gc1 = new GreatClass(-200L, 10D);

    m.put(i, Lists.newArrayList(
        new InputTransformationContainer("big", bg),
        new InputTransformationContainer("great", gc1),
        new OutputTransformationContainer("sc", SmallClass.class),
        new OutputTransformationContainer("ac", AnotherClass.class)
    ));
});
// ...
```
In this example we perform the transformation 10 times. You can of course always invoke a regular transformation in a loop. You may be asking yourself if that is the case why would you want to perform a transformation this way? There are two major reasons that you gain using the multi-transformer. 

1. You can utilize function transformations.
2. You can utilize overrides.

### Function Transformations
A function transformation is a way of dynamically generating the data based on the current state of the transformation. This can be fixed and pre-determined or more dynamic in nature.

```java
public interface FunctionTransformation<V> {
    V next(TransformationContext context);
}
```
this is the interface for a [`FunctionTransformation`](src/main/java/org/finra/scaffolding/transformer/function/FunctionTransformation.java). It is essentially an infinite stream of data that takes in the current context. A possible example of a function transformation may be sequential transaction id, or dates and time. There is an implementation called [`LongSequential`](src/main/java/org/finra/scaffolding/transformer/function/impl/LongSequential.java) which is perfect for numeric identifier, and the [`DateTimeSequential`](src/main/java/org/finra/scaffolding/transformer/function/impl/DateTimeSequential.java) which is great for dates and times. Those two functions may take custom start values, custom steps or intervals, and may be either ascending or descending. They both come from the [`OrderedFunctionTransformation`](src/main/java/org/finra/scaffolding/transformer/function/OrderedFunctionTransformation.java).

In the `SmallClass` there is a field called `dateSeq`:
```java
public class TestClass {
    // ...
    @Transformation(function=@FunctionTransformation(key="date1", clazz=DateTimeSequential.class))
    private LocalDateTime dateSeq;
    // ...
}

```
A function transformation is given a unique alias to identify it. Here are a few simple declaration:
```java
// ...
mt.setFunctionTransformation("seq", LongSequential.class);
mt.setFunctionTransformation("seq1", LongSequential.class, new Object[]{10.00 , 5}, new Class[]{Long.class, long.class});
mt.setFunctionTransformation("date1", DateTimeSequential.class, new Object[]{LocalDateTime.of(1776, Month.JULY, 4, 0, 0)}, new Class[]{LocalDateTime.class});
// ...
```
The "seq" function transformation is using the default parameters for the `LongSequential` transformation. It starts are 0 and increments by 1 per iteration. The "seq1" transformation is explicitly defined to start at 10 with a step of 5 per iteration. Lastly is the "date1" transformation which starts at the signing of the declaration of the independence with a default step of one day per iteration.

### Overrides
When performing a multi-transformer you may want to override the transformation in various conditions. The `orderedTransform()` call can take a `Map<Predicate<TransformationContext>, OutputOverride>` override parameter.  The predicate takes in the current [TransformationContext](src/main/java/org/finra/scaffolding/transformer/service/TransformationContext.java) if the predicate evaluates as true then the `OutputOverride` will be evaluated. An override is per class and field. It then has a condition that may be evaluated. 

```java
Map<Predicate<TransformationContext>, Set<OutputOverride>> overrides = new HashMap();
overrides.put(
    ct -> ct.iteration % 2 == 0,
    Sets.newHashSet(
        new OutputOverride<>(
            SmallClass.class,
            "fruit",
            (sc) -> true,
            () -> "flounder"
        )
    )
);

Map<Long, ? extends Collection<TransformationContainer>> os = mt.orderedTransform(
    m,
    10L,
    overrides
);
```

This example will set the "fruit" field to the value "flounder" for each even iteration of this transformation.

## Transformations Without Annotations
There may be times where it is preferable to programmatically describe transformations as opposed to using annotations. Fortunately it is not an all or nothing decision. You can combine both annotation and programmatic transformation metadata. This is accomplished using the [TransformationsImpl](/org/finra/datagenerator/scaffolding/transformer/service/transformations/TransformationsImpl.java) which utilizes a builder pattern.

Let's look at a snippet of how this may look:
```java
Transformer t = new Transformer(random, objectMapper);
t.registerTransformations(
    TransformationsImpl.multiBuilder()
        .withField(SmallClass.class.getDeclaredField("fruit"))
            .withTransformations()
                .withTransformation()
                    .withValue("#big.name")
                .build()
            .build()
        .build()
        .withField(SmallClass.class.getDeclaredField("tomorrow"))
            .withTransformations()
                .withTransformation()
                    .withValue("#big.num")
                .build()
        .build()
    .build()
.build());

List<TransformationContainer> outputs = t.transform(Lists.newArrayList(
    new InputTransformationContainer("big", bg),
    new OutputTransformationContainer("sc", SmallClass.class)
), TransformationSessionType.REGISTERED_ONLY);
```

These simple transformations set the `fruit` field of the `SmallClass` to the value of the field `name` of the object with the alias "big". The other transformation is setting the field `tomorrow` to the value of the field `num` of the object with the alias "big".

The second parameter of the `transform()` method takes in a value from the enum [`TransformationSessionType`](/src/main/java/org/finra/scaffolding/transformer/service/TransformationSessionType.java). The nature of this parameter is to provide the flexibility and power of controlling the transformations that are registered with the given `Transformer`. 

### REGISTERED_ONLY
When this session type is used any annotations that may be present on the classes will be ignored, only the transformations that are registered programmatically will be utilized.
 
### MERGE
When this session type is used all current transformations will be merged with any annotations present in the provided classes. A given `Transformation` will merge existing with new `Transformation`s. If the `@Order` or `order` attribute on a single `Transformation` is already present, then it will be duplicated. This behavior may be slightly unpredictable since the sorted order will be the same. Generally, merge is not recommended due to that reason.
 
 ### REPLACE
 This session type will replace all transformations registered with the currently provided ones.
 
 ### CURRENT_ONLY
 The registered transformations will remain untouched, but only the annotations in the classes provided will actually be used when processing the current transformation.

## Transformer value processing
This is the order fields are processed:

1. Random value is assigned to field based on its type.
2. Check if the field has an override? If so use that value.
3. If there is a transformation set to be NULL, make it NULL.
4. If there is a transformation set to be an empty string, make it an empty string.
5. If there is a transformation that has a function transformation, apply it.
6. If there is a transformation with a value expression, apply it.


## Resources
* Java 8
* [Jackson](https://github.com/FasterXML/jackson)
* [SpEL](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html)
* [Spring Framework](https://projects.spring.io/spring-framework/)
* [Random Beans](https://github.com/benas/random-beans)