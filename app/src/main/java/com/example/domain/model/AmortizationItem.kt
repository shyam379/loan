package com.example.domain.model

data class AmortizationItem(
    val monthNumber: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)
