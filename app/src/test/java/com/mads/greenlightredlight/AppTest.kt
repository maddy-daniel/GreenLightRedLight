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
        assertFalse("Rollover should NOT happen when last rollover was this Sunday", result)
    }

    @Test
    //Tests when rollover should happen when the last rollover was last week
    fun rolloverShouldHappenWhenLastRolloverWasLastWeek(){
        val today = LocalDate.now()
        val lastWeekSunday = getMostRecentSunday(today).minusWeeks(1)
        val lastRolloverStr = lastWeekSunday.format(dateFormatter)
        val result = shouldRollover(today, lastRolloverStr)
        assertTrue("Rollover should happen when last rollover was last week",result)
    }

    @Test
    //Tests when rollover should happen when the last rollover was months ago
    fun rolloverShouldHappenWhenLastRolloverWasMonthsAgo(){
        val today = LocalDate.now()
        val monthsAgo = today.minusMonths(3)
        val lastRolloverStr = monthsAgo.format(dateFormatter)
        val result = shouldRollover(today, lastRolloverStr)
        assertTrue("Rollover should happen when last rollover was months ago", result)
    }

    @Test
    //Tests when rollover should not happen when the rollover was today
    fun rolloverShouldNotHappenWhenLastRolloverWasToday(){
        val today = LocalDate.now()
        val lastRolloverStr = today.format(dateFormatter)
        val result = shouldRollover(today, lastRolloverStr)
        assertFalse("Rollover should NOT happen when last rollover was today.", result)
    }

    @Test
    //Tests that the recent Sunday is correct based on a given date
    fun mostRecentSundayCalculationCorrect(){
        val monday = LocalDate.of(2026,6,1)
        val expectedSunday = LocalDate.of(2026,5,31)
        val result = getMostRecentSunday(monday)
        assertEquals("Most recent Sunday from Monday, June 1st should be May 31st", expectedSunday, result)
    }

    @Test
    //Tests that if the date is a Sunday then it should be the new Recent Sunday
    fun mostRecentSundayWhenTodayIsSunday(){
        val sunday = LocalDate.of(2026,6,7)
        val result = getMostRecentSunday(sunday)
        assertEquals("Most recent Sunday when today is Sunday should return today's date", sunday, result)
    }


}