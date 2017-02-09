package org.finra.datagenerator.scaffolding.random.types

import org.finra.datagenerator.scaffolding.config.{ConfigDefinition, ConfigName, Configurable, Configuration}
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.core.RubberRandom

/**
  * Created by dkopel on 12/12/16.
  */
trait CollectionType
trait GeneralCollection extends CollectionType
trait MapType extends CollectionType
trait ListType extends CollectionType
trait QueueType extends ListType
trait StackType extends ListType
trait SetType extends CollectionType

object CollectionTypes extends Configurable with Logging with TypeProcessor {
    object CollectionTypesName extends ConfigName("collectionTypes")
    object CollectionRangeName extends ConfigName("collectionRange")

    val types: Map[Class[_], Class[_ <: CollectionType]] = Map(
        // Unordered collection
        classOf[java.util.Collection[_]]->classOf[GeneralCollection],
        classOf[java.util.Set[_]]->classOf[SetType],

        // Ordered collection
        classOf[java.util.Queue[_]]->classOf[QueueType],
        classOf[java.util.Stack[_]]->classOf[StackType],
        classOf[java.util.List[_]]->classOf[ListType],
        classOf[scala.collection.Seq[_]]->classOf[ListType],
        classOf[scala.collection.immutable.List[_]]->classOf[ListType],
        classOf[scala.collection.mutable.ListBuffer[_]]->classOf[ListType],

        // Map
        classOf[java.util.Map[_, _]]->classOf[MapType],
        classOf[scala.collection.immutable.Map[_, _]]->classOf[MapType],
        classOf[scala.collection.mutable.Map[_, _]]->classOf[MapType],
        classOf[scala.collection.Map[_, _]]->classOf[MapType]
    )

    val typesDef = ConfigDefinition[Map[Class[_], Class[_ <: CollectionType]]](
        CollectionTypesName,
        Some(types)
    )

    val collectionsRange = ConfigDefinition[Range](
        CollectionRangeName,
        Some(Range(10, 50))
    )

    override def configBundle: ConfigBundle = {
        ConfigBundle(
            getClass,
            Seq(
                typesDef,
                collectionsRange
            )
        )
    }

    def getCollectionTypes(implicit conf: Configuration): Map[Class[_], Class[_ <: CollectionType]] = {
        conf.conf[Map[Class[_], Class[_ <: CollectionType]]](CollectionTypes.CollectionTypesName).getValue()
    }

    def isCollection(clazz: Class[_])(implicit configuration: Configuration): Boolean = getCollectionTypes(configuration).keySet.contains(clazz)

    def isCollectionType(clazz: Class[_], tpe: Class[_ <: CollectionType])(implicit configuration: Configuration): Boolean = getCollectionTypes(configuration)(clazz).equals(tpe)

    override def eval[T](typeContainer: TypeContainer[T])(
        implicit rubberRandom: RubberRandom,
        conf: Configuration
    ): Boolean = isCollection(typeContainer.clazz)

    override def apply[T](tt: TypeContainer[T])(
        implicit rubberRandom: RubberRandom,
        conf: Configuration
    ): T = {
        // Map
        if(isCollectionType(tt.clazz, classOf[MapType])) {
            // Convert to map type specified
            MapTypeConvert(tt)
        } else if(isCollectionType(tt.clazz, classOf[ListType])) {
            // Convert to list type specified
            ListTypeConverter(tt)
        } else {
            logger.warn("Unknown collection type {}", tt.clazz)
            throw new IllegalArgumentException
        }
    }
}