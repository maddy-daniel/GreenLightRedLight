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

class TaxCalculatorTest{
    private val delta = 0.001

    @Test
    fun federalTax_10PercentBracket(){
        assertEquals(20.0, TaxCalculator.calculateFederalTax(200.0), delta)
    }

    @Test
    fun federalTax_12PercentBracket(){
        assertEquals(55.2, TaxCalculator.calculateFederalTax(460.0), delta)
    }

    @Test
    fun federalTax_22PercentBracket(){
        assertEquals(220.0, TaxCalculator.calculateFederalTax(1000.0), delta)
    }

    @Test
    fun federalTax_24PercentBracket(){
        assertEquals(480.0, TaxCalculator.calculateFederalTax(2000.0), delta)
    }

    @Test
    fun federalTax_32PercentBracket(){
        assertEquals(1600.0, TaxCalculator.calculateFederalTax(5000.0), delta)
    }

    @Test
    fun socialSecurity_is6Point2Percent(){
        assertEquals(28.52, TaxCalculator.calculateSocialSecurity(460.0), delta)
    }

    @Test
    fun medicare_is1Point45Percent(){
        assertEquals(6.67, TaxCalculator.calculateMedicare(460.0), delta)
    }

    @Test
    fun njStateTax_secondBracket(){
        assertEquals(8.05, TaxCalculator.calculateNJStateTax(460.0), delta)
    }

    @Test
    fun njInsurances_matchExpectedRates(){
        assertEquals(0.874, TaxCalculator.calculateSDI(460.0), delta)
        assertEquals(1.978, TaxCalculator.calculateSUI(460.0), delta)
        assertEquals(0.414, TaxCalculator.calculateFLI(460.0), delta)
    }

    @Test
    fun netTakeHome_for460Gross(){
        assertEquals(358.294, TaxCalculator.calculateNetTakeHome(460.0), delta)
    }

    @Test
    fun netTakeHome_isAlwaysLessThanGross(){
        listOf(50.0, 1000.0, 2500.0, 5000.0).forEach{
            gross->
            assertTrue(TaxCalculator.calculateNetTakeHome(gross)<gross)
        }
    }

    @Test
    fun zeroIncome_producesZeroIncome(){
        assertEquals(0.0, TaxCalculator.calculateNetTakeHome(0.0), delta)
    }
}

class FilterNumericInputTest{
    private fun filterNumericInput(input: String): String {
        val digitsAndDot = input.filter{it.isDigit() || it == '.' }
        val firstDotIndex = digitsAndDot.indexOf('.')
        if(firstDotIndex == -1) return digitsAndDot

        val before = digitsAndDot.substring(0, firstDotIndex+1)
        val after = digitsAndDot.substring(firstDotIndex + 1).replace(".","")
        return before + after
    }

    @Test
    fun comma_isRemoved(){
        assertEquals("1850", filterNumericInput("18,50"))
    }

    @Test
    fun spaces_areRemoved(){
        assertEquals("1600", filterNumericInput("16 00"))
    }

    @Test
    fun multipleDecimalPoints_keepsOnlyTheFirst(){
        assertEquals("18.50", filterNumericInput("18..50"))
        assertEquals("1.23", filterNumericInput("1.2.3"))
    }

    @Test
    fun letters_areRemoved(){
        assertEquals("", filterNumericInput("abc"))
        assertEquals("12", filterNumericInput("1a2b"))
    }
}