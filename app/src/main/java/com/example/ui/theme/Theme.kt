package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = KankaPrimaryDark,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = KankaPrimaryContainer,
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = KankaSecondaryContainer,
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    background = GeoDarkBackground,
    onBackground = GeoDarkTextPrimary,
    surface = GeoDarkSurface,
    onSurface = GeoDarkTextPrimary,
    surfaceVariant = GeoDarkSurfaceVariant,
    onSurfaceVariant = GeoDarkTextSecondary,
    outline = GeoDarkOutline,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = KankaPrimary,
    onPrimary = Color.White,
    primaryContainer = KankaPrimaryContainer,
    onPrimaryContainer = KankaOnPrimaryContainer,
    secondary = KankaSecondary,
    onSecondary = Color.White,
    secondaryContainer = KankaSecondaryContainer,
    onSecondaryContainer = KankaOnSecondaryContainer,
    tertiary = KankaAccentCyan,
    onTertiary = Color.White,
    background = GeoLightBackground,
    onBackground = GeoTextPrimary,
    surface = GeoLightSurface,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoLightSurfaceVariant,
    onSurfaceVariant = GeoTextSecondary,
    outline = GeoLightOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our tailored branded colors
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

