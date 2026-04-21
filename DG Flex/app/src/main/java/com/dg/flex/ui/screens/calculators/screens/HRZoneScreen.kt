package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
fun HRZoneScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var ageInput by remember { mutableStateOf("") }
    
    var hrMaxTanaka by remember { mutableStateOf<Int?>(null) }
    var hrMaxFox by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val zones = listOf(
        HRZoneData("Zone 1", "50-60%", "Recovery, Very Light", Color(0xFFA0D911), 0.5, 0.6),
        HRZoneData("Zone 2", "60-70%", "Fat Burn, Base Endurance", Color(0xFF1890FF), 0.6, 0.7),
        HRZoneData("Zone 3", "70-80%", "Aerobic Capacity (Cardio)", Color(0xFF52C41A), 0.7, 0.8),
        HRZoneData("Zone 4", "80-90%", "Performance, Anaerobic Threshold", Color(0xFFFADB14), 0.8, 0.9),
        HRZoneData("Zone 5", "90-100%", "Maximum Effort, Speed", Color(0xFFF5222D), 0.9, 1.0)
    )

    fun calculate() {
        errorMessage = null
        val age = ageInput.toIntOrNull() ?: return
        if (age <= 0 || age > 120) {
            errorMessage = "Please enter a valid age."
            return
        }

        hrMaxTanaka = (208 - (0.7 * age)).roundToInt()
        hrMaxFox = 220 - age
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Zones") },
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
                    "Heart Rate Zones Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = ageInput,
                    onValueChange = { ageInput = it },
                    label = { Text("Age (Years)") },
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

            hrMaxTanaka?.let { maxT ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Maximum Heart Rate (HRmax) is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "$maxT",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Beats Per Minute (bpm)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tanaka Formula (208 - 0.7 x Age)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "220-Age Formula result is $hrMaxFox bpm",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "HRZone",
                                        inputs = "{\"age\":$ageInput}",
                                        result = "{\"hrmax\":$maxT}"
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

                items(zones.size) { index ->
                    val zone = zones[index]
                    HRZoneRow(zone, maxT.toDouble())
                }
            }
        }
    }
}

data class HRZoneData(val name: String, val percent: String, val purpose: String, val color: Color, val low: Double, val high: Double)

@Composable
fun HRZoneRow(zone: HRZoneData, hrMax: Double) {
    val lowBpm = (hrMax * zone.low).roundToInt()
    val highBpm = (hrMax * zone.high).roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(zone.color, shape = MaterialTheme.shapes.extraSmall)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(zone.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(zone.percent, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(zone.purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "$lowBpm - $highBpm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(" bpm", style = MaterialTheme.typography.labelSmall)
        }
    }
}
