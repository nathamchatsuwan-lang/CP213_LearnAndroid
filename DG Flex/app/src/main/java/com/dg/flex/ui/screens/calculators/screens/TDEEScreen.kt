package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import java.time.ZonedDateTime
import java.util.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDEEScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    val heightFromProfile by viewModel.userHeight.collectAsState(initial = 0f)

    var ageInput by remember { mutableStateOf("") }
    var weightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    var heightInput by remember(heightFromProfile) { mutableStateOf(if (heightFromProfile > 0) heightFromProfile.toString() else "") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }
    var selectedActivityLevel by remember { mutableStateOf(1.2) }
    
    var bmrResult by remember { mutableStateOf<Double?>(null) }
    var tdeeResult by remember { mutableStateOf<Double?>(null) }

    val activityLevels = listOf(
        ActivityLevelItem("Sedentary (No exercise)", 1.2),
        ActivityLevelItem("Lightly Active (Exercise 1-3 days/week)", 1.375),
        ActivityLevelItem("Moderately Active (Exercise 3-5 days/week)", 1.55),
        ActivityLevelItem("Very Active (Exercise 6-7 days/week)", 1.725),
        ActivityLevelItem("Extra Active (Heavy exercise / Physical job)", 1.9)
    )

    var showActivityLevelDialog by remember { mutableStateOf(false) }

    fun calculate() {
        val age = ageInput.toIntOrNull() ?: return
        val w = weightInput.toDoubleOrNull() ?: return
        val h = heightInput.toDoubleOrNull() ?: return
        
        val bmr = if (selectedSex == Sex.MALE) {
            (10 * w) + (6.25 * h) - (5 * age) + 5
        } else {
            (10 * w) + (6.25 * h) - (5 * age) - 161
        }
        
        bmrResult = bmr
        tdeeResult = bmr * selectedActivityLevel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TDEE/BMR Calculator") },
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
                    "TDEE/BMR Calculator",
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
                    value = ageInput,
                    onValueChange = { ageInput = it },
                    label = { Text("Age (years)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
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
                OutlinedTextField(
                    value = activityLevels.find { it.level == selectedActivityLevel }?.label ?: "",
                    onValueChange = {},
                    label = { Text("Activity Level") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showActivityLevelDialog = true },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
                )
            }

            item {
                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Calculate")
                }
            }

            tdeeResult?.let { tdee ->
                item {
                    TDEEResultCard(
                        bmr = bmrResult ?: 0.0,
                        tdee = tdee,
                        onSave = {
                            viewModel.saveResult(
                                type = "TDEE",
                                inputs = "{\"age\":$ageInput, \"weight\":$weightInput, \"height\":$heightInput, \"sex\":\"$selectedSex\", \"activity\":$selectedActivityLevel}",
                                result = "{\"bmr\":${bmrResult?.toInt()}, \"tdee\":${tdee.toInt()}}"
                            )
                        }
                    )
                }
            }
        }
    }

    if (showActivityLevelDialog) {
        AlertDialog(
            onDismissRequest = { showActivityLevelDialog = false },
            confirmButton = {},
            title = { Text("Select Activity Level") },
            text = {
                Column {
                    activityLevels.forEach { item ->
                        ListItem(
                            headlineContent = { Text(item.label) },
                            modifier = Modifier.clickable {
                                selectedActivityLevel = item.level
                                showActivityLevelDialog = false
                            }
                        )
                    }
                }
            }
        )
    }
}

data class ActivityLevelItem(val label: String, val level: Double)

@Composable
fun TDEEResultCard(bmr: Double, tdee: Double, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your Results", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            ResultRow("Basal Metabolic Rate (BMR)", bmr.toInt())
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ResultRow("Total Daily Energy Expenditure (TDEE)", tdee.toInt(), isHighlighted = true)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            var expandedLoss by remember { mutableStateOf(false) }
            var expandedGain by remember { mutableStateOf(false) }
            
            GoalSection("Goal: Lose Weight", tdee, -250, -500, -1000, expandedLoss) { expandedLoss = !expandedLoss }
            Spacer(modifier = Modifier.height(8.dp))
            GoalSection("Goal: Gain Weight", tdee, 250, 500, 1000, expandedGain) { expandedGain = !expandedGain }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Result")
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: Int, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$value",
                style = if (isHighlighted) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text("calories / day", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun GoalSection(
    title: String,
    tdee: Double,
    mild: Int,
    normal: Int,
    extreme: Int,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
        
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GoalRow(if (mild < 0) "Mild Weight Loss (0.25 kg/week)" else "Mild Weight Gain (0.25 kg/week)", (tdee + mild).toInt())
                GoalRow(if (normal < 0) "Weight Loss (0.5 kg/week)" else "Weight Gain (0.5 kg/week)", (tdee + normal).toInt())
                GoalRow(if (extreme < 0) "Extreme Weight Loss (1 kg/week)" else "Extreme Weight Gain (1 kg/week)", (tdee + extreme).toInt())
            }
        }
    }
}

@Composable
fun GoalRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text("$value kcal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
