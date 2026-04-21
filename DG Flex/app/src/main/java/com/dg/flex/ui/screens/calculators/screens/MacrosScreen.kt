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
fun MacrosScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var caloriesInput by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("balanced") }
    
    var customCarbs by remember { mutableStateOf("40") }
    var customProtein by remember { mutableStateOf("30") }
    var customFat by remember { mutableStateOf("30") }

    var results by remember { mutableStateOf<MacroResults?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val goals = listOf(
        MacroGoal("Balanced (40/30/30)", "balanced", 40, 30, 30),
        MacroGoal("Low Carb (25/45/30)", "low-carb", 25, 45, 30),
        MacroGoal("High Protein (30/40/30)", "high-protein", 30, 40, 30),
        MacroGoal("Custom", "custom", 0, 0, 0)
    )

    var showGoalDialog by remember { mutableStateOf(false) }

    fun calculate() {
        errorMessage = null
        val totalCalories = caloriesInput.toDoubleOrNull() ?: return
        if (totalCalories <= 0) {
            errorMessage = "Please enter a valid total calorie value."
            return
        }

        val carbsP: Double
        val proteinP: Double
        val fatP: Double

        if (selectedGoal == "custom") {
            carbsP = customCarbs.toDoubleOrNull() ?: 0.0
            proteinP = customProtein.toDoubleOrNull() ?: 0.0
            fatP = customFat.toDoubleOrNull() ?: 0.0
            
            if (carbsP + proteinP + fatP != 100.0) {
                errorMessage = "Custom ratios must total 100%."
                return
            }
        } else {
            val goal = goals.find { it.id == selectedGoal }!!
            carbsP = goal.carbsP.toDouble()
            proteinP = goal.proteinP.toDouble()
            fatP = goal.fatP.toDouble()
        }

        val pGrams = (totalCalories * (proteinP / 100.0)) / 4.0
        val cGrams = (totalCalories * (carbsP / 100.0)) / 4.0
        val fGrams = (totalCalories * (fatP / 100.0)) / 9.0

        results = MacroResults(
            totalCalories.toInt(),
            proteinP.toInt(), proteinP.toInt() * totalCalories.toInt() / 100, pGrams,
            carbsP.toInt(), carbsP.toInt() * totalCalories.toInt() / 100, cGrams,
            fatP.toInt(), fatP.toInt() * totalCalories.toInt() / 100, fGrams
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macro Calculator") },
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
                    "Macronutrient Calculator (Macros)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = caloriesInput,
                    onValueChange = { caloriesInput = it },
                    label = { Text("Total Calories Goal (kcal)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = goals.find { it.id == selectedGoal }?.name ?: "",
                    onValueChange = {},
                    label = { Text("Macronutrient Ratio Type") },
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

            if (selectedGoal == "custom") {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customProtein,
                            onValueChange = { customProtein = it },
                            label = { Text("Protein %") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = customCarbs,
                            onValueChange = { customCarbs = it },
                            label = { Text("Carbs %") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = customFat,
                            onValueChange = { customFat = it },
                            label = { Text("Fat %") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

            results?.let { res ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Results for ${res.totalCalories} kcal", style = MaterialTheme.typography.titleMedium)
                            
                            MacroRow("Protein", res.proteinP, res.proteinC, res.proteinG, MaterialTheme.colorScheme.primary)
                            MacroRow("Carbohydrates", res.carbsP, res.carbsC, res.carbsG, MaterialTheme.colorScheme.tertiary)
                            MacroRow("Fats", res.fatP, res.fatC, res.fatG, MaterialTheme.colorScheme.secondary)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Macros",
                                        inputs = "{\"calories\":$caloriesInput, \"goal\":\"$selectedGoal\"}",
                                        result = "{\"p\":${res.proteinG.toInt()}, \"c\":${res.carbsG.toInt()}, \"f\":${res.fatG.toInt()}}"
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
            title = { Text("Select Ratio Type") },
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

data class MacroGoal(val name: String, val id: String, val carbsP: Int, val proteinP: Int, val fatP: Int)

data class MacroResults(
    val totalCalories: Int,
    val proteinP: Int, val proteinC: Int, val proteinG: Double,
    val carbsP: Int, val carbsC: Int, val carbsG: Double,
    val fatP: Int, val fatC: Int, val fatG: Double
)

@Composable
fun MacroRow(label: String, percent: Int, kcal: Int, grams: Double, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("$percent%", style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                String.format(Locale.US, "%.0f g", grams),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text("$kcal kcal", style = MaterialTheme.typography.labelSmall)
        }
    }
}
