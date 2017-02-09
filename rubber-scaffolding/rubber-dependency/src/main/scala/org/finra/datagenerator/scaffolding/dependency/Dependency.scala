package org.finra.datagenerator.scaffolding.dependency

/**
  * Created by dkopel on 9/19/16.
  */

/**
  * Represents a void that can be filled by
  * the appropriate value
  *
  */
trait Dependency extends Serializable with Ordered[Dependency] {
    /*
    Determines whether or not this dependency is required or not.
    A dependency that is not required, its resolution will still be attempted
    however it is considered not erroneous if no resolution may be found
     */
    def required: Boolean

    /*
    Used to comp
     */
    def priority: Long
    def dependencies: Seq[Dependency]
    def resolved: Boolean
}
