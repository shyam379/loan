package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.domain.model.SavedLoan
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLoanDao {

    @Query("SELECT * FROM saved_loans ORDER BY savedAt DESC")
    fun getAllLoans(): Flow<List<SavedLoan>>

    @Query("SELECT * FROM saved_loans WHERE id = :id")
    suspend fun getLoanById(id: Int): SavedLoan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: SavedLoan): Long

    @Delete
    suspend fun deleteLoan(loan: SavedLoan)

    @Query("DELETE FROM saved_loans")
    suspend fun clearAllLoans()
}
