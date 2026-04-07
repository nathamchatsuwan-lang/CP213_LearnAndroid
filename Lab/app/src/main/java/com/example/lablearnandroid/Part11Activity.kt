package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay

class Part11Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                SkeletonLoadingScreen()
            }
        }
    }
}

@Composable
fun SkeletonLoadingScreen() {
    var isLoading by remember { mutableStateOf(true) }

    // จำลองการโหลดข้อมูล 3 วินาที
    LaunchedEffect(Unit) {
        delay(3000)
        isLoading = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Mission 11: Skeleton Loading") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SkeletonExplanation()
            }

            if (isLoading) {
                // แสดง Skeleton Cards ขณะโหลด
                items(5) {
                    ShimmerListItem()
                }
            } else {
                // แสดงข้อมูลจริงหลังโหลดเสร็จ
                items(5) { index ->
                    RealListItem(index + 1)
                }
            }

            item {
                Button(
                    onClick = { isLoading = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Reload (Simulate 3s)")
                }
            }
        }
    }
}

@Composable
fun ShimmerListItem() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun RealListItem(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "D", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Data Item #$index", style = MaterialTheme.typography.titleMedium)
                Text(text = "Loaded successfully from mock server", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SkeletonExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Concept: Skeleton Loading",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Skeleton Loading คือการแสดงโครงสร้างจำลองของเนื้อหาในขณะที่กำลังรอการโหลดข้อมูล (Loading State) โดยมักใช้เอฟเฟกต์ Shimmer (การวูบวาบของแสง) เพื่อสื่อให้ผู้ใช้รู้ว่ากำลังจะมีเนื้อหาปรากฏขึ้นในตำแหน่งนั้นๆ\n\n" +
                        "ทำไมถึงดีกว่า Progress Bar?\n" +
                        "- ผู้ใช้รู้สึกว่าแอปโหลดเร็วขึ้นเพราะเห็นเค้าโครงหน้าจอทันที\n" +
                        "- ช่วยลด Cognitive Load เพราะผู้ใช้รู้ว่าจะต้องมองไปที่จุดไหน",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part11Preview() {
    LabLearnAndroidTheme {
        SkeletonLoadingScreen()
    }
}