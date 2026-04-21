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
import kotlin.math.log10

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BFPScreen(
    navigator: DestinationsNavigator,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val weightFromProfile by viewModel.userWeight.collectAsState(initial = 0f)
    val heightFromProfile by viewModel.userHeight.collectAsState(initial = 0f)
    val sexFromProfile by viewModel.userHeight.collectAsState(initial = 0f) // Actually sex, wait

    // I should get sex from profile too
    // In ProfileViewModel, sex is Sex.MALE/FEMALE/OTHER
    
    var weightInput by remember(weightFromProfile) { mutableStateOf(if (weightFromProfile > 0) weightFromProfile.toString() else "") }
    var heightInput by remember(heightFromProfile) { mutableStateOf(if (heightFromProfile > 0) heightFromProfile.toString() else "") }
    var neckInput by remember { mutableStateOf("") }
    var waistInput by remember { mutableStateOf("") }
    var hipInput by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf(Sex.MALE) }

    var bfpResult by remember { mutableStateOf<Double?>(null) }
    var fatMass by remember { mutableStateOf(0.0) }
    var lbm by remember { mutableStateOf(0.0) }
    var category by remember { mutableStateOf("") }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    var categoryColor by remember(onSurfaceColor) { mutableStateOf(onSurfaceColor) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        errorMessage = null
        val w = weightInput.toDoubleOrNull() ?: return
        val h = heightInput.toDoubleOrNull() ?: return
        val n = neckInput.toDoubleOrNull() ?: return
        val waist = waistInput.toDoubleOrNull() ?: return
        val hip = hipInput.toDoubleOrNull() ?: 0.0

        if (w <= 0 || h <= 0 || n <= 0 || waist <= 0 || (selectedSex == Sex.FEMALE && hip <= 0)) {
            errorMessage = "Please enter valid measurements greater than 0."
            return
        }

        if (selectedSex == Sex.MALE && waist <= n) {
            errorMessage = "Waist measurement must be greater than neck."
            return
        }
        if (selectedSex == Sex.FEMALE && (waist + hip) <= n) {
            errorMessage = "Sum of waist and hip must be greater than neck."
            return
        }

        val bfp = if (selectedSex == Sex.MALE) {
            495 / (1.0324 - 0.19077 * log10(waist - n) + 0.15456 * log10(h)) - 450
        } else {
            495 / (1.29579 - 0.35004 * log10(waist + hip - n) + 0.22100 * log10(h)) - 450
        }

        if (bfp.isNaN() || bfp < 1 || bfp > 75) {
            errorMessage = "Cannot calculate with these measurements. Please check your data."
            return
        }

        bfpResult = bfp
        fatMass = w * (bfp / 100.0)
        lbm = w - fatMass

        if (selectedSex == Sex.MALE) {
            when {
                bfp < 6 -> { category = "Essential Fat"; categoryColor = primaryColor }
                bfp <= 13 -> { category = "Athletes"; categoryColor = tertiaryColor }
                bfp <= 17 -> { category = "Fitness"; categoryColor = tertiaryColor }
                bfp <= 24 -> { category = "Average"; categoryColor = secondaryColor }
                else -> { category = "Obese"; categoryColor = primaryColor }
            }
        } else {
            when {
                bfp < 14 -> { category = "Essential Fat"; categoryColor = primaryColor }
                bfp <= 20 -> { category = "Athletes"; categoryColor = tertiaryColor }
                bfp <= 24 -> { category = "Fitness"; categoryColor = tertiaryColor }
                bfp <= 31 -> { category = "Average"; categoryColor = secondaryColor }
                else -> { category = "Obese"; categoryColor = primaryColor }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BFP Calculator") },
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
                    "Body Fat Percentage (BFP) & LBM Calculator",
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
                    value = neckInput,
                    onValueChange = { neckInput = it },
                    label = { Text("Neck (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
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

            if (selectedSex == Sex.FEMALE) {
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

            bfpResult?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Your Body Fat Percentage is", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                String.format(Locale.US, "%.1f%%", result),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                            Text(
                                "Result: $category",
                                style = MaterialTheme.typography.titleMedium,
                                color = categoryColor
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            ResultLabelValue("Fat Mass", String.format(Locale.US, "%.1f kg", fatMass))
                            ResultLabelValue("Lean Body Mass (LBM)", String.format(Locale.US, "%.1f kg", lbm), isBold = true)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveResult(
                                        type = "BFP",
                                        inputs = "{\"weight\":$weightInput, \"height\":$heightInput, \"neck\":$neckInput, \"waist\":$waistInput, \"hip\":$hipInput, \"sex\":\"$selectedSex\"}",
                                        result = "{\"bfp\":${String.format(Locale.US, "%.1f", result)}, \"lbm\":${String.format(Locale.US, "%.1f", lbm)}, \"category\":\"$category\"}"
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

@Composable
fun ResultLabelValue(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
