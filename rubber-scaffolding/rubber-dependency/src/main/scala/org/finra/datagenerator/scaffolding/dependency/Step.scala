package org.finra.datagenerator.scaffolding.dependency

import java.util.UUID

/**
  * Created by dkopel on 9/20/16.
  */
trait Step[+T] extends Serializable {
    val id: UUID
}
