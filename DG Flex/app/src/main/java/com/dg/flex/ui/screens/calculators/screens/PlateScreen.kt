package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
fun PlateScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val imperialSystem by viewModel.imperialSystem.collectAsState(initial = false)
    var unit by remember(imperialSystem) { mutableStateOf(if (imperialSystem) "lbs" else "kg") }
    
    val kgPlates = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25, 0.5)
    val lbsPlates = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5, 1.0)
    
    val kgBars = listOf(BarOption("Standard Olympic (20 kg)", 20.0), BarOption("Women's Olympic (15 kg)", 15.0))
    val lbsBars = listOf(BarOption("Standard Olympic (45 lbs)", 45.0), BarOption("Women's Olympic (35 lbs)", 35.0))

    var targetWeightInput by remember { mutableStateOf("") }
    var selectedBarWeight by remember(unit) { mutableStateOf(if (unit == "kg") 20.0 else 45.0) }
    var availablePlates by remember(unit) { mutableStateOf(if (unit == "kg") kgPlates else lbsPlates) }
    
    var platesForOneSide by remember { mutableStateOf<List<Double>?>(null) }
    var remainder by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        platesForOneSide = null
        remainder = 0.0
        
        val target = targetWeightInput.toDoubleOrNull() ?: return
        if (target <= 0) {
            errorMessage = "Please enter target weight."
            return
        }
        if (target < selectedBarWeight) {
            errorMessage = "Target weight must be at least the bar weight."
            return
        }

        var weightNeededPerSide = (target - selectedBarWeight) / 2.0
        val result = mutableListOf<Double>()
        
        val sortedPlates = availablePlates.sortedDescending()
        for (plate in sortedPlates) {
            while (weightNeededPerSide >= plate - 0.001) { // Small tolerance for float errors
                result.add(plate)
                weightNeededPerSide -= plate
            }
        }
        
        platesForOneSide = result
        remainder = weightNeededPerSide
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plate Calculator") },
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
                    "Plate Loading Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = unit == "kg",
                        onClick = { unit = "kg" },
                        label = { Text("Metric (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = unit == "lbs",
                        onClick = { unit = "lbs" },
                        label = { Text("Imperial (lbs)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = targetWeightInput,
                    onValueChange = { targetWeightInput = it },
                    label = { Text("Total Weight ($unit)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            item {
                Text("Bar Weight", style = MaterialTheme.typography.labelMedium)
                val bars = if (unit == "kg") kgBars else lbsBars
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    bars.forEach { bar ->
                        FilterChip(
                            selected = selectedBarWeight == bar.weight,
                            onClick = { selectedBarWeight = bar.weight },
                            label = { Text(bar.name) },
                            modifier = Modifier.weight(1f)
                        )
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

            platesForOneSide?.let { sidePlates ->
                item {
                    if (remainder > 0.01) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                "Cannot load exactly (remainder of ${String.format(Locale.US, "%.2f", remainder)} $unit per side)",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Plates Per Side", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (sidePlates.isEmpty()) {
                                Text("Empty Bar", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            } else {
                                sidePlates.forEach { plate ->
                                    PlateItem(plate, unit)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Plate",
                                        inputs = "{\"target\":$targetWeightInput, \"bar\":$selectedBarWeight, \"unit\":\"$unit\"}",
                                        result = "{\"plates\":${sidePlates.joinToString(",")}}"
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

data class BarOption(val name: String, val weight: Double)

@Composable
fun PlateItem(plate: Double, unit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            "$plate $unit",
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
