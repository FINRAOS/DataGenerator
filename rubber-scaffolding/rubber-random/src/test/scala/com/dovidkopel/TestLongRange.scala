package com.dovidkopel

import java.util.Date

import com.fasterxml.jackson.annotation.JsonFormat
import org.finra.datagenerator.scaffolding.random.support.annotations.{CustomRandomizer, DateTime, LongRange}
import org.finra.datagenerator.scaffolding.random.{BarStringRandomizer, FooStringRandomizer}




/**
  * Created by dkopel on 1/5/17.
  */
class TestLongRange {
    @LongRange(min = 10, max = 20)
    private var amount: Long = 0L

    @LongRange(min = 50, max = 60)
    private var wager: Long = 5L

    @DateTime(dateYears = "-5..2")
    private var birthday: Date = null

    @CustomRandomizer(classOf[FooStringRandomizer])
    private var custom: String = null

    @CustomRandomizer(value=classOf[BarStringRandomizer])
    private var another: String = null

    def getAmount: Long = {
        return amount
    }

    def getWager: Long = {
        return wager
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss") def getBirthday: Date = {
        return birthday
    }

    def getCustom: String = {
        return custom
    }

    def getAnother: String = {
        return another
    }
}