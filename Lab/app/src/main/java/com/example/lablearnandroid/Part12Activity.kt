package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.launch

class Part12Activity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                DialogExampleScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogExampleScreen() {
    // สถานะสำหรับ Bottom Sheet
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // สถานะสำหรับ Middle Dialog (AlertDialog)
    var showAlertDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Mission 12: Dialogs & Sheets") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogExplanation()
            
            Spacer(modifier = Modifier.height(32.dp))

            // ปุ่มเปิด Modal Bottom Sheet
            Button(
                onClick = { showSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Modal Bottom Sheet")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มเปิด Middle Dialog
            OutlinedButton(
                onClick = { showAlertDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Center Dialog (Alert)")
            }
        }

        // --- Implementation of Modal Bottom Sheet ---
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                // เนื้อหาด้านใน Bottom Sheet
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Modal Bottom Sheet Content",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "นี่คือตัวอย่างของ Bottom Sheet ที่เลื่อนขึ้นมาจากด้านล่าง เหมาะสำหรับการแสดงตัวเลือกย่อยหรือเมนูที่ต้องการพื้นที่มากขึ้น")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showSheet = false
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // --- Implementation of Middle Dialog (AlertDialog) ---
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text(text = "Confirm Action") },
                text = { Text(text = "นี่คือ Middle Dialog หรือ AlertDialog แบบมาตรฐาน จะปรากฏขึ้นกลางหน้าจอเพื่อแจ้งเตือนหรือขอการยืนยันจากผู้ใช้") },
                confirmButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DialogExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Concept: Sheet vs Dialog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "1. Modal Bottom Sheet: ใช้เมื่อต้องการแสดงตัวเลือกที่ซับซ้อน หรือต้องการให้ผู้ใช้รู้สึกว่ายังอยู่ในบริบทของหน้าเดิมแต่มีเมนูเสริมขึ้นมา\n\n" +
                        "2. Middle Dialog (AlertDialog): ใช้เพื่อขัดจังหวะผู้ใช้ (Interrupt) สำหรับเหตุการณ์สำคัญ เช่น การแจ้งเตือนข้อผิดพลาด หรือการขอยืนยันการลบข้อมูล",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part12Preview() {
    LabLearnAndroidTheme {
        DialogExampleScreen()
    }
}