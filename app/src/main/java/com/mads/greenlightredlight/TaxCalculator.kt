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

    //FICA Taxes
    fun calculateSocialSecurity(weeklyGross: Double): Double{
        return weeklyGross * 0.062
    }
    fun calculateMedicare(weeklyGross: Double): Double{
        return weeklyGross * 0.0145
    }

    //NJ State Tax (2026 weekly brackets)
    fun calculateNJStateTax(weeklyGross: Double): Double{
        return when{
            weeklyGross <= 384.62 -> weeklyGross * 0.014
            weeklyGross <= 673.08 -> weeklyGross * 0.0175
            weeklyGross <= 769.23 -> weeklyGross * 0.035
            weeklyGross <= 1142.31 -> weeklyGross * 0.05525
            weeklyGross <= 9615.38 -> weeklyGross * 0.0637
            weeklyGross <= 19230.77 -> weeklyGross * 0.0897
            else -> weeklyGross * 0.1075
        }
    }

    //NJ State Insurance Taxes
    fun calculateSDI(weeklyGross: Double): Double = weeklyGross * 0.0019
    fun calculateSUI(weeklyGross: Double): Double = weeklyGross * 0.0043
    fun calculateFLI(weeklyGross: Double): Double = weeklyGross * 0.0009

    //Net Take-Home: All deductions combined
    fun calculateNetTakeHome(weeklyGross: Double): Double{
        val federalTax = calculateFederalTax(weeklyGross)
        val socialSecurity = calculateSocialSecurity(weeklyGross)
        val medicare = calculateMedicare(weeklyGross)
        val njStateTax = calculateNJStateTax(weeklyGross)
        val sdi = calculateSDI(weeklyGross)
        val sui = calculateSUI(weeklyGross)
        val fli = calculateFLI(weeklyGross)
        return weeklyGross - federalTax - socialSecurity - medicare - njStateTax - sdi - sui - fli
    }
}