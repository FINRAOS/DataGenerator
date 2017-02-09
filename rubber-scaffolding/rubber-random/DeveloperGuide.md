# Rubber Random - Developer Guide

This module was designed from the ground up to be flexible and still be relatively simple to use. For most configuration options there are predefined defaults that will work for many cases. The configuration system works with the notion of a current context. Different configurations may be applicable depending on the current state of the system. We will explain this more later.

### The Predicate
A predicate is a simple expression that returns a boolean value. In scala using the fat arrow notation you can easily define a predicate like this:
```scala
def isEven(number: Int): Boolean = number % 2 == 0
```
This simple function is a simple predicate that returns true if the inputted value is evenly divisible by two.
```scala
trait Food {
    val name: String
    var currentTemp: Int
    var currentDuration: Int
    def cookTemp: Int // Degrees F
    def cookDuration: Int // Seconds    
}
trait Bakery {
    var readyItems = Seq.empty[Food]
    
    def howWeBake[T <: Food]: T=>Boolean

    def takeOut[T <: Food](food: T) = {
        if(howWeBake(food)) {
            readyItems :+ food
            println("Took it out of the oven!")
        }       
    }
}
class Cake() extends Food {
    val name = "Cake"
    var currentTemp: Int = 0
    var currentDuration: Int = 0
    val cookTemp = 350
    val cookDuration = 30 * 60 // 30 minutes * 60 seconds
}
class MyBakery extends Bakery {
    @Override
    def howWeBake[T <: Food]: T=>Boolean = {
        food => food.cookDuration.equals(food.currentDuration) &&
        food.currentTemp.equals(food.cookTemp)
    }
}
```
What we did here is describe a simple bakery. We created a `Food` trait which specifies the required temperature and duration in order for the food item to be ready to eat. The bakery will `takeOut` the food and add it to the list of ready items if the food is considered ready. The `howWeBake` method remains abstract to be defined by its implementation.
   
The bakery trait lets each bakery decide how it cooks food and when it is considered ready. This separates the logic making a very customizable interface.
  
### [TypeContainer](src/main/scala/org/finra/datagenerator/scaffolding/random/types/TypeContainer.scala)
There are many different types of data that Rubber Random can generate. Rubber Random first and foremost supports Java built-in types, [POJO](https://en.wikipedia.org/wiki/Plain_Old_Java_Object), Java collections, and generics. Support for Scala built-in types, as well as Scala collections (mutable and immutable) and generics.

A [TypeContainer](src/main/scala/org/finra/datagenerator/scaffolding/random/types/TypeContainer.scala) abstracts the different types and provides a single interface for the framework to deal with. The `TypeContainer` is composed of three main parts:
* clazz: Class<T> - The base underlying type
* types:  Seq<TypeContainer<T>> - The secondary types 
* parentTypeContainer: Option<TypeContainer<T>> - A parent type container if present

The other thing that the `TypeContainer` maintains is a [Stack](src/main/scala/org/finra/datagenerator/scaffolding/random/types/Stack.scala). The `Stack` hold every step of the generating process. This information is important for recursion as well as collections.  

### [RandomContext](src/main/scala/org/finra/datagenerator/scaffolding/random/predicate/RandomContext.scala)
The `RandomContext` is passed along which holds the current `TypeContainer`, access to the current `Random` seed, as well as the parent`TypeContainer`.

### [RandomGenerator](src/main/scala/org/finra/datagenerator/scaffolding/random/predicate/RandomGenerator.scala)
This is used to generate the random value.
```scala
trait RandomGenerator[T] extends (RandomContext=>T)
```
A very simple random generator that generates an `Integer` is:
```scala
class IntRandomGenerator extends RandomGenerator[Integer] {
    val random = new java.util.Random()
    override def apply(rc: RandomContext): Integer = random.nextInt() 
}
```

### [RandomPredicate](src/main/scala/org/finra/datagenerator/scaffolding/random/predicate/RandomPredicate.scala)
The `RandomPredicate` takes the `RandomContext` as an argument and returns a `Boolean`, this is the predicate. The `action` has the type `RandomGenerator[T]` which returns the generated value. The priority defaults to the value `Long.MaxValue`. The priority is used to sort the priorities to choose the most appropriate one for the situation.
   
### [PredicateRegistry](src/main/scala/org/finra/datagenerator/scaffolding/random/predicate/PredicateRegistry.scala)
Rubber Random chooses how to generate data base on evaluating predicates the are registered to the system. Within that list of valid predicates they are sorted and the strongest priority will be selected.

#### [PredicateType](src/main/scala/org/finra/datagenerator/scaffolding/random/predicate/PredicateType.scala)
There are three types of predicates:
* `InternalPredicate` - This is a built-in predicate that are made to handle primitives and built-in classes. 
* `CustomPredicate` - This is for any custom predicate to be added to the system.

##### Built-In Predicates
###### Primitives
1. `Long` 
2. `Float`
3. `Double`
4. `Int`
5. `BigInt`
6. `BigDecimal`
7. `Boolean`
8. `Char`
9. `String`

###### Other built-in
1. `UUID`
2. `Date`
3. `LocalDateTime`

###### [GeneratingStrategy](src/main/scala/org/finra/datagenerator/scaffolding/random/strategy/GeneratingStrategy.scala)
Let's look at what a `GeneratingStrategy` is:
```scala
trait GeneratingStrategy extends Logging {
    def apply[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit conf: Configuration): T
}
```
When a predicate is *not* present for a given `TypeContainer`, a `GenerationStrategy` is used to generate the data. 

###### Protected Packages
Any class that is a member of one of the protected packages (by default that is sun.\*\*, java.\*\*, scala.\*\*) if there isn't a predicate found an exception will be thrown. This is because the assumption is that you would want to implement a `RandomGenerator` and not rely on a `GeneratingStrategy` for built-in types.

##### How to create a Randomizer
The `RandomGenerator<T>` interface look like this:
```java
public interface RandomGenerator<T> {
    T apply(RandomContext randomContext);
}
```
Rubber Random looks at its `basePackages` for any class that implements `ClassRandomGenerator<T>`. It is easy to add this capacity to a randomizer which makes it very easy to create new randomizers and easily use them.

Let's look at a simple example. We have a simple class that represents an `Email`:
```java
public class Email {
    private String user;

    private String domain;

    private String tld;

    public Email(String user, String domain, String tld) {
        this.user = user;
        this.domain = domain;
        this.tld = tld;
    }

    public String getUser() {
        return user;
    }

    public String getDomain() {
        return domain;
    }

    public String getTld() {
        return tld;
    }

    @Override
    public String toString() {
        return user+"@"+domain+"."+tld;
    }
}
```
This is a randomizer implementation for the `Email`:
```java
public class EmailRandomizer implements JavaClassRandomGenerator<Email> {
    @Override
    public Class<?>[] classes() {
        return new Class<?>[] { Email.class };
    }
    
    @Override
    public Email apply(RandomContext rc) {
        String name = rc.jpr().strings.nextString(5, 10);
        String domain1 = rc.jpr().strings.nextAlphaString(5, 10);
        String domain2 = rc.jpr().strings.nextAlphaString(2, 3).toLowerCase();
        return new Email(name, domain1, domain2);
    }
}
```
All the classes that this randomizer should generate can be indicated in the `classes` function.