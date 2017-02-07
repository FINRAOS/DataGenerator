package org.finra.datagenerator.scaffolding.transformer.join

/**
  * Created by dkopel on 12/28/16.
  */
trait JoinKeyCompare
object WrongValue extends JoinKeyCompare
object CorrectValue extends JoinKeyCompare
object AbsentKey extends JoinKeyCompare
object AbsentValue extends JoinKeyCompare
object PresentKey extends JoinKeyCompare
object PresentValue extends JoinKeyCompare