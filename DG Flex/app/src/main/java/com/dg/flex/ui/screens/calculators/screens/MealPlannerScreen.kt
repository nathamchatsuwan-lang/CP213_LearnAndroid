package com.dg.flex.ui.screens.calculators.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.dg.flex.ui.screens.calculators.CalculatorViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var wakeTime by remember { mutableStateOf(LocalTime.of(7, 0)) }
    var bedTime by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var numMeals by remember { mutableStateOf(4) }
    
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var mealPlan by remember { mutableStateOf<List<MealTime>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        
        var awakeMinutes = Duration.between(wakeTime, bedTime).toMinutes()
        if (awakeMinutes <= 0) {
            awakeMinutes += 24 * 60 // Handle overnight
        }

        if (awakeMinutes <= 0) {
            errorMessage = "Bedtime must be after wake up time."
            return
        }

        val interval = awakeMinutes / numMeals.toDouble()
        val results = mutableListOf<MealTime>()
        
        for (i in 1..numMeals) {
            val mealLocalTime = if (i == 1) {
                wakeTime.plusMinutes(30) // First meal 30 mins after waking
            } else {
                val prevMeal = results.last().time
                prevMeal.plusMinutes(interval.toLong())
            }
            results.add(MealTime(i, mealLocalTime))
        }
        mealPlan = results
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Timing Planner") },
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
                    "Meal Timing Planner",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wakeTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Wake Time") },
                        modifier = Modifier.weight(1f).clickable {
                            TimePickerDialog(context, { _, h, m -> wakeTime = LocalTime.of(h, m) }, wakeTime.hour, wakeTime.minute, true).show()
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        trailingIcon = { Icon(Icons.Default.AccessTime, null) }
                    )
                    OutlinedTextField(
                        value = bedTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Bedtime") },
                        modifier = Modifier.weight(1f).clickable {
                            TimePickerDialog(context, { _, h, m -> bedTime = LocalTime.of(h, m) }, bedTime.hour, bedTime.minute, true).show()
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        trailingIcon = { Icon(Icons.Default.AccessTime, null) }
                    )
                }
            }

            item {
                Text("Number of Meals: $numMeals", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = numMeals.toFloat(),
                    onValueChange = { numMeals = it.toInt() },
                    valueRange = 2f..8f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )
            }

            item {
                errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Meal Schedule")
                }
            }

            mealPlan?.let { plan ->
                item {
                    Text("Meal Schedule Example", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Button(
                        onClick = {
                            viewModel.saveResult(
                                type = "MealPlan",
                                inputs = "{\"wake\":\"${wakeTime.format(timeFormatter)}\", \"bed\":\"${bedTime.format(timeFormatter)}\", \"meals\":$numMeals}",
                                result = "{\"plan\":\"Generated\"}"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save This Schedule")
                    }
                }

                items(plan) { meal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Meal ${meal.index}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                meal.time.format(timeFormatter),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MealTime(val index: Int, val time: LocalTime)
