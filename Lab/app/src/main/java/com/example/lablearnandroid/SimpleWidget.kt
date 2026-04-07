package com.example.lablearnandroid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class SimpleWidget : GlanceAppWidget() {

    // สำหรับ Glance 1.1.x ฟังก์ชันที่ต้อง override คือ provideGlance
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "Mission 10",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.primary
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Jetpack Glance Widget",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface
                )
            )
        }
    }
}
