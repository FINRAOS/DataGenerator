package org.finra.datagenerator.scaffolding.transformer.service

import java.lang.reflect.Field

import org.finra.datagenerator.scaffolding.config.{Configuration, LocalConfig}
import org.finra.datagenerator.scaffolding.utils.ReflectionUtils
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.core.RubberRandom
import org.finra.datagenerator.scaffolding.transformer.join._
import org.finra.datagenerator.scaffolding.transformer.service.{OutputTransformationContainer, TransformationContainer, TransformationContext, TransformationSessionType}
import org.finra.datagenerator.scaffolding.transformer.join.JoinFilter.JoinTypeName
import org.finra.datagenerator.scaffolding.transformer.join._

import scala.collection.JavaConverters._

/**
  * This service provides functionality to `join` collections of data that share keys
  */
trait JoinService extends TransformationsProvider {
    this: TransformationSession =>
    val JoinsAnnotation: Class[org.finra.datagenerator.scaffolding.transformer.support.Joins] = classOf[org.finra.datagenerator.scaffolding.transformer.support.Joins]


    private[service] var registeredJoins: Map[Class[_], JoinsImpl] = Map.empty

    def registerJoin(clazz: Class[_], joins: JoinsImpl) = registeredJoins += clazz->joins

    def join[T](implicit tContext: TransformationContext, conf: Configuration): java.util.List[OutputTransformationContainer[T]] = {
        // Set context to active
        setContext(tContext)
        tContext.getIterationOutputs
            .asScala
            .toList
            .asInstanceOf[List[TransformationContainer[T]]]
            .map(c => {
                logger.debug("Processing container {}/{}", c.alias.asInstanceOf[Any], c.clazz)
                val ja = getJoinKeys(c)
                val joins = filterJoins(ja).joins.values
                logger.debug("Before processing join: {}", joins)
                val outs = joins.map(j => processJoin(c, j._1, j._2, ja.aliases))
                ja.aliases.values.foreach(t => registerVariable(t._1, t._2))
                val nc = new OutputTransformationContainer(c.alias, c.clazz, outs.toList.asJava.asInstanceOf[T], c.order, c.join)
                tContext.addTransformationContainer(nc)
                registerVariable(c.alias, outs)
                nc
            }).asJava
    }

    private def processJoin[T](
                                  container: TransformationContainer[T],
                                  join: JoinKeys, values: JoinValues[_],
                                  aliases: Aliases)
                              (implicit conf: Configuration) = {
        join.values.foreach(k => {
            logger.debug(s"Registering join key ${k._1} -> ${k._2}")
            registerVariable(s"joinKey_${k._1}", k._2)
        })

        // Alias instances
        val fields = values.values.flatMap(k => {
            logger.debug("Registering alias {}: {}", k._1.asInstanceOf[Any], k._2)
            val value = {
                if (k._2.isInstanceOf[Iterable[_]]) {
                    logger.debug("The data for the alias {} has {} values in the iterable", k._1, k._2.asInstanceOf[Iterable[_]].size)
                    k._2.head
                }
                else k._2
            }
            registerVariable(k._1, value)
            value.getClass.getDeclaredFields.map(ff => ff.getName -> ReflectionUtils.getFieldValue(ff, value))
        })

        logger.debug("Fields: {}", fields)

        locally {
            implicit val lc = LocalConfig(
                Seq(
                    TransformationSession.DefaultFieldStrategy(defaultStrategyFromFields(fields)),
                    TransformationSession.AddTransformationContainerToContext(false)
                )
            )(conf)

            processOutputClass(container).value
        }
    }

    private def defaultStrategyFromFields(fields: Map[String, Field]): DefaultFieldGenerateStrategy = {
        new DefaultFieldGenerateStrategy {
            override def apply[T](field: Field, inst: T)(implicit rubberRandom: RubberRandom) = {
                logger.debug("Using join adopting field generate strategy for field {}", field.getName)
                if (fields.contains(field.getName)) {
                    ReflectionUtils.setField(field, inst, fields(field.getName))
                }
                inst
            }
        }
    }

