package com.example.lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class SensorActivity : ComponentActivity() {

    private val sensorViewModel by viewModels<SensorViewModel>()
    private lateinit var sensorTracker: SensorTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sensorTracker = SensorTracker(this)

        setContent {
            MaterialTheme {
                SensorScreen(sensorViewModel, sensorTracker)
            }
        }
    }
}

@Composable
fun SensorScreen(viewModel: SensorViewModel, sensorTracker: SensorTracker) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val accelerometerData by viewModel.accelerometerData.collectAsState()
    val locationData by viewModel.locationData.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            sensorTracker.startLocationUpdates { lat, lng ->
                viewModel.updateLocation(lat, lng)
            }
        } else {
            Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(lifecycleOwner, hasLocationPermission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sensorTracker.startAccelerometer { values ->
                    viewModel.updateAccelerometer(values.clone()) // clone to trigger recomposition
                }
                if (hasLocationPermission) {
                    sensorTracker.startLocationUpdates { lat, lng ->
                        viewModel.updateLocation(lat, lng)
                    }
                }
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                sensorTracker.stopAccelerometer()
                sensorTracker.stopLocationUpdates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorTracker.stopAccelerometer()
            sensorTracker.stopLocationUpdates()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "MVVM + Sensors & Location", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Accelerometer", style = MaterialTheme.typography.titleMedium)
                Text(text = "X: ${accelerometerData[0]}")
                Text(text = "Y: ${accelerometerData[1]}")
                Text(text = "Z: ${accelerometerData[2]}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Location", style = MaterialTheme.typography.titleMedium)
                if (hasLocationPermission) {
                    Text(text = "Lat: ${locationData.first}")
                    Text(text = "Lng: ${locationData.second}")
                } else {
                    Text("Permission required to view location")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}
