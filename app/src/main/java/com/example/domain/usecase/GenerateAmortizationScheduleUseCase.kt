package com.example.domain.usecase

import com.example.domain.model.AmortizationItem
import kotlin.math.round

class GenerateAmortizationScheduleUseCase {

    operator fun invoke(
        loanAmount: Double,
        annualInterestRate: Double,
        tenureMonths: Int,
        prepaymentAmount: Double = 0.0,
        monthlyEmiCalculated: Double
    ): List<AmortizationItem> {
        val schedule = mutableListOf<AmortizationItem>()
        if (tenureMonths <= 0 || loanAmount <= 0.0) return schedule

        var remainingBalance = (loanAmount - prepaymentAmount).coerceAtLeast(0.0)
        val monthlyRate = (annualInterestRate / 12.0) / 100.0

        for (month in 1..tenureMonths) {
            if (remainingBalance <= 0.0) {
                break
            }

            val interestPaid = if (annualInterestRate > 0.0) {
                remainingBalance * monthlyRate
            } else {
                0.0
            }

            var emi = monthlyEmiCalculated
            var principalPaid = emi - interestPaid

            // For the last month or if the remaining balance is less than the principal paid
            if (remainingBalance < principalPaid || month == tenureMonths) {
                principalPaid = remainingBalance
                emi = principalPaid + interestPaid
                remainingBalance = 0.0
            } else {
                remainingBalance -= principalPaid
            }

            schedule.add(
                AmortizationItem(
                    monthNumber = month,
                    emi = roundToTwoDecimals(emi),
                    principalPaid = roundToTwoDecimals(principalPaid),
                    interestPaid = roundToTwoDecimals(interestPaid),
                    remainingBalance = roundToTwoDecimals(remainingBalance)
                )
            )
        }

        return schedule
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}
