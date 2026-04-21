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
import com.ramcosta.composedestinations.generated.destinations.*

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementScreen(
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplement Guide") },
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
                    "Supplement Usage Guide",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SupplementCard(
                    title = "Protein",
                    description = "A primary macronutrient essential for muscle growth and repair. Sufficient protein intake is crucial for everyone.",
                    recommendation = "Requirement depends on body weight and goals. You can use our calculator for personalized results.",
                    buttonText = "Go to Protein Calculator",
                    onButtonClick = { navigator.navigate(ProteinScreenDestination) }
                )
            }

            item {
                SupplementCard(
                    title = "Creatine Monohydrate",
                    description = "One of the most researched supplements. Enhances molecular energy (ATP), boosting strength and power for high-intensity, short-duration exercise.",
                    recommendation = "Optimal dosage depends on body weight and usage phase (loading vs. maintenance). Use our detailed calculator below.",
                    buttonText = "Go to Creatine Calculator",
                    onButtonClick = { navigator.navigate(CreatineScreenDestination) }
                )
            }

            item {
                SupplementCard(
                    title = "BCAAs",
                    description = "A group of three essential amino acids (Leucine, Isoleucine, Valine) that play a key role in muscle protein synthesis.",
                    recommendation = "Typical dose: 10-20g, often taken during workouts. Note: If you consume enough protein from whole foods, additional BCAA supplementation may not be necessary.",
                    buttonText = null,
                    onButtonClick = {}
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Important Note", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Supplements are only secondary aids. The most critical foundations are a balanced whole-food diet, adequate rest, and consistent training.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SupplementCard(title: String, description: String, recommendation: String, buttonText: String?, onButtonClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Recommended Intake:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(recommendation, style = MaterialTheme.typography.bodySmall)
            
            buttonText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onButtonClick, modifier = Modifier.fillMaxWidth()) {
                    Text(it)
                }
            }
        }
    }
}
