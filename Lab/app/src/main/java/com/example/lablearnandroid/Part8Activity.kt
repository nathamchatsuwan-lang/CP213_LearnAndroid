package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResponsiveProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ResponsiveProfileScreen(modifier: Modifier = Modifier) {
    // 1. ใช้ BoxWithConstraints เพื่อตรวจสอบขนาดหน้าจอที่ใช้งานอยู่ (maxWidth)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val isTablet = maxWidth >= 600.dp

        if (isTablet) {
            // 2. แนวนอน (Row) สำหรับหน้าจอขนาดกว้าง
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.width(32.dp))
                ProfileInfo(modifier = Modifier.weight(1f))
            }
        } else {
            // 3. แนวตั้ง (Column) สำหรับหน้าจอมือถือ
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage(modifier = Modifier.size(150.dp))
                Spacer(modifier = Modifier.height(24.dp))
                ProfileInfo(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ProfileImage(modifier: Modifier = Modifier) {
    // กล่องสมมติแทนรูปโปรไฟล์
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = MaterialTheme.shapes.large
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "Photo", color = Color.DarkGray)
        }
    }
}

@Composable
fun ProfileInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Android Developer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Experienced in Jetpack Compose, Kotlin, and Responsive Design. Passing through different screen sizes effectively.",
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )
    }
}

// Preview สำหรับหน้าจอมือถือ (Portrait)
@Preview(showBackground = true, widthDp = 400)
@Composable
fun PortraitPreview() {
    LabLearnAndroidTheme {
        ResponsiveProfileScreen()
    }
}

// Preview สำหรับแท็บเล็ต (Landscape)
@Preview(showBackground = true, widthDp = 800)
@Composable
fun LandscapePreview() {
    LabLearnAndroidTheme {
        ResponsiveProfileScreen()
    }
}