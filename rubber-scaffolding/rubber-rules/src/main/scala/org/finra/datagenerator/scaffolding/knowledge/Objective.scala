package org.finra.datagenerator.scaffolding.knowledge

import java.util.UUID

/**
  * Created by dkopel on 9/19/16.
  */
/*
An objective is the definition of what the
 */
trait Objective[T] extends Serializable{
    val id: UUID
    val description: String

}
