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
fun IBWScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val heightFromProfile by viewModel.userHeight.collectAsState(initial = 0f)
    var heightInput by remember(heightFromProfile) { mutableStateOf(if (heightFromProfile > 0) heightFromProfile.toString() else "") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }

    var results by remember { mutableStateOf<IBWResults?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val h = heightInput.toDoubleOrNull() ?: return
        if (h <= 0) {
            errorMessage = "Please enter a valid height."
            return
        }

        val heightInInches = h / 2.54
        val inchesOver5Feet = if (heightInInches > 60) heightInInches - 60 else 0.0
        val heightInMeters = h / 100.0

        val hamwi = if (selectedSex == Sex.MALE) 48.0 + (2.7 * inchesOver5Feet) else 45.5 + (2.2 * inchesOver5Feet)
        val robinson = if (selectedSex == Sex.MALE) 52.0 + (1.9 * inchesOver5Feet) else 49.0 + (1.7 * inchesOver5Feet)
        val miller = if (selectedSex == Sex.MALE) 56.2 + (1.41 * inchesOver5Feet) else 53.1 + (1.36 * inchesOver5Feet)
        
        val bmiLow = 18.5 * (heightInMeters * heightInMeters)
        val bmiHigh = 24.9 * (heightInMeters * heightInMeters)

        results = IBWResults(hamwi, robinson, miller, bmiLow, bmiHigh)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ideal Body Weight") },
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
                    "Ideal Body Weight (IBW) Calculator",
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
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("Height (cm)") },
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

            results?.let { res ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IBWRow("Hamwi Formula (1964)", res.hamwi)
                            IBWRow("Robinson Formula (1983)", res.robinson)
                            IBWRow("Miller Formula (1983)", res.miller)
                            
                            HorizontalDivider()
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Healthy Weight Range (BMI 18.5 - 24.9)", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    String.format(Locale.US, "%.1f - %.1f kg", res.bmiLow, res.bmiHigh),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "IBW",
                                        inputs = "{\"height\":$heightInput, \"sex\":\"$selectedSex\"}",
                                        result = "{\"hamwi\":${res.hamwi}, \"robinson\":${res.robinson}, \"miller\":${res.miller}, \"bmiRange\":\"${res.bmiLow}-${res.bmiHigh}\"}"
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

data class IBWResults(val hamwi: Double, val robinson: Double, val miller: Double, val bmiLow: Double, val bmiHigh: Double)

@Composable
fun IBWRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            String.format(Locale.US, "%.1f kg", value),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
