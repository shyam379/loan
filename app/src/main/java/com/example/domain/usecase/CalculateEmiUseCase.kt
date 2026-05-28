package com.example.domain.usecase

import com.example.domain.model.EmiResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

class CalculateEmiUseCase {

    operator fun invoke(
        loanAmount: Double,
        annualInterestRate: Double,
        tenure: Int,
        tenureType: String, // "Years" or "Months"
        processingFee: Double = 0.0,
        prepaymentAmount: Double = 0.0,
        startDateString: String = ""
    ): EmiResult {
        // 1. Calculate tenure in months (N)
        val tenureMonths = if (tenureType == "Years") tenure * 12 else tenure
        if (tenureMonths <= 0 || loanAmount <= 0.0) {
            return EmiResult(0.0, loanAmount, 0.0, loanAmount, 0f, 100f, processingFee, prepaymentAmount, "-", tenureMonths, annualInterestRate)
        }

        val principalAfterPrepayment = (loanAmount - prepaymentAmount).coerceAtLeast(0.0)

        // 2. Compute Monthly EMI
        val monthlyEmi: Double
        val totalPayment: Double
        val totalInterest: Double

        if (annualInterestRate <= 0.0) {
            // Zero interest loan handling
            monthlyEmi = principalAfterPrepayment / tenureMonths
            totalPayment = principalAfterPrepayment
            totalInterest = 0.0
        } else {
            val monthlyRate = (annualInterestRate / 12.0) / 100.0
            val numerator = principalAfterPrepayment * monthlyRate * (1.0 + monthlyRate).pow(tenureMonths)
            val denominator = (1.0 + monthlyRate).pow(tenureMonths) - 1.0
            
            monthlyEmi = if (denominator != 0.0) numerator / denominator else 0.0
            totalPayment = monthlyEmi * tenureMonths
            totalInterest = (totalPayment - principalAfterPrepayment).coerceAtLeast(0.0)
        }

        // 3. Compute breakdown percentages
        val totalCost = principalAfterPrepayment + totalInterest
        val principalPercentage = if (totalCost > 0) ((principalAfterPrepayment / totalCost) * 100.0).toFloat() else 100f
        val interestPercentage = if (totalCost > 0) ((totalInterest / totalCost) * 100.0).toFloat() else 0f

        // 4. Calculate End Date
        val formattedEndDate = calculateEndDate(startDateString, tenureMonths)

        return EmiResult(
            monthlyEmi = roundToTwoDecimals(monthlyEmi),
            principalAmount = roundToTwoDecimals(loanAmount),
            totalInterest = roundToTwoDecimals(totalInterest),
            totalPayment = roundToTwoDecimals(totalPayment + processingFee),
            interestPercentage = interestPercentage,
            principalPercentage = principalPercentage,
            processingFee = roundToTwoDecimals(processingFee),
            prepaymentAmount = roundToTwoDecimals(prepaymentAmount),
            formattedEndDate = formattedEndDate,
            tenureMonths = tenureMonths,
            annualRate = annualInterestRate
        )
    }

    private fun calculateEndDate(startDateString: String, monthsToAdd: Int): String {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        if (startDateString.isNotEmpty()) {
            try {
                format.parse(startDateString)?.let {
                    calendar.time = it
                }
            } catch (e: Exception) {
                // Keep current calendar if parsing fails
            }
        }
        
        calendar.add(Calendar.MONTH, monthsToAdd)
        val outFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return outFormat.format(calendar.time)
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return kotlin.math.round(value * 100.0) / 100.0
    }
}
