package com.dg.flex.data.db.dao

import androidx.room.*
import com.dg.flex.data.db.entity.CalculatorResult
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculator_results ORDER BY date DESC")
    fun getAllResults(): Flow<List<CalculatorResult>>

    @Query("SELECT * FROM calculator_results WHERE calculatorType = :type ORDER BY date DESC")
    fun getResultsByType(type: String): Flow<List<CalculatorResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: CalculatorResult)

    @Delete
    suspend fun deleteResult(result: CalculatorResult)

    @Query("DELETE FROM calculator_results WHERE calculatorType = :type")
    suspend fun deleteResultsByType(type: String)
}
