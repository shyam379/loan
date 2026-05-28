package com.example.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_loans")
data class SavedLoan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val loanAmount: Double,
    val interestRate: Double,
    val tenure: Int,
    val tenureType: String, // "Years" or "Months"
    val emi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val processingFee: Double,
    val prepaymentAmount: Double = 0.0,
    val startDate: String = "",
    val savedAt: Long = System.currentTimeMillis()
)
