package com.example.lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part7Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Activity Transitions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                // ปุ่มกดเพื่อเปิด DetailActivity พร้อม Animation
                Button(
                    onClick = {
                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtra("EXTRA_MESSAGE", "This message is from MainActivity!")
                        }
                        
                        // 1. สร้าง ActivityOptions สำหรับ Custom Animation (Slide Up)
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            context,
                            R.anim.slide_in_up, // เข้ามาจากด้านล่าง
                            R.anim.stay        // หน้าเก่าอยู่นิ่งๆ
                        )
                        
                        context.startActivity(intent, options.toBundle())
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Open Detail (Slide Up)")
                }
            }
        }
    }
}