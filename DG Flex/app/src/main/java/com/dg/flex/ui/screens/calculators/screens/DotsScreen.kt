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
import kotlin.math.pow

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DotsScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    var bodyWeightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    
    var squatInput by remember { mutableStateOf("") }
    var benchInput by remember { mutableStateOf("") }
    var deadliftInput by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }

    var dotsScore by remember { mutableStateOf<Double?>(null) }
    var strengthRating by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val bw = bodyWeightInput.toDoubleOrNull() ?: return
        val s = squatInput.toDoubleOrNull() ?: 0.0
        val b = benchInput.toDoubleOrNull() ?: 0.0
        val d = deadliftInput.toDoubleOrNull() ?: 0.0

        if (bw <= 0) {
            errorMessage = "Please enter a valid body weight."
            return
        }
        if (s + b + d <= 0) {
            errorMessage = "Please enter at least one lift weight."
            return
        }

        val coeffs = if (selectedSex == Sex.FEMALE) femaleCoeffs else maleCoeffs
        val denominator = (coeffs.a * bw.pow(4)) + (coeffs.b * bw.pow(3)) + (coeffs.c * bw.pow(2)) + (coeffs.d * bw) + coeffs.e
        
        if (denominator == 0.0) {
            errorMessage = "Calculation failed. Please check your inputs."
            return
        }

        val score = ((s + b + d) / denominator) * 500.0
        dotsScore = score
        strengthRating = when {
            score < 200 -> "Beginner"
            score < 300 -> "Novice"
            score < 400 -> "Intermediate"
            score < 500 -> "Advanced"
            score < 600 -> "National Level"
            else -> "Elite / World Class"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DOTS Calculator") },
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
                    "DOTS Calculator (Powerlifting)",
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
                    value = bodyWeightInput,
                    onValueChange = { bodyWeightInput = it },
                    label = { Text("Body Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            item {
                Text("Total Lifts (1RM)", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = squatInput, onValueChange = { squatInput = it }, label = { Text("Squat") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    OutlinedTextField(value = benchInput, onValueChange = { benchInput = it }, label = { Text("Bench") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    OutlinedTextField(value = deadliftInput, onValueChange = { deadliftInput = it }, label = { Text("Deadlift") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
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
                    Text("Calculate DOTS")
                }
            }

            dotsScore?.let { score ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your DOTS Score is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.2f", score),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Points", style = MaterialTheme.typography.titleMedium)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Text("Strength Level", style = MaterialTheme.typography.bodySmall)
                            Text(
                                strengthRating,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Dots",
                                        inputs = "{\"bw\":$bodyWeightInput, \"s\":$squatInput, \"b\":$benchInput, \"d\":$deadliftInput, \"sex\":\"$selectedSex\"}",
                                        result = "{\"score\":${String.format(Locale.US, "%.2f", score)}, \"rating\":\"$strengthRating\"}"
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

data class DotsCoeffs(val a: Double, val b: Double, val c: Double, val d: Double, val e: Double)
val maleCoeffs = DotsCoeffs(-0.0000010930, 0.0007391293, -0.1918759221, 24.0900756, -307.75076)
val femaleCoeffs = DotsCoeffs(-0.0000010706, 0.0005158568, -0.1126655495, 13.6175032, -57.96288)
