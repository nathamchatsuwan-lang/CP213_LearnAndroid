package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part10Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                AppWidgetLandingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWidgetLandingScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Mission 10: App Widgets") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WidgetExplanationCard()
            
            GlanceFeaturesCard()

            HowToUseCard()
        }
    }
}

@Composable
fun WidgetExplanationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Concept: App Widgets & Glance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "App Widgets คือส่วนเสริมของแอปที่แสดงผลบนหน้าจอหลัก (Home Screen) โดยตรง ช่วยให้ผู้ใช้เข้าถึงข้อมูลหรือฟังก์ชันสำคัญได้โดยไม่ต้องเปิดแอปหลัก\n\n" +
                        "Jetpack Glance เป็นเฟรมเวิร์กใหม่ที่ใช้พื้นฐานของ Compose ในการสร้าง Widget ช่วยให้การออกแบบ UI ของ Widget ง่ายขึ้นมากเมื่อเทียบกับ RemoteViews แบบเดิม",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun GlanceFeaturesCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ทำไมต้องใช้ Jetpack Glance?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FeatureItem(icon = Icons.Default.Info, text = "เขียน UI ด้วย Compose-like syntax")
            FeatureItem(icon = Icons.Default.Info, text = "จัดการ Layout และ State ได้ง่ายขึ้น")
            FeatureItem(icon = Icons.Default.Info, text = "รองรับ Material 3 ใน Widget")
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun HowToUseCard() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "วิธีทดสอบ Widget ของบทนี้:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "1. ไปที่หน้าจอหลัก (Home Screen) ของมือถือ/Emulator\n" +
                        "2. กดค้างที่พื้นที่ว่างบนหน้าจอ\n" +
                        "3. เลือกเมนู 'Widgets'\n" +
                        "4. ค้นหาแอป 'LabLearnAndroid'\n" +
                        "5. ลาก 'Glance Widget' มาวางบนหน้าจอ",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part10Preview() {
    LabLearnAndroidTheme {
        AppWidgetLandingScreen()
    }
}