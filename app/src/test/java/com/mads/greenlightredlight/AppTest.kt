package com.mads.greenlightredlight

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class RolloverTest{
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun getMostRecentSunday(date:LocalDate):LocalDate{
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }

    private fun shouldRollover(today:LocalDate, lastRolloverStr:String?):Boolean{
        val mostRecentSunday = getMostRecentSunday(today)
        return if(lastRolloverStr == null){
            true
        }
        else{
            val lastRollover = LocalDate.parse(lastRolloverStr, dateFormatter)
            lastRollover.isBefore(mostRecentSunday)
        }
    }

    @Test
    //Tests when the rollover should happen when there wasn't a previous rollover date that existed
    fun rolloverShouldHappenWhenNoPastRolloverDateExist(){
        val today = LocalDate.now()
        val result = shouldRollover(today, null)
        assertTrue("Rollover should happen when no previous rollover date exists", result)

    }

    @Test
    //Tests when the rollover should not happen when the last rollover was this week
    fun rolloverShouldNotHappenWhenLastRolloverWasThisWeek(){
        val today = LocalDate.now()
        val lastWeekSunday = getMostRecentSunday(today)
        val lastRolloverStr = lastWeekSunday.format(dateFormatter)
        val result = shouldRollover(today, lastRolloverStr)
        assertTrue("Rollover should NOT happen when last rollover was this Sunday", result)
    }

    @Test
    //Tests when rollover should happen when the last rollover was last week
    fun rolloverShouldHappenWhenLastRolloverWasLastWeek(){
        val today = LocalDate.now()
        val lastWeekSunday = getMostRecentSunday(today).minusWeeks(1)


    }
}