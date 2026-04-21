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
fun VO2MaxScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    var weightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    var ageInput by remember { mutableStateOf("") }
    var timeMinInput by remember { mutableStateOf("") }
    var timeSecInput by remember { mutableStateOf("") }
    var heartRateInput by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }

    var vo2MaxResult by remember { mutableStateOf<Double?>(null) }
    var fitnessRating by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val age = ageInput.toIntOrNull() ?: return
        val w = weightInput.toDoubleOrNull() ?: return
        val tMin = timeMinInput.toDoubleOrNull() ?: return
        val tSec = timeSecInput.toDoubleOrNull() ?: 0.0
        val hr = heartRateInput.toIntOrNull() ?: return

        if (age <= 0 || w <= 0 || tMin < 0 || tSec < 0 || hr <= 0) {
            errorMessage = "Please enter valid information."
            return
        }

        val weightLbs = w * 2.20462
        val timeDecimal = tMin + (tSec / 60.0)
        val genderValue = if (selectedSex == Sex.MALE) 1 else 0
        
        val vo2max = 132.853 - (0.0769 * weightLbs) - (0.3877 * age) + (6.315 * genderValue) - (3.2649 * timeDecimal) - (0.1565 * hr)
        
        vo2MaxResult = vo2max
        fitnessRating = getFitnessRating(vo2max, age, selectedSex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VO2 Max Calculator") },
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
                    "VO2 Max Calculator (Rockport Walk Test)",
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { ageInput = it },
                        label = { Text("Age (years)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }

            item {
                Text("Walking Time (1 mile / 1.6 km)", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = timeMinInput,
                        onValueChange = { timeMinInput = it },
                        label = { Text("min") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = timeSecInput,
                        onValueChange = { timeSecInput = it },
                        label = { Text("sec") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = heartRateInput,
                    onValueChange = { heartRateInput = it },
                    label = { Text("Heart Rate Upon Completion (bpm)") },
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

            vo2MaxResult?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Estimated VO2 Max is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.1f", result),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("mL/kg/min", style = MaterialTheme.typography.titleMedium)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Text("Fitness Rating (by Age and Gender)", style = MaterialTheme.typography.bodySmall)
                            Text(
                                fitnessRating,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "VO2Max",
                                        inputs = "{\"age\":$ageInput, \"weight\":$weightInput, \"time\":\"$timeMinInput:$timeSecInput\", \"hr\":$heartRateInput, \"sex\":\"$selectedSex\"}",
                                        result = "{\"vo2max\":${String.format(Locale.US, "%.1f", result)}, \"rating\":\"$fitnessRating\"}"
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

fun getFitnessRating(vo2: Double, age: Int, sex: Sex): String {
    val ratingMap = mapOf(
        "excellent" to "Excellent",
        "good" to "Good",
        "above_avg" to "Above Average",
        "avg" to "Average",
        "below_avg" to "Below Average",
        "poor" to "Poor",
        "very_poor" to "Very Poor"
    )

    val norms = if (sex == Sex.MALE) vo2MaxMenNorms else vo2MaxWomenNorms
    val ageGroup = when {
        age <= 25 -> "18-25"
        age <= 35 -> "26-35"
        age <= 45 -> "36-45"
        age <= 55 -> "46-55"
        age <= 65 -> "56-65"
        else -> "65+"
    }

    val ageNorms = norms[ageGroup] ?: return "Cannot evaluate"
    
    for (entry in ageNorms) {
        val range = entry.value
        if (range.startsWith(">")) {
            if (vo2 > range.substring(1).toDouble()) return ratingMap[entry.key] ?: ""
        } else if (range.startsWith("<")) {
            if (vo2 < range.substring(1).toDouble()) return ratingMap[entry.key] ?: ""
        } else {
            val parts = range.split("-").map { it.toDouble() }
            if (vo2 >= parts[0] && vo2 <= parts[1]) return ratingMap[entry.key] ?: ""
        }
    }
    return "Cannot evaluate"
}

// Norms data
val vo2MaxMenNorms = mapOf(
    "18-25" to mapOf("excellent" to ">60", "good" to "52-60", "above_avg" to "47-51", "avg" to "42-46", "below_avg" to "37-41", "poor" to "30-36", "very_poor" to "<30"),
    "26-35" to mapOf("excellent" to ">56", "good" to "49-56", "above_avg" to "43-48", "avg" to "40-42", "below_avg" to "35-39", "poor" to "30-34", "very_poor" to "<30"),
    "36-45" to mapOf("excellent" to ">51", "good" to "45-51", "above_avg" to "39-44", "avg" to "35-38", "below_avg" to "31-34", "poor" to "26-30", "very_poor" to "<26"),
    "46-55" to mapOf("excellent" to ">45", "good" to "41-45", "above_avg" to "36-40", "avg" to "32-35", "below_avg" to "29-31", "poor" to "25-28", "very_poor" to "<25"),
    "56-65" to mapOf("excellent" to ">41", "good" to "38-41", "above_avg" to "33-37", "avg" to "30-32", "below_avg" to "26-29", "poor" to "22-25", "very_poor" to "<22"),
    "65+" to mapOf("excellent" to ">37", "good" to "34-37", "above_avg" to "30-33", "avg" to "28-29", "below_avg" to "23-27", "poor" to "20-22", "very_poor" to "<20")
)
val vo2MaxWomenNorms = mapOf(
    "18-25" to mapOf("excellent" to ">56", "good" to "47-56", "above_avg" to "42-46", "avg" to "38-41", "below_avg" to "33-37", "poor" to "28-32", "very_poor" to "<28"),
    "26-35" to mapOf("excellent" to ">52", "good" to "45-52", "above_avg" to "39-44", "avg" to "35-38", "below_avg" to "31-34", "poor" to "26-30", "very_poor" to "<26"),
    "36-45" to mapOf("excellent" to ">45", "good" to "39-45", "above_avg" to "35-38", "avg" to "32-34", "below_avg" to "28-31", "poor" to "24-27", "very_poor" to "<24"),
    "46-55" to mapOf("excellent" to ">40", "good" to "36-40", "above_avg" to "32-35", "avg" to "29-31", "below_avg" to "25-28", "poor" to "22-24", "very_poor" to "<22"),
    "56-65" to mapOf("excellent" to ">37", "good" to "33-37", "above_avg" to "29-32", "avg" to "26-28", "below_avg" to "23-25", "poor" to "20-22", "very_poor" to "<20"),
    "65+" to mapOf("excellent" to ">32", "good" to "29-32", "above_avg" to "26-28", "avg" to "24-25", "below_avg" to "21-23", "poor" to "18-20", "very_poor" to "<18")
)
