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
fun CreatineScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    var weightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }

    var results by remember { mutableStateOf<CreatineResults?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val w = weightInput.toDoubleOrNull() ?: return
        if (w <= 0) {
            errorMessage = "Please enter a valid weight."
            return
        }

        val loading = w * 0.3
        val maintenance = w * 0.03

        results = CreatineResults(w, loading, maintenance)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creatine Calculator") },
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
                    "Creatine Dosage Calculator",
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Recommendation for ${String.format(Locale.US, "%.1f", res.weight)} kg", style = MaterialTheme.typography.titleMedium)
                            
                            CreatinePhaseCard(
                                title = "Loading Phase",
                                subtitle = "For the first 5-7 days to saturate stores quickly.",
                                dosage = String.format(Locale.US, "%.1f", res.loading),
                                detail = "(Can be split into 5g doses 3-4 times a day)",
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            CreatinePhaseCard(
                                title = "Maintenance Phase",
                                subtitle = "Daily dose to maintain levels after loading.",
                                dosage = "3 - 5",
                                detail = String.format(Locale.US, "(or approx. %.1f g/day based on body weight)", res.maintenance),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Hydration Advice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "Stay well hydrated. Aim for at least 3-4 liters of water per day.",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Creatine",
                                        inputs = "{\"weight\":$weightInput}",
                                        result = "{\"loading\":${res.loading}, \"maintenance\":${res.maintenance}}"
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
                        "*Loading phase is optional but helps saturate muscle stores faster.*",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

data class CreatineResults(val weight: Double, val loading: Double, val maintenance: Double)

@Composable
fun CreatinePhaseCard(title: String, subtitle: String, dosage: String, detail: String, color: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "$dosage g/day",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
