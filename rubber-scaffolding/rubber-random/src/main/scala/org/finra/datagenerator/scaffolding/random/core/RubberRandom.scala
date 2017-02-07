package org.finra.datagenerator.scaffolding.random.core

import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.core.RubberRandom.ProtectedPackagesName
import org.finra.datagenerator.scaffolding.random.predicate._
import org.finra.datagenerator.scaffolding.random.strategy.{FieldGeneratingStrategy, GeneratingStrategy}
import org.finra.datagenerator.scaffolding.random.support.{AnnotationProcessor, CustomRandomizerProcessor}
import org.finra.datagenerator.scaffolding.random.types.{CollectionTypes, ParameterizedTypeReference, TypeContainer}
import org.finra.datagenerator.scaffolding.random.{Parameter, RandomProvider}
import org.springframework.util.AntPathMatcher

import scala.reflect.runtime.universe._
import scala.util.matching.Regex


/**
  * Created by dkopel on 11/25/16.
  */
abstract class RubberRandom(
                               val basePackages: Seq[String] = Seq("org.finra.datagenerator.scaffolding"),
                               val conf: Option[Configuration]=Option.empty
                           )
    extends Configuration(ConfigurationProvider(basePackages, conf))
    with PredicateRegistry
    with RandomProvider
    with JavaPrimitives {

    val matcher = new AntPathMatcher(".")
    val custom = new CustomRandomizerProcessor
    val config = new AnnotationProcessor
    var regexProviders = Map.empty[Regex, RegexGenerator]

    val _predicates = collection.mutable.Map[PredicateType, Set[RandomPredicate[_]]](
        InternalPredicate->BuiltInPredicates.predicates(this),
        CustomPredicate->Set.empty
    )

    implicit def generateRegex(regex: String): RegexGenerator = generateRegex(new Regex(regex))

    def generateRegex(regex: Regex): RegexGenerator = {
        if(regexProviders.contains(regex)) {
            regexProviders(regex)
        } else {
            val r = new RubberRegexRandom()(this).generate(regex)
            regexProviders += regex->r
            r
        }
    }

    def generate(regex: Regex): String = generateRegex(regex).next

    protected def generateCustomWithStrategy[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit configuration: Configuration): T = {
        val pkg = typeContainer.clazz.getPackage.getName
        if(configuration.conf[Seq[String]](ProtectedPackagesName).getValue().count(p => matcher.`match`(p, pkg)) > 0) {
            val err = s"Class ${typeContainer.clazz.getName} is a built in class and should not be generated through a custom strategy!"
            logger.error(err)
            throw new IllegalStateException(err)
        }

        configuration.conf[Class[FieldGeneratingStrategy]](GeneratingStrategy.GeneratingStrategyName).getValue() match {
            case c: Class[FieldGeneratingStrategy] => FieldGeneratingStrategy(this, custom, config)(typeContainer, args)(configuration)
            case _ => null.asInstanceOf[T]
            //case MethodAccessorGeneratingStrategy =>
        }
    }

    override def nextObject[T](clazz: Class[T], args: Parameter[_]*): T = generate[T](clazz, args.toSeq)(this)

    override def nextObject[T](clazz: Class[T]): T = generate[T](clazz, Seq.empty[Parameter[_]])(this)

    override def next(bits: Int): Int = nextInt()

    override def nextBoolean: Boolean = jpr.booleans.nextBoolean()

    override def nextBytes(bytes: Array[Byte]): Unit = jpr.bytes.nextBytes(bytes)

    override def nextDouble(): Double = jpr.doubles.nextDouble()

    override def nextGaussian(): Double =jpr.doubles.nextDouble()

    override def nextFloat(): Float = jpr.floats.nextFloat()

    override def nextInt(): Int = jpr.ints.nextInt()

    override def nextLong(): Long = jpr.longs.nextLong()

    override def setSeed(seed: Long): Unit = jpr.random.setSeed(seed)

    def generateCount[T, U](tc: TypeContainer[T], action: TypeContainer[T]=>U)(implicit conf: Configuration) = {
        val count = getCount(tc)
        logger.debug("Generating {} instances for {}", count, tc)
        Range.apply(0, count)
            .map(i => {
                tc.iteration = i
                tc(action(tc))
            })
    }

    def generateTypeContainer[T](typeContainer: TypeContainer[T])(implicit configuration: Configuration): T = generateTypeContainer(typeContainer, Seq.empty)(configuration)

    def generateTypeContainer[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit configuration: Configuration): T

    def generate[T](implicit typeTag: TypeTag[T], conf: Configuration): T = generateTypeContainer[T](TypeContainer(typeTag, conf), Seq.empty)

    def generate[T](args: Seq[Parameter[_]]=Seq.empty)(implicit typeTag: TypeTag[T], conf: Configuration): T = generateTypeContainer[T](TypeContainer(typeTag, conf), args)

    def generate[T](clazz: Class[T])(implicit configuration: Configuration): T = generate[T](clazz, Seq.empty[Parameter[_]])(configuration)

    def generate[T](clazz: Class[T], args: Seq[Parameter[_]])(implicit configuration: Configuration): T = generateTypeContainer(TypeContainer(clazz)(configuration), args)(configuration)

    def generate[T](field: java.lang.reflect.Field)(implicit configuration: Configuration): T = {
        FieldGeneratingStrategy(this, custom, config).generateField(field, TypeContainer(field.getDeclaringClass))
    }

    def generate[T](parameterizedType: ParameterizedTypeReference[T])(implicit configuration: Configuration): T = {
        generateTypeContainer(TypeContainer[T](parameterizedType))
    }

    def getClassInfo(clazz: Class[_]): (Class[_], Boolean) = {
       (if(clazz.isArray) clazz.getComponentType else clazz, clazz.isArray)
    }

    def isClass(clazz: Class[_], classes: Class[_]*): Boolean = {
        classes.exists(cc => clazz.isAssignableFrom(cc))
    }

    def getCount(typeContainer: TypeContainer[_])(implicit conf: Configuration): Int = {
        val crn = conf.conf[Range](CollectionTypes.CollectionRangeName)
        val rge = crn.getValue()
        rge(jpr.ints.nextInt(0, rge.size))
    }
}
object RubberRandom extends Configurable with Logging {
    object RecursionEnabledName extends ConfigName("recursionEnabled")
    object MaxRecursionCountName extends ConfigName("maxRecursionCount")
    object ProtectedPackagesName extends ConfigName("protectedPackages")
    object RegexStrategyName extends ConfigName("regexStrategy")

    override def configBundle: ConfigBundle = ConfigBundle(
        getClass,
        Seq(
            ConfigDefinition[Int](
                MaxRecursionCountName,
                Some(5)
            ),
            ConfigDefinition[Boolean](
                RecursionEnabledName,
                Some(true)
            ),
            ConfigDefinition[Seq[String]](
                ProtectedPackagesName,
                Some(Seq(
                    "sun.**",
                    "java.**",
                    "scala.**"
                ))
            ),
            ConfigDefinition[RegexStrategy](
                RegexStrategyName,
                Some(SimpleRegex)
            )
        )
    )
}