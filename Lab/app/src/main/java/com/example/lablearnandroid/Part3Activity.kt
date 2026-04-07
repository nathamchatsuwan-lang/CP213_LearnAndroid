package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DonutChartScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DonutChartScreen(modifier: Modifier = Modifier) {
    val percentages = listOf(30f, 40f, 30f)
    val colors = listOf(
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFFC107)  // Amber
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Project Statistics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 1. เรียกใช้ DonutChart Composable
        DonutChart(
            percentages = percentages,
            colors = colors,
            modifier = Modifier.size(250.dp),
            strokeWidth = 40.dp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // คำอธิบายสี (Legend)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            percentages.forEachIndexed { index, value ->
                LegendItem(color = colors[index], label = "${value.toInt()}%")
            }
        }
    }
}

@Composable
fun DonutChart(
    percentages: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 30.dp
) {
    // 2. แอนิเมชันสำหรับ Sweep Angle (0 ถึง 1)
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
    }

    Canvas(modifier = modifier) {
        val totalSweep = 360f * animationProgress.value
        var startAngle = -90f // เริ่มจากด้านบนสุด

        percentages.forEachIndexed { index, percentage ->
            // คำนวณองศาของแต่ละส่วน
            val sweepAngle = (percentage / 100f) * totalSweep
            
            // 3. วาด Arc โดยใช้สไตล์ Stroke เพื่อให้เป็นรูปโดนัท
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false, // สำคัญ: false เพื่อให้เป็นเส้นรอบวง ไม่ใช่พาย
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round // ทำให้ปลายเส้นมนดูสวยงาม
                )
            )
            
            // ปรับมุมเริ่มสำหรับส่วนถัดไป
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            color = color,
            shape = MaterialTheme.shapes.extraSmall
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    LabLearnAndroidTheme {
        DonutChartScreen()
    }
}