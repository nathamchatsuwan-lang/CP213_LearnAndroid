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
import com.dg.flex.data.db.entity.Sex
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.dg.flex.ui.screens.calculators.CalculatorViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WHRScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var waistInput by remember { mutableStateOf("") }
    var hipInput by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }

    var whrResult by remember { mutableStateOf<Double?>(null) }
    var category by remember { mutableStateOf("") }
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    var categoryColor by remember(onSurfaceColor) { mutableStateOf(onSurfaceColor) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val waist = waistInput.toDoubleOrNull() ?: return
        val hip = hipInput.toDoubleOrNull() ?: return

        if (waist <= 0 || hip <= 0) {
            errorMessage = "Please enter numbers greater than 0."
            return
        }

        val whr = waist / hip
        whrResult = whr

        if (selectedSex == Sex.MALE) {
            if (whr < 0.90) {
                category = "Low Health Risk"
                categoryColor = tertiaryColor
            } else {
                category = "High Health Risk (Abdominal Obesity)"
                categoryColor = primaryColor
            }
        } else {
            if (whr < 0.85) {
                category = "Low Health Risk"
                categoryColor = tertiaryColor
            } else {
                category = "High Health Risk (Abdominal Obesity)"
                categoryColor = primaryColor
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WHR Calculator") },
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
                    "Waist-to-Hip Ratio (WHR) Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSex == Sex.MALE,
                        onClick = { selectedSex = Sex.MALE },
                        label = { Text("Male") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedSex == Sex.FEMALE,
                        onClick = { selectedSex = Sex.FEMALE },
                        label = { Text("Female") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = waistInput,
                    onValueChange = { waistInput = it },
                    label = { Text("Waist (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = hipInput,
                    onValueChange = { hipInput = it },
                    label = { Text("Hip (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

            whrResult?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your WHR is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.2f", result),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                            Text(
                                "Result: $category",
                                style = MaterialTheme.typography.titleMedium,
                                color = categoryColor
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "WHR",
                                        inputs = "{\"waist\":$waistInput, \"hip\":$hipInput, \"sex\":\"$selectedSex\"}",
                                        result = "{\"whr\":${String.format(Locale.US, "%.2f", result)}, \"category\":\"$category\"}"
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
