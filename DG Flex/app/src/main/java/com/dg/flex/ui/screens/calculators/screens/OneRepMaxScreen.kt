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
fun OneRepMaxScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var weightInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("") }
    
    val imperialSystem by viewModel.imperialSystem.collectAsState(initial = false)
    val unit = if (imperialSystem) "lbs" else "kg"

    var resultOneRM by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val w = weightInput.toDoubleOrNull() ?: return
        val r = repsInput.toIntOrNull() ?: return

        if (w <= 0 || r <= 0) {
            errorMessage = "Please enter numbers greater than 0."
            return
        }
        if (r > 12) {
            errorMessage = "For better accuracy, keep reps under 12."
            return
        }
        if (r == 1) {
            resultOneRM = w
            return
        }

        // Epley Formula: 1RM = w * (1 + r/30)
        resultOneRM = w * (1 + (r / 30.0))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1RM Calculator") },
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
                    "Estimated 1RM Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight Lifted ($unit)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = { repsInput = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
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

            resultOneRM?.let { oneRM ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Estimated 1RM is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.1f", oneRM),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(unit, style = MaterialTheme.typography.titleMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "1RM",
                                        inputs = "{\"weight\":$weightInput, \"reps\":$repsInput, \"unit\":\"$unit\"}",
                                        result = "{\"onerm\":${String.format(Locale.US, "%.1f", oneRM)}}"
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

                item {
                    Text(
                        "Training Weights (% of 1RM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val percentages = listOf(95, 90, 85, 80, 75, 70, 65, 60)
                items(percentages.size) { index ->
                    val p = percentages[index]
                    val calculatedWeight = oneRM * (p / 100.0)
                    TrainingWeightRow(p, calculatedWeight, unit)
                    if (index < percentages.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingWeightRow(percent: Int, weight: Double, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$percent%", style = MaterialTheme.typography.bodyMedium)
        Text(
            String.format(Locale.US, "%.1f %s", weight, unit),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
