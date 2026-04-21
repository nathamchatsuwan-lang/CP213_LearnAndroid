package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.clickable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.dg.flex.ui.screens.calculators.CalculatorViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaceScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var calcMode by remember { mutableStateOf("pace") } // pace, time, dist
    
    var distInput by remember { mutableStateOf("") }
    var distUnit by remember { mutableStateOf("km") }
    
    var timeHrInput by remember { mutableStateOf("") }
    var timeMinInput by remember { mutableStateOf("") }
    var timeSecInput by remember { mutableStateOf("") }
    
    var paceMinInput by remember { mutableStateOf("") }
    var paceSecInput by remember { mutableStateOf("") }
    var paceUnit by remember { mutableStateOf("min/km") }

    var resultValue by remember { mutableStateOf("") }
    var resultLabel by remember { mutableStateOf("") }
    var resultSubLabel by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val MILE_TO_KM = 1.60934

    fun calculate() {
        errorMessage = null
        try {
            when (calcMode) {
                "pace" -> {
                    val dist = distInput.toDoubleOrNull() ?: throw Exception("Please enter distance")
                    val hr = timeHrInput.toDoubleOrNull() ?: 0.0
                    val min = timeMinInput.toDoubleOrNull() ?: 0.0
                    val sec = timeSecInput.toDoubleOrNull() ?: 0.0
                    val totalSec = (hr * 3600) + (min * 60) + sec
                    if (totalSec <= 0) throw Exception("Please enter time")
                    
                    val distKm = if (distUnit == "mi") dist * MILE_TO_KM else dist
                    val targetDistKm = if (paceUnit == "min/mi") MILE_TO_KM else 1.0
                    val secPerTarget = (totalSec / distKm) * targetDistKm
                    
                    val pMin = (secPerTarget / 60).toInt()
                    val pSec = (secPerTarget % 60).toInt()
                    resultLabel = "Calculated Pace"
                    resultValue = String.format(Locale.US, "%d:%02d", pMin, pSec)
                    resultSubLabel = paceUnit
                }
                "time" -> {
                    val dist = distInput.toDoubleOrNull() ?: throw Exception("Please enter distance")
                    val pMin = paceMinInput.toDoubleOrNull() ?: 0.0
                    val pSec = paceSecInput.toDoubleOrNull() ?: 0.0
                    val paceTotalSec = (pMin * 60) + pSec
                    if (paceTotalSec <= 0) throw Exception("Please enter pace")
                    
                    val distKm = if (distUnit == "mi") dist * MILE_TO_KM else dist
                    val paceIsPerMi = paceUnit == "min/mi"
                    
                    val totalSec = if (paceIsPerMi) paceTotalSec * (distKm / MILE_TO_KM) else paceTotalSec * distKm
                    
                    val h = (totalSec / 3600).toInt()
                    val m = ((totalSec % 3600) / 60).toInt()
                    val s = (totalSec % 60).toInt()
                    resultLabel = "Calculated Time"
                    resultValue = if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s) else String.format(Locale.US, "%d:%02d", m, s)
                    resultSubLabel = "hr:min:sec"
                }
                "dist" -> {
                    val hr = timeHrInput.toDoubleOrNull() ?: 0.0
                    val min = timeMinInput.toDoubleOrNull() ?: 0.0
                    val sec = timeSecInput.toDoubleOrNull() ?: 0.0
                    val totalTimeSec = (hr * 3600) + (min * 60) + sec
                    
                    val pMin = paceMinInput.toDoubleOrNull() ?: 0.0
                    val pSec = paceSecInput.toDoubleOrNull() ?: 0.0
                    val paceTotalSec = (pMin * 60) + pSec
                    
                    if (totalTimeSec <= 0 || paceTotalSec <= 0) throw Exception("Please fill in all fields")
                    
                    val paceIsPerMi = paceUnit == "min/mi"
                    val distKm = totalTimeSec / (if (paceIsPerMi) paceTotalSec / MILE_TO_KM else paceTotalSec)
                    val finalDist = if (distUnit == "mi") distKm / MILE_TO_KM else distKm
                    
                    resultLabel = "Calculated Distance"
                    resultValue = String.format(Locale.US, "%.2f", finalDist)
                    resultSubLabel = distUnit
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pace Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Pace / Time / Distance Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text("What do you want to calculate?", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = calcMode == "pace", onClick = { calcMode = "pace" }, label = { Text("Pace") }, modifier = Modifier.weight(1f))
                    FilterChip(selected = calcMode == "time", onClick = { calcMode = "time" }, label = { Text("Time") }, modifier = Modifier.weight(1f))
                    FilterChip(selected = calcMode == "dist", onClick = { calcMode = "dist" }, label = { Text("Distance") }, modifier = Modifier.weight(1f))
                }
            }

            // Distance Group
            item {
                Column(modifier = Modifier.alpha(if (calcMode == "dist") 0.5f else 1f)) {
                    Text("Distance", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = distInput,
                            onValueChange = { distInput = it },
                            modifier = Modifier.weight(1f),
                            enabled = calcMode != "dist",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            label = { Text("Enter distance") }
                        )
                        Row(modifier = Modifier.weight(1f)) {
                            RadioButton(selected = distUnit == "km", onClick = { distUnit = "km" }, enabled = calcMode != "dist")
                            Text("km", modifier = Modifier.clickable(enabled = calcMode != "dist") { distUnit = "km" }, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(8.dp))
                            RadioButton(selected = distUnit == "mi", onClick = { distUnit = "mi" }, enabled = calcMode != "dist")
                            Text("mi", modifier = Modifier.clickable(enabled = calcMode != "dist") { distUnit = "mi" }, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Time Group
            item {
                Column(modifier = Modifier.alpha(if (calcMode == "time") 0.5f else 1f)) {
                    Text("Time (hr:min:sec)", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(value = timeHrInput, onValueChange = { timeHrInput = it }, label = { Text("hr.") }, modifier = Modifier.weight(1f), enabled = calcMode != "time", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = timeMinInput, onValueChange = { timeMinInput = it }, label = { Text("min") }, modifier = Modifier.weight(1f), enabled = calcMode != "time", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = timeSecInput, onValueChange = { timeSecInput = it }, label = { Text("sec") }, modifier = Modifier.weight(1f), enabled = calcMode != "time", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
            }

            // Pace Group
            item {
                Column(modifier = Modifier.alpha(if (calcMode == "pace") 0.5f else 1f)) {
                    Text("Pace", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.weight(1.5f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(value = paceMinInput, onValueChange = { paceMinInput = it }, label = { Text("min") }, modifier = Modifier.weight(1f), enabled = calcMode != "pace", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = paceSecInput, onValueChange = { paceSecInput = it }, label = { Text("sec") }, modifier = Modifier.weight(1f), enabled = calcMode != "pace", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = paceUnit == "min/km", onClick = { paceUnit = "min/km" }, enabled = calcMode != "pace")
                                Text("min/km", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = paceUnit == "min/mi", onClick = { paceUnit = "min/mi" }, enabled = calcMode != "pace")
                                Text("min/mi", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            item {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Calculate")
                }
            }

            if (resultValue.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(resultLabel, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                resultValue,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(resultSubLabel, style = MaterialTheme.typography.titleMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Pace",
                                        inputs = "{\"mode\":\"$calcMode\", \"dist\":\"$distInput\", \"time\":\"$timeHrInput:$timeMinInput:$timeSecInput\", \"pace\":\"$paceMinInput:$paceSecInput\"}",
                                        result = "{\"value\":\"$resultValue\", \"label\":\"$resultLabel\"}"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Result")
                            }
                        }
                    }
                }
            }
        }
    }
}
