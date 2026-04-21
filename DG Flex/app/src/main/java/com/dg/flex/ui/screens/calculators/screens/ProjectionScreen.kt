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
import kotlin.math.abs
import kotlin.math.roundToInt

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectionScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    
    var currentWeightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    var goalWeightInput by remember { mutableStateOf("") }
    var tdeeInput by remember { mutableStateOf("") }
    var planCaloriesInput by remember { mutableStateOf("") }

    var resultDays by remember { mutableStateOf<Int?>(null) }
    var goalType by remember { mutableStateOf("") }
    var weightDiff by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val current = currentWeightInput.toDoubleOrNull() ?: return
        val goal = goalWeightInput.toDoubleOrNull() ?: return
        val tdee = tdeeInput.toDoubleOrNull() ?: return
        val plan = planCaloriesInput.toDoubleOrNull() ?: return

        if (current <= 0 || goal <= 0 || tdee <= 0 || plan <= 0) {
            errorMessage = "Please enter numbers greater than 0."
            return
        }
        if (current == goal) {
            errorMessage = "Current and goal weights are the same."
            return
        }

        val dailyDiff = tdee - plan
        
        if ((goal < current && dailyDiff <= 0) || (goal > current && dailyDiff >= 0)) {
            errorMessage = "Your plan does not match your goal (e.g., trying to lose weight while eating over TDEE)."
            return
        }

        weightDiff = abs(current - goal)
        val totalCalorieToChange = weightDiff * 7700.0 // 7700 kcal per kg
        val days = totalCalorieToChange / abs(dailyDiff)
        
        resultDays = days.roundToInt()
        goalType = if (goal < current) "lose" else "gain"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projection Calculator") },
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
                    "Weight Projection Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentWeightInput,
                        onValueChange = { currentWeightInput = it },
                        label = { Text("Current Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = goalWeightInput,
                        onValueChange = { goalWeightInput = it },
                        label = { Text("Goal Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = tdeeInput,
                    onValueChange = { tdeeInput = it },
                    label = { Text("Daily Burn (TDEE)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = planCaloriesInput,
                    onValueChange = { planCaloriesInput = it },
                    label = { Text("Daily Calorie Intake Plan") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
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

            resultDays?.let { days ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "To ${goalType} ${String.format(Locale.US, "%.1f", weightDiff)} kg, it will take about",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$days",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("days", style = MaterialTheme.typography.titleMedium)
                            
                            val months = days / 30
                            val remainingDays = days % 30
                            if (months > 0) {
                                Text(
                                    "(approx. $months months and $remainingDays days)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Projection",
                                        inputs = "{\"current\":$currentWeightInput, \"goal\":$goalWeightInput, \"tdee\":$tdeeInput, \"plan\":$planCaloriesInput}",
                                        result = "{\"days\":$days, \"type\":\"$goalType\", \"weight\":$weightDiff}"
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