    private def getAliases[T](implicit tContext: TransformationContext, conf: Configuration) = {
        Aliases(tContext.getAliases.asScala.toMap.map(t => {
            val c: Class[T] = t._2.asInstanceOf[Class[T]]
            logger.debug("Lookup: {}", t._1)
            val it = lookupVariable(t._1).asInstanceOf[java.lang.Iterable[T]].asScala.toSet
            t._1 -> it
        }))
    }

    private def filterJoins[T](ja: JoinsAliases[T])(implicit conf: Configuration): JoinsAliases[_] = {
        logger.debug("Filtering {} joins : {}", ja.joins.values.size, ja.joins)
        val filtered = conf.conf[JoinFilter](JoinTypeName).getValue()(ja)
        logger.debug("Filtered {} joins : {}", filtered.joins.values.size, filtered.joins)
        filtered
    }

    def getJoinKeys[T, S](container: TransformationContainer[_])
                                 (implicit tContext: TransformationContext, conf: Configuration): JoinsAliases[_] = {
        // Grab aliases
        val aliases = getAliases

        val groupedJoins = getJoins(container)
            .toList
            // Flatten the joins
            .flatMap(j => { j.fields.flatMap(f => {
                val values: Set[T] = aliases.values(j.alias).asInstanceOf[Set[T]]
                // For each join value create a `JoinValue` to contain the relevant data
                values.map(vs => {
                    JoinValue(vs, f.key, j.alias, ReflectionUtils.getFieldValue(f.field, vs))
                })
            })
        }).groupBy(t => (t.inst, t.alias))

        val grouped = groupedJoins.map(t => {
            // Group by keys
            groupedJoins.foldLeft(
                // Initial value: JoinKeys->Set[(T, String)]
                JoinKeys(t)->JoinValues(Map[String, Set[T]](t._1._2->Set(t._1._1)))
            )((key, currentItem) => {
                val jk = key._1
                var vals = key._2.values
                val njk = JoinKeys(currentItem)
                if(njk.equals(jk)) {
                    if(!vals.contains(currentItem._1._2)) {
                        vals += (currentItem._1._2 -> Set(currentItem._1._1))
                    } else {
                        vals += currentItem._1._2 -> (vals(currentItem._1._2) + currentItem._1._1)
                    }

                    njk.merge(key._1) -> JoinValues(vals)
                } else {
                    key
                }
            })
        })

        JoinsAliases(Joins(grouped), aliases)
    }

    def getJoinsFromContainer(container: TransformationContainer[_])
                             (implicit tContext: TransformationContext, conf: Configuration): JoinsImpl = {
        JoinsImpl(container.clazz.getAnnotation(JoinsAnnotation))(this)
    }

    def getJoins(container: TransformationContainer[_], sessionType: TransformationSessionType=TransformationsProvider.defaultSessionType)
                                      (implicit tContext: TransformationContext, conf: Configuration): Set[JoinImpl] = {
        sessionType match {
            case TransformationSessionType.MERGE => {
                val oj = registeredJoins.get(container.clazz)
                val nj = getJoinsFromContainer(container)

                val c = if(oj.isDefined) {
                    if(oj.get.condition.equals(nj.condition)) nj.condition else oj.get.condition+" || "+nj.condition
                } else nj.condition
                val combined = oj.getOrElse(JoinsImpl.empty).joins ++ nj.joins

                registeredJoins += container.clazz->JoinsImpl(combined, c)(this)
                combined
            }
            case TransformationSessionType.REGISTERED_ONLY => registeredJoins(container.clazz)
            case TransformationSessionType.REPLACE => {
                registeredJoins += container.clazz->getJoinsFromContainer(container)
                registeredJoins(container.clazz)
            }
            case TransformationSessionType.CURRENT_ONLY => getJoinsFromContainer(container)
        }
    }
}