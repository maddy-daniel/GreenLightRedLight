package com.mads.greenlightredlight

object TaxCalculator{
    fun calculateFederalTax(weeklyGross: Double): Double{
        return when{
            weeklyGross <= 223.08 -> weeklyGross * 0.10
            weeklyGross <= 906.73 -> weeklyGross * 0.12
            weeklyGross <= 1933.17 -> weeklyGross * 0.22
            weeklyGross <= 3691.35 -> weeklyGross * 0.24
            else -> weeklyGross * 0.32
        }
    }

    fun calculateSocialSecurity(weeklyGross: Double): Double{
        return weeklyGross * 0.062
    }

    fun calculateMedicare(weeklyGross: Double): Double{
        return weeklyGross * 0.0145
    }

    fun calculateNetTakeHome(weeklyGross: Double): Double{
        val federalTax = calculateFederalTax(weeklyGross)
        val socialSecurity = calculateSocialSecurity(weeklyGross)
        val medicare = calculateMedicare(weeklyGross)
        return weeklyGross - federalTax - socialSecurity - medicare
    }
}