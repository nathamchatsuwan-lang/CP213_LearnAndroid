package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay

class Part1AnimationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LikeButtonScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LikeButtonScreen(modifier: Modifier = Modifier) {
    // 1. สถานะพื้นฐาน
    var isLiked by remember { mutableStateOf(false) }
    var buttonScaleTarget by remember { mutableStateOf(1.0f) }
    
    // 2. Scale Animation (ขยายแล้วกลับมาขนาดเดิม)
    val scale by animateFloatAsState(
        targetValue = buttonScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ScaleAnimation",
        finishedListener = {
            // เมื่อขยายเสร็จ ให้กลับมาขนาดเดิม (1.0f)
            if (buttonScaleTarget == 1.2f) {
                buttonScaleTarget = 1.0f
            }
        }
    )

    // 3. Background Color Animation (เทา -> ชมพู)
    val backgroundColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFE91E63) else Color.LightGray,
        label = "BackgroundColorAnimation"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ปุ่มกด
        Button(
            onClick = { 
                isLiked = !isLiked
                // ทริกเกอร์การขยาย
                buttonScaleTarget = 1.2f
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = if (isLiked) Color.White else Color.Black
            ),
            modifier = Modifier
                .scale(scale) // นำ scale animation มาใช้
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLiked) "Liked" else "Like",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                // 4. AnimatedVisibility แสดงหัวใจเมื่อ Liked
                AnimatedVisibility(
                    visible = isLiked,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Heart",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LikeButtonPreview() {
    LabLearnAndroidTheme {
        LikeButtonScreen()
    }
}