package com.dg.flex.ui.screens.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dg.flex.R
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.ramcosta.composedestinations.generated.destinations.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculators(
    navigator: DestinationsNavigator
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val calculators = listOf(
        CalculatorItem("BMI", "Body Mass Index (BMI)", "Assess your weight category"),
        CalculatorItem("TDEE", "TDEE/BMR Calculator", "Calculate daily energy requirements"),
        CalculatorItem("BFP", "Body Fat % (BF%) & LBM", "Assess body fat and lean body mass"),
        CalculatorItem("WHR", "Waist-to-Hip Ratio (WHR)", "Assess health risk from fat distribution"),
        CalculatorItem("IBW", "Ideal Body Weight (IBW)", "Assess your ideal weight range"),
        CalculatorItem("Macros", "Macros Calculator", "Divide calories into P/C/F ratios"),
        CalculatorItem("Water", "Water Intake Calculator", "Calculate water needs based on weight"),
        CalculatorItem("Projection", "Weight Goal Projection", "Estimate time to reach target weight"),
        CalculatorItem("OneRM", "1RM & Training % Calculator", "Estimate your one-rep maximum"),
        CalculatorItem("Volume", "Workout Volume Calculator", "Calculate total training volume"),
        CalculatorItem("Progression", "Progression Planner", "Plan your training weight increments"),
        CalculatorItem("HRZone", "Heart Rate Zones", "Calculate max HR and 5 training zones"),
        CalculatorItem("VO2Max", "VO2 Max Estimator", "Assess cardiovascular fitness level"),
        CalculatorItem("Sleep", "Sleep Calculator", "Plan optimal bedtime based on cycles"),
        CalculatorItem("MealPlanner", "Meal Planner", "Create a meal timing schedule"),
        CalculatorItem("Supplement", "Supplement Guide", "Basic info and usage tips for supplements"),
        CalculatorItem("Creatine", "Creatine Calculator", "Calculate creatine dosage by weight"),
        CalculatorItem("Plate", "Plate Calculator", "Calculate plates for your barbell"),
        CalculatorItem("Pace", "Running Pace Calculator", "Calculate pace or race time"),
        CalculatorItem("DOTS", "DOTS Score Calculator", "Compare relative strength in Powerlifting"),
        CalculatorItem("Protein", "Protein Intake Calculator", "Calculate daily protein requirements"),
        CalculatorItem("Refeed", "Refeed Calculator", "Plan carbohydrate refeed periods"),
        CalculatorItem("Activity", "Activity Level Guide", "Assess your physical activity level")
    )

    val filteredCalculators = calculators.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Calculators") },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search calculators...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCalculators) { item ->
                CalculatorCard(item) {
                    when (item.id) {
                        "BMI" -> navigator.navigate(BMIScreenDestination)
                        "TDEE" -> navigator.navigate(TDEEScreenDestination)
                        "BFP" -> navigator.navigate(BFPScreenDestination)
                        "WHR" -> navigator.navigate(WHRScreenDestination)
                        "IBW" -> navigator.navigate(IBWScreenDestination)
                        "Macros" -> navigator.navigate(MacrosScreenDestination)
                        "Water" -> navigator.navigate(WaterScreenDestination)
                        "Projection" -> navigator.navigate(ProjectionScreenDestination)
                        "OneRM" -> navigator.navigate(OneRepMaxScreenDestination)
                        "Volume" -> navigator.navigate(VolumeScreenDestination)
                        "Progression" -> navigator.navigate(ProgressionScreenDestination)
                        "HRZone" -> navigator.navigate(HRZoneScreenDestination)
                        "VO2Max" -> navigator.navigate(VO2MaxScreenDestination)
                        "Sleep" -> navigator.navigate(SleepScreenDestination)
                        "MealPlanner" -> navigator.navigate(MealPlannerScreenDestination)
                        "Supplement" -> navigator.navigate(SupplementScreenDestination)
                        "Creatine" -> navigator.navigate(CreatineScreenDestination)
                        "Plate" -> navigator.navigate(PlateScreenDestination)
                        "Pace" -> navigator.navigate(PaceScreenDestination)
                        "DOTS" -> navigator.navigate(DotsScreenDestination)
                        "Protein" -> navigator.navigate(ProteinScreenDestination)
                        "Refeed" -> navigator.navigate(RefeedScreenDestination)
                        "Activity" -> navigator.navigate(ActivityScreenDestination)
                    }
                }
            }
        }
    }
}

data class CalculatorItem(
    val id: String,
    val title: String,
    val description: String
)

@Composable
fun CalculatorCard(item: CalculatorItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
