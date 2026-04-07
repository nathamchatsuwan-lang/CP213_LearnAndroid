package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part9Activity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                CollapsingExampleScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingExampleScreen(onBack: () -> Unit) {
    // 1. สร้าง ScrollBehavior สำหรับควบคุมการหด/ขยายของ TopAppBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // 2. เชื่อมต่อการเลื่อนกับ ScrollBehavior
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Mission 9: Collapsing Title",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior // 3. กำหนด scrollBehavior ให้กับ TopAppBar
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CollapsingExplanation()
            }
            
            // รายการตัวอย่างเพื่อให้เห็นผลลัพธ์การ scroll
            items((1..30).toList()) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Item #$index: Scroll up to see the title collapse",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CollapsingExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Concept: Collapsing Top App Bar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Collapsing Top App Bar คือส่วนหัวของแอปที่สามารถยืดหดได้ตามการเลื่อน (Scroll) โดยแบ่งออกเป็น 2 สภาวะหลัก:\n\n" +
                        "1. Expanded (ขยายเต็ม): แสดงตัวหนังสือขนาดใหญ่เมื่อเนื้อหาอยู่ด้านบนสุด\n" +
                        "2. Collapsed (หดตัว): ตัวหนังสือจะย่อเล็กลงและเลื่อนขึ้นไปเป็นแถบเล็กเมื่อผู้ใช้เลื่อนหน้าจอลง\n\n" +
                        "ใน Compose ต้องใช้:\n" +
                        "- ScrollBehavior: เก็บสถานะการหด/ขยาย\n" +
                        "- nestedScroll: เชื่อมต่อเหตุการณ์เลื่อนจาก LazyColumn มายัง TopAppBar",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part9Preview() {
    LabLearnAndroidTheme {
        CollapsingExampleScreen(onBack = {})
    }
}