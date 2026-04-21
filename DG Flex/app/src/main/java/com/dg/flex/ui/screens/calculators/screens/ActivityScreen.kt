package com.dg.flex.ui.screens.calculators.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navigator: DestinationsNavigator
) {
    val activities = listOf(
        ActivityInfo("Sedentary (Little or no exercise)", "Office job, sitting most of the day, little movement.", "1.2"),
        ActivityInfo("Lightly Active", "Light exercise 1-3 days/week.", "1.375"),
        ActivityInfo("Moderately Active", "Moderate exercise 3-5 days/week.", "1.55"),
        ActivityInfo("Very Active", "Heavy exercise 6-7 days/week.", "1.725"),
        ActivityInfo("Extra Active", "Very heavy exercise, athlete, or physically demanding job.", "1.9")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Level Guide") },
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
                    "Understand Your Activity Level",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    "Physical Activity Level (PAL) is a multiplier applied to your BMR to calculate your TDEE.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(activities.size) { index ->
                val activity = activities[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(activity.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("x${activity.multiplier}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(activity.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

data class ActivityInfo(val name: String, val description: String, val multiplier: String)
