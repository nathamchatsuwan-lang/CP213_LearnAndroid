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
import kotlin.math.roundToInt

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefeedScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var tdeeInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }

    var carbResult by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val tdee = tdeeInput.toDoubleOrNull() ?: return
        val p = proteinInput.toDoubleOrNull() ?: return
        val f = fatInput.toDoubleOrNull() ?: return

        if (tdee <= 0 || p <= 0 || f <= 0) {
            errorMessage = "Please enter valid information (numbers > 0)."
            return
        }

        val remainingCalories = tdee - (p * 4.0) - (f * 9.0)
        if (remainingCalories < 0) {
            errorMessage = "Calories from protein and fat exceed TDEE. Cannot calculate carbohydrates."
            return
        }

        carbResult = (remainingCalories / 4.0).roundToInt()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refeed Day Calculator") },
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
                    "Refeed Day Macro Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = tdeeInput,
                    onValueChange = { tdeeInput = it },
                    label = { Text("Calorie Goal (Recommended: TDEE)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = proteinInput,
                        onValueChange = { proteinInput = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatInput,
                        onValueChange = { fatInput = it },
                        label = { Text("Fat (g)") },
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
                    Text("Calculate Required Carbohydrates")
                }
            }

            carbResult?.let { carbs ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Carbohydrates to consume", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "$carbs",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text("grams", style = MaterialTheme.typography.titleMedium)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Text("Refeed Day Macro Summary", style = MaterialTheme.typography.labelSmall)
                            ResultLabelValue("Total Calories", "${tdeeInput} kcal")
                            ResultLabelValue("Protein", "${proteinInput} g")
                            ResultLabelValue("Carbohydrates", "$carbs g", isBold = true)
                            ResultLabelValue("Fats", "${fatInput} g")
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Refeed",
                                        inputs = "{\"tdee\":$tdeeInput, \"p\":$proteinInput, \"f\":$fatInput}",
                                        result = "{\"carbs\":$carbs}"
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
