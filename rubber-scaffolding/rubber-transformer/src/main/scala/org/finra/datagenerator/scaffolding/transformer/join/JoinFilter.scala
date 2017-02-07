package org.finra.datagenerator.scaffolding.transformer.join

import org.finra.datagenerator.scaffolding.config.{ConfigBundle, ConfigDefinition, ConfigName, Configurable}


/**
  * Created by dkopel on 12/19/16.
  */
trait JoinFilter {
    def apply[T](ja: JoinsAliases[T]): JoinsAliases[T]
}
object JoinFilter extends Configurable {
    object JoinTypeName extends ConfigName("joinType")
    val JoinType: ConfigDefinition[JoinFilter] = ConfigDefinition[JoinFilter](
        JoinTypeName,
        Some(FullJoin)
    )

    override def configBundle: ConfigBundle = {
        ConfigBundle(
            getClass,
            Seq(
                JoinType
            )
        )
    }
}

object FullJoin extends JoinFilter {
    override def apply[T](ja: JoinsAliases[T]): JoinsAliases[T] = {
        JoinsAliases(
            Joins(ja.joins.values.filter(k => k._2.values.values.size.equals(ja.aliases.values.size))),
            ja.aliases
        )
    }
}
object PartialJoin extends JoinFilter {
    override def apply[T](ja: JoinsAliases[T]): JoinsAliases[T] = {
        JoinsAliases(
            Joins(ja.joins.values.filter(k => k._2.values.values.size > 1 && k._2.values.values.size <= ja.aliases.values.size )),
            ja.aliases
        )
    }
}
