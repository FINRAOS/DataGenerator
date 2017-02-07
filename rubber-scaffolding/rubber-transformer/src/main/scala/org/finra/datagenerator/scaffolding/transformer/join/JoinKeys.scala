package org.finra.datagenerator.scaffolding.transformer.join

import org.slf4j.LoggerFactory

/**
  * Created by dkopel on 12/28/16.
  */
case class JoinKeys(values: Map[String, _]) {
    val logger = LoggerFactory.getLogger(getClass)

    def eval(o: JoinKeys): Map[JoinKeyCompare, Int] = {
        var vals = Map[JoinKeyCompare, Int](
            WrongValue->0,
            CorrectValue->0,
            AbsentKey->0,
            AbsentValue->0,
            PresentValue->0
        )

        vals += AbsentKey->(values.keySet diff o.values.keySet).size
        vals += PresentKey->(o.values.keySet diff values.keySet).size

        values.foreach(t => {
            if(o.values.keySet.contains(t._1)) {
                val ov = o.values(t._1)
                val t2 = t._2

                if(ov == null && t2 == null) {
                    vals += CorrectValue->(vals(CorrectValue) + 1)
                } else if(ov == null) {
                    vals += AbsentValue->(vals(AbsentValue) + 1)
                } else if(t2 == null) {
                    vals += PresentValue->(vals(PresentValue) + 1)
                } else if(ov.equals(t2)) {
                    vals += CorrectValue->(vals(CorrectValue) + 1)
                } else {
                    vals += WrongValue->(vals(WrongValue) + 1)
                }
            }
        })

        vals
    }

    def merge(o: JoinKeys): JoinKeys = {
        JoinKeys(values.filter(t => t._2 != null) ++  o.values.filter(t => t._2 != null))
    }

    override def canEqual(that: Any): Boolean = equals(that)

    override def equals(that: Any): Boolean = that match {
        case o: JoinKeys => {
            val vals = eval(o)

            val result = {
                if(vals(WrongValue) > 0) false
                else if(vals(CorrectValue) > 0) true
                else false
            }

//            logger.debug("This: {}, That: {}, Result: {}", values, o.values, vals)
            //logger.debug(s"RESULT $result, Wrong value: $wrongValue, Correct value: $correctValue, Absent key: $absentKey, Absent value: $absentValue")

            result
        }
        case _ => false
    }

    override def toString = values.toString
}

object JoinKeys {
    implicit def apply[T](t: ((T, String), List[JoinValue[T, Nothing]])): JoinKeys = {
        JoinKeys(t._2.map(tt => tt.key -> tt.value).toMap)
    }
}