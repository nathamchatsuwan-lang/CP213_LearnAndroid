package com.dg.flex.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val BlackRedColorScheme = darkColorScheme(
    primary = PrimaryRed,
    secondary = SecondaryRed,
    tertiary = TertiaryRed,
    background = BackgroundBlack,
    surface = SurfaceBlack,
    onPrimary = OnRedText,
    onSecondary = OnRedText,
    onTertiary = OnRedText,
    onBackground = OnBlackText,
    onSurface = OnBlackText,
    surfaceVariant = SurfaceContainerBlack,
    onSurfaceVariant = OnBlackText,
    surfaceContainer = SurfaceContainerBlack,
    surfaceContainerLow = SurfaceBlack,
    surfaceContainerHigh = Color(0xFF2C2C2C)
)

@Composable
fun DGFlexTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackRedColorScheme,
        typography = customTypography(),
        content = content
    )
}

