package com.example.domain.repository

import com.example.domain.model.SavedLoan
import kotlinx.coroutines.flow.Flow

interface LoanRepository {
    fun getAllLoans(): Flow<List<SavedLoan>>
    suspend fun getLoanById(id: Int): SavedLoan?
    suspend fun insertLoan(loan: SavedLoan): Long
    suspend fun deleteLoan(loan: SavedLoan)
    suspend fun clearAllLoans()

    fun isOnboarded(): Flow<Boolean>
    fun getThemePreference(): Flow<String>
    fun getCurrencyPreference(): Flow<String>
    suspend fun setOnboarded(completed: Boolean)
    suspend fun setTheme(theme: String)
    suspend fun setCurrency(currency: String)
    suspend fun clearSettings()
}
