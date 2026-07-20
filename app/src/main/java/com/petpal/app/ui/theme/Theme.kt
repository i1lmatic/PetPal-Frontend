package com.petpal.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = WarmWhite,
    primaryContainer = Green80,
    onPrimaryContainer = Green10,
    secondary = Amber40,
    onSecondary = WarmWhite,
    secondaryContainer = Amber80,
    onSecondaryContainer = Amber20,
    tertiary = Mint40,
    onTertiary = WarmWhite,
    tertiaryContainer = Mint80,
    onTertiaryContainer = Color(0xFF003632),
    error = Red40,
    onError = WarmWhite,
    errorContainer = Red80,
    onErrorContainer = Red20,
    background = WarmWhite,
    onBackground = DarkBrown,
    surface = WarmSurface,
    onSurface = DarkBrown,
    surfaceVariant = WarmGray,
    onSurfaceVariant = MediumBrown,
    outline = SoftGray,
    outlineVariant = WarmGray,
    inverseSurface = DarkBrown,
    inverseOnSurface = WarmWhite,
    inversePrimary = Green60
)

@Composable
fun PetPalTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PetPalTypography,
        shapes = PetPalShapes,
        content = content
    )
}
