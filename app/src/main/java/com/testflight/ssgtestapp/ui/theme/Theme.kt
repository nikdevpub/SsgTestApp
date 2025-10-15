package com.testflight.ssgtestapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SsgTestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val localFocusManager = LocalFocusManager.current
    ChangeSystemControlsColor(colorScheme)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                }
        ) {
            content()
        }
    }
}

@Composable
private fun ChangeSystemControlsColor(colorScheme: ColorScheme) {
    val view = LocalView.current

    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as Activity).window

        // Determine if light icons are needed
        val isLightBars = colorScheme.background.luminance() > 0.5f

        // API 23+ supports light status bar icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isLightBars

            // API 26+ supports light navigation bar icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                insetsController.isAppearanceLightNavigationBars = isLightBars
            }

            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
        } else {
            // API 21-22: Use dark color for status bar
            // to make white icons visible
            window.statusBarColor = if (isLightBars) {
                // If background is light, make status bar semi-transparent dark
                Color(0xFF4A5B7C).copy(alpha = 0.3f).toArgb()
            } else {
                // If background is dark, use surface color
                colorScheme.surface.toArgb()
            }

            // Navigation bar is always dark on API 21-22
            window.navigationBarColor = Color(0xFF4A5B7C).copy(alpha = 0.3f).toArgb()
        }
    }
}
