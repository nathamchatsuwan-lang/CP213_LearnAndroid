package com.dg.flex.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "calculator_results")
data class CalculatorResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculatorType: String,
    val date: ZonedDateTime,
    val inputs: String, // JSON string of inputs
    val result: String  // JSON string of results
)
