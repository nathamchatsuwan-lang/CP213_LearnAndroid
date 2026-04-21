package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
fun VolumeScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val imperialSystem by viewModel.imperialSystem.collectAsState(initial = false)
    val unit = if (imperialSystem) "lbs" else "kg"

    var exercises by remember { mutableStateOf(listOf(ExerciseVolumeData())) }
    var totalVolume by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        var total = 0.0
        var isValid = true
        
        for (ex in exercises) {
            val s = ex.sets.toDoubleOrNull()
            val r = ex.reps.toDoubleOrNull()
            val w = ex.weight.toDoubleOrNull()
            
            if (s == null || r == null || w == null || s <= 0 || r <= 0 || w < 0) {
                isValid = false
                break
            }
            total += s * r * w
        }

        if (!isValid) {
            errorMessage = "Please fill all fields with numbers greater than 0."
            return
        }
        totalVolume = total
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Volume Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { exercises = exercises + ExerciseVolumeData() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
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
                    "Training Volume Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            itemsIndexed(exercises) { index, ex ->
                ExerciseVolumeRow(
                    index = index,
                    data = ex,
                    unit = unit,
                    onValueChange = { newData ->
                        val newList = exercises.toMutableList()
                        newList[index] = newData
                        exercises = newList
                    },
                    onRemove = {
                        if (exercises.size > 1) {
                            val newList = exercises.toMutableList()
                            newList.removeAt(index)
                            exercises = newList
                        }
                    }
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
                    Text("Calculate Total Volume")
                }
            }

            totalVolume?.let { volume ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total Training Volume", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%,.0f", volume),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(unit, style = MaterialTheme.typography.titleMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "Volume",
                                        inputs = "{\"exercises\":${exercises.size}}",
                                        result = "{\"total\":$volume, \"unit\":\"$unit\"}"
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

data class ExerciseVolumeData(val sets: String = "", val reps: String = "", val weight: String = "")

@Composable
fun ExerciseVolumeRow(index: Int, data: ExerciseVolumeData, unit: String, onValueChange: (ExerciseVolumeData) -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = data.sets,
                onValueChange = { onValueChange(data.copy(sets = it)) },
                label = { Text("Sets") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = data.reps,
                onValueChange = { onValueChange(data.copy(reps = it)) },
                label = { Text("Reps") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = data.weight,
                onValueChange = { onValueChange(data.copy(weight = it)) },
                label = { Text("Wt ($unit)") },
                modifier = Modifier.weight(1.2f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
