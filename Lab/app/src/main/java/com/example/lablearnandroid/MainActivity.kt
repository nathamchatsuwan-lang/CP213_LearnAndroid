package com.example.lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                MainGameScreen()
            }
        }
    }
}

@Composable
fun MainGameScreen() {
    val context = LocalContext.current // ใช้ Context สำหรับ Intent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .padding(32.dp)
    ) {

        // --- HP Bar ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(color = Color.White)

        ) {
            Text(
                text = "hp",
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .fillMaxWidth(fraction = 0.19f)
                    .background(color = Color.Red)
                    .padding(vertical = 4.dp, horizontal = 8.dp) // ปรับ padding ให้สวยงามขึ้น
            )
        }

        // --- Profile Image ---
        // หมายเหตุ: ต้องมีคลาส LitsMainActivity อยู่จริง โค้ดถึงจะทำงานได้
        Image(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Profile",
            modifier = Modifier
                .size(480.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .clickable {
                    // ตรวจสอบว่ามี Class นี้อยู่จริงไหม
                    context.startActivity(Intent(context, LitsMainActivity::class.java))
                }
        )

        // --- Stats Variables ---
        var str by remember { mutableStateOf(8) }
        var agi by remember { mutableStateOf(10) }
        var intelligence by remember { mutableStateOf(15) } // เปลี่ยนชื่อ int เป็น intelligence ป้องกันสับสน

        // --- Stats Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // === Column STR ===
            Column(
                modifier = Modifier.padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { str++ }) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_arrow_drop_up_24),
                        contentDescription = "up",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(text = "Str", fontSize = 32.sp)
                Text(text = str.toString(), fontSize = 32.sp)

                Button(onClick = { str-- }) {
                    Image(
                        painter = painterResource(R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // === Column AGI ===
            Column(
                modifier = Modifier.padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { agi++ }) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_arrow_drop_up_24),
                        contentDescription = "up",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(text = "Agi", fontSize = 32.sp)
                Text(text = agi.toString(), fontSize = 32.sp)

                Button(onClick = { agi-- }) {
                    Image(
                        painter = painterResource(R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            } // จบ Column AGI

            // === Column INT === (แก้: ย้ายออกมาให้อยู่นอก AGI และเป็นพี่น้องกับ STR/AGI)
            Column(
                modifier = Modifier.padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { intelligence++ }) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_arrow_drop_up_24),
                        contentDescription = "up",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(text = "Int", fontSize = 32.sp)
                Text(text = intelligence.toString(), fontSize = 32.sp)

                Button(onClick = { intelligence-- }) {
                    Image(
                        painter = painterResource(R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            } // จบ Column INT
        } // จบ Row
    } // จบ Column ใหญ่
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LabLearnAndroidTheme {
        MainGameScreen()
    }
}
// check in 10/03/2569