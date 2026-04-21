package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun ProteinScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    var weightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    var selectedGoal by remember { mutableStateOf("muscle-gain") }

    var results by remember { mutableStateOf<ProteinResults?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val goals = listOf(
        ProteinGoal("Sedentary (No exercise)", "sedentary", 0.8, 1.0),
        ProteinGoal("Active (General exercise)", "active", 1.2, 1.6),
        ProteinGoal("Muscle Gain (Build muscle)", "muscle-gain", 1.6, 2.2),
        ProteinGoal("Fat Loss (Lose fat / Preserve muscle)", "fat-loss", 1.8, 2.4)
    )

    var showGoalDialog by remember { mutableStateOf(false) }

    fun calculate() {
        errorMessage = null
        val w = weightInput.toDoubleOrNull() ?: return
        if (w <= 0) {
            errorMessage = "Please enter a valid weight."
            return
        }

        val goalData = goals.find { it.id == selectedGoal }!!
        val lowIntake = w * goalData.lowMult
        val highIntake = w * goalData.highMult

        results = ProteinResults(lowIntake, highIntake)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Protein Calculator") },
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
                    "Daily Protein Requirement Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = goals.find { it.id == selectedGoal }?.name ?: "",
                    onValueChange = {},
                    label = { Text("Goal / Activity Level") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGoalDialog = true },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
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

            results?.let { res ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Daily Protein Requirement is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.0f - %.0f", res.low, res.high),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("grams / day", style = MaterialTheme.typography.titleMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Protein",
                                        inputs = "{\"weight\":$weightInput, \"goal\":\"$selectedGoal\"}",
                                        result = "{\"min\":${res.low.toInt()}, \"max\":${res.high.toInt()}}"
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

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            confirmButton = {},
            title = { Text("Select Goal") },
            text = {
                Column {
                    goals.forEach { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            modifier = Modifier.clickable {
                                selectedGoal = item.id
                                showGoalDialog = false
                            }
                        )
                    }
                }
            }
        )
    }
}

data class ProteinGoal(val name: String, val id: String, val lowMult: Double, val highMult: Double)
data class ProteinResults(val low: Double, val high: Double)
