package com.dg.flex.ui.screens.calculators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dg.flex.data.PreferenceRepository
import com.dg.flex.data.db.dao.CalculatorDao
import com.dg.flex.data.db.entity.CalculatorResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val preferences: PreferenceRepository,
    private val calculatorDao: CalculatorDao
) : ViewModel() {

    val userWeight = preferences.getUserWeight()
    val userHeight = preferences.getUserHeight()
    val imperialSystem = preferences.getImperialSystem()

    fun getAllResults() = calculatorDao.getAllResults()

    fun getResultsByType(type: String) = calculatorDao.getResultsByType(type)

    fun saveResult(type: String, inputs: String, result: String) {
        viewModelScope.launch {
            val calculatorResult = CalculatorResult(
                calculatorType = type,
                date = ZonedDateTime.now(),
                inputs = inputs,
                result = result
            )
            calculatorDao.insertResult(calculatorResult)
        }
    }

    fun deleteResult(result: CalculatorResult) {
        viewModelScope.launch {
            calculatorDao.deleteResult(result)
        }
    }
}
