package com.example.domain.model

data class EmiResult(
    val monthlyEmi: Double,
    val principalAmount: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val interestPercentage: Float,
    val principalPercentage: Float,
    val processingFee: Double,
    val prepaymentAmount: Double,
    val formattedEndDate: String,
    val tenureMonths: Int,
    val annualRate: Double
)
