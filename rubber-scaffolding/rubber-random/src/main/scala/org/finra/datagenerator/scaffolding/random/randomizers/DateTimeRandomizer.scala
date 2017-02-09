package org.finra.datagenerator.scaffolding.random.randomizers

import java.time._
import java.util.Date

import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.config._
import org.finra.datagenerator.scaffolding.random.predicate.{ClassRandomGenerator, RandomContext}

/**
  * Created by dkopel on 12/12/16.
  */
class DateTimeRandomizer extends ClassRandomGenerator[Date]
    with Configurable with AnnotationCapable with Logging {
    override def apply(rc: RandomContext): Date = {
        rc.conf.conf[DateGeneratingStrategy](dateStrategyName).getValue().apply(rc)
    }

    object minEpochLongDateName extends ConfigName("minEpochLongSeconds")
    object maxEpochLongDateName extends ConfigName("maxEpochLongSeconds")
    object dateRangeYearsName extends ConfigName("dateRangeYears")
    object dateStrategyName extends ConfigName("dateStrategy")

    trait DateGeneratingStrategy extends (RandomContext=>Date)

    val SECONDS_PER_DAY: Long = 60 * 24 * 24

    object YearRange extends DateGeneratingStrategy {
        override def apply(rc: RandomContext): Date = {
            val second = rc.jpr.longs.nextLong(0L, SECONDS_PER_DAY)
            val day = rc.jpr.ints.nextInt(1, 365)

            val dateYearRange = rc.conf.conf[Range](dateRangeYearsName).getValue()
            val i = dateYearRange(rc.jpr.ints.nextInt(0, dateYearRange.size-1))
            val year = Year.of(Year.now().getValue + i)

            logger.debug("Creating date from year: {}, day: {}, second: {}", year, day, second)
            Date.from(
                LocalDateTime.of(year.atDay(day),
                LocalTime.ofSecondOfDay(second)).toInstant(ZoneOffset.UTC)
            )
        }
    }

    object MinMaxEpochLong extends DateGeneratingStrategy {
        override def apply(rc: RandomContext): Date = {
            val minEpochLong = rc.conf.conf[java.lang.Long](minEpochLongDateName).getValue()
            val maxEpochLong = rc.conf.conf[java.lang.Long](maxEpochLongDateName).getValue()

            val d = rc.jpr.longs.nextLong(minEpochLong, maxEpochLong)
            val i = Instant.ofEpochSecond(d)

            logger.debug("Using time {} and instant {}", d, i.asInstanceOf[Any])
            Date.from(i)
        }
    }

    val minEpochLongSeconds = ConfigDefinition[java.lang.Long](
        minEpochLongDateName,
        Some(-2211753600L)
    )

    val maxEpochLongSeconds = ConfigDefinition[java.lang.Long](
        maxEpochLongDateName,
        Some(253368000000L)
    )

    val dateRangeYears = ConfigDefinition[Range](
        dateRangeYearsName,
        Some(Range(-10, 10))
    )

    val dateStrategy = ConfigDefinition[DateGeneratingStrategy](
        dateStrategyName,
        Some(YearRange)
    )

    override def configBundle: ConfigBundle = {
        ConfigBundle(
            getClass,
            Seq(
                minEpochLongSeconds,
                maxEpochLongSeconds,
                dateRangeYears,
                dateStrategy
            )
        )
    }

    override def name: String = "DateTime"

    override def values: Set[AnnotationField[_, _]] = Set(
        AnnotationField("minEpochLongSeconds", minEpochLongSeconds, classOf[Long], classOf[Long]),
        AnnotationField("maxEpochLongSeconds", maxEpochLongSeconds, classOf[Long], classOf[Long]),
        AnnotationField("dateYears", dateRangeYears, classOf[Range], classOf[String], Some((r: String) => {
            val rs = r.split("\\.\\.")
            logger.debug("Initial value: {}, Split value: {}", r.asInstanceOf[Any], rs)
            Range.apply(Integer.parseInt(rs(0)), Integer.parseInt(rs(1)))
        }))
    )

    override def classes: Array[Class[_]] = Array(classOf[java.util.Date])
}
class LocalDateTimeRandomizer extends ClassRandomGenerator[LocalDateTime]
    with Configurable with Logging {
    val dt = new DateTimeRandomizer
    override def apply(rc: RandomContext): LocalDateTime = dt(rc)

    override def configBundle: ConfigBundle = ConfigBundle(getClass, Seq.empty)

    implicit def dateToLocalDateTime(date: Date): java.time.LocalDateTime = {
        LocalDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())
    }

    override def classes: Array[Class[_]] = Array(classOf[java.time.LocalDateTime])
}