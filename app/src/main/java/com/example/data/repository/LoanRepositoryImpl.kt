package com.example.data.repository

import com.example.data.local.SavedLoanDao
import com.example.data.local.SettingsManager
import com.example.domain.model.SavedLoan
import com.example.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow

class LoanRepositoryImpl(
    private val savedLoanDao: SavedLoanDao,
    private val settingsManager: SettingsManager
) : LoanRepository {

    override fun getAllLoans(): Flow<List<SavedLoan>> {
        return savedLoanDao.getAllLoans()
    }

    override suspend fun getLoanById(id: Int): SavedLoan? {
        return savedLoanDao.getLoanById(id)
    }

    override suspend fun insertLoan(loan: SavedLoan): Long {
        return savedLoanDao.insertLoan(loan)
    }

    override suspend fun deleteLoan(loan: SavedLoan) {
        savedLoanDao.deleteLoan(loan)
    }

    override suspend fun clearAllLoans() {
        savedLoanDao.clearAllLoans()
    }

    override fun isOnboarded(): Flow<Boolean> {
        return settingsManager.isOnboarded
    }

    override fun getThemePreference(): Flow<String> {
        return settingsManager.themePreference
    }

    override fun getCurrencyPreference(): Flow<String> {
        return settingsManager.currencyPreference
    }

    override suspend fun setOnboarded(completed: Boolean) {
        settingsManager.setOnboarded(completed)
    }

    override suspend fun setTheme(theme: String) {
        settingsManager.setTheme(theme)
    }

    override suspend fun setCurrency(currency: String) {
        settingsManager.setCurrency(currency)
    }

    override suspend fun clearSettings() {
        settingsManager.clearAll()
    }
}
