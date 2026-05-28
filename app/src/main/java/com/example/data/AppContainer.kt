package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsManager
import com.example.data.repository.LoanRepositoryImpl
import com.example.domain.repository.LoanRepository

class AppContainer(private val context: Context) {
    
    private val database by lazy { AppDatabase.getDatabase(context) }
    private val settingsManager by lazy { SettingsManager(context) }
    
    val loanRepository: LoanRepository by lazy {
        LoanRepositoryImpl(
            savedLoanDao = database.savedLoanDao(),
            settingsManager = settingsManager
        )
    }
}
