package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 1. ViewModel สำหรับส่ง One-time Event (Error Message)
class EffectViewModel : ViewModel() {
    // ใช้ SharedFlow แทน StateFlow สำหรับเหตุการณ์ที่เกิดขึ้นครั้งเดียว (เช่น Snackbar, Toast, Navigation)
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    fun triggerError() {
        viewModelScope.launch {
            // ส่งข้อความ Error เข้าไปใน Flow
            _errorEvents.emit("Error: Something went wrong in the background!")
        }
    }
}

class Part5Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                EffectScreen()
            }
        }
    }
}

@Composable
fun EffectScreen(
    viewModel: EffectViewModel = viewModel()
) {
    // 2. สถานะของ SnackbarHost
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. ใช้ LaunchedEffect สังเกตการณ์ One-time Event จาก ViewModel
    // เมื่อ errorEvents มีค่าใหม่เข้ามา LaunchedEffect จะเรียกฟังก์ชันภายใน
    LaunchedEffect(viewModel.errorEvents) {
        viewModel.errorEvents.collect { message ->
            // แสดง Snackbar เมื่อได้รับ Event
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            // 4. แสดง Snackbar Host ใน Scaffold
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "LaunchedEffect & Snackbar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Testing Side Effect in Compose",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ปุ่มกดเพื่อ Trigger Error จาก ViewModel
                Button(
                    onClick = { viewModel.triggerError() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Trigger Error Event")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EffectPreview() {
    LabLearnAndroidTheme {
        EffectScreen()
    }
}