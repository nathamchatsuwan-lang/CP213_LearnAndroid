package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ProgressionScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var weightInput by remember { mutableStateOf("") }
    var weeksInput by remember { mutableStateOf("") }
    var incrementInput by remember { mutableStateOf("") }
    
    val imperialSystem by viewModel.imperialSystem.collectAsState(initial = false)
    val unit = if (imperialSystem) "lbs" else "kg"

    var planResults by remember { mutableStateOf<List<ProgressionWeek>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val startW = weightInput.toDoubleOrNull() ?: return
        val wks = weeksInput.toIntOrNull() ?: return
        val inc = incrementInput.toDoubleOrNull() ?: return

        if (startW <= 0 || wks <= 0 || inc < 0) {
            errorMessage = "Please enter valid information."
            return
        }
        if (wks > 52) {
            errorMessage = "Plan cannot exceed 52 weeks."
            return
        }

        val results = mutableListOf<ProgressionWeek>()
        for (i in 1..wks) {
            val target = startW + ((i - 1) * inc)
            results.add(ProgressionWeek(i, target))
        }
        planResults = results
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progression Planner") },
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
                    "Weekly Weight Progression Planner",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Starting Weight ($unit)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weeksInput,
                        onValueChange = { weeksInput = it },
                        label = { Text("Weeks") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = incrementInput,
                        onValueChange = { incrementInput = it },
                        label = { Text("Weekly Inc. ($unit)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    Text("Create Progression Plan")
                }
            }

            planResults?.let { results ->
                item {
                    Text(
                        "${results.size}-Week Weight Progression Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Button(
                        onClick = {
                            viewModel.saveResult(
                                type = "Progression",
                                inputs = "{\"start\":$weightInput, \"weeks\":$weeksInput, \"inc\":$incrementInput}",
                                result = "{\"final\":${results.last().weight}}"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save This Plan")
                    }
                }

                items(results) { week ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Week ${week.week}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                String.format(Locale.US, "%.2f %s", week.weight, unit),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ProgressionWeek(val week: Int, val weight: Double)
