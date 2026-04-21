package com.dg.flex.ui.screens.calculators.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.dg.flex.ui.screens.calculators.CalculatorViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var wakeUpTime by remember { mutableStateOf(LocalTime.of(7, 0)) }
    val context = LocalContext.current

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    var results by remember { mutableStateOf<List<SleepOption>>(emptyList()) }

    fun calculate() {
        val timeToFallAsleep = 15 // minutes
        val cycleDuration = 90 // minutes
        val cyclesToCalculate = listOf(6, 5, 4)

        val options = cyclesToCalculate.map { cycles ->
            val totalSleepMinutes = cycles * cycleDuration
            val bedtime = wakeUpTime.minusMinutes(totalSleepMinutes.toLong() + timeToFallAsleep)
            SleepOption(bedtime, cycles, totalSleepMinutes / 60.0)
        }
        results = options
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Calculator") },
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
                    "Sleep Timing Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                OutlinedTextField(
                    value = wakeUpTime.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("What time do you want to wake up?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    wakeUpTime = LocalTime.of(hour, minute)
                                },
                                wakeUpTime.hour,
                                wakeUpTime.minute,
                                true
                            ).show()
                        },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                )
            }

            item {
                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Calculate Bedtime")
                }
            }

            if (results.isNotEmpty()) {
                item {
                    Text(
                        "To wake up refreshed at ${wakeUpTime.format(timeFormatter)}, you should go to bed around:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(results.size) { index ->
                    val option = results[index]
                    BedtimeCard(option, onSave = {
                        viewModel.saveResult(
                            type = "Sleep",
                            inputs = "{\"wakeup\":\"${wakeUpTime.format(timeFormatter)}\"}",
                            result = "{\"bedtime\":\"${option.time.format(timeFormatter)}\", \"hours\":${option.hours}}"
                        )
                    })
                }

                item {
                        Text(
                            "*Includes a 15-minute window to fall asleep.*",
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

data class SleepOption(val time: LocalTime, val cycles: Int, val hours: Double)

@Composable
fun BedtimeCard(option: SleepOption, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    option.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "For ${option.cycles} sleep cycles (${String.format(Locale.US, "%.1f", option.hours)} hours)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onSave) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    }
}
