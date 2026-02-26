package com.weather.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Color Palette ───────────────────────────────────────────────

// Primary - Sky Blue gradient
val SkyBlue = Color(0xFF4FC3F7)
val SkyBlueDark = Color(0xFF0288D1)
val DeepBlue = Color(0xFF01579B)

// Secondary
val SunsetOrange = Color(0xFFFF8A65)
val SunsetOrangeDark = Color(0xFFE64A19)

// Surface colors
val LightSurface = Color(0xFFF5F9FF)
val DarkSurface = Color(0xFF0D1B2A)
val DarkSurfaceVariant = Color(0xFF1B2838)

// Weather condition colors
val ClearDayStart = Color(0xFF4FC3F7)
val ClearDayEnd = Color(0xFF0288D1)
val CloudyStart = Color(0xFF90A4AE)
val CloudyEnd = Color(0xFF546E7A)
val RainyStart = Color(0xFF455A64)
val RainyEnd = Color(0xFF263238)
val NightStart = Color(0xFF0D1B2A)
val NightEnd = Color(0xFF1B2838)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlueDark,
    onPrimary = Color.White,
    primaryContainer = SkyBlue.copy(alpha = 0.2f),
    onPrimaryContainer = DeepBlue,
    secondary = SunsetOrange,
    onSecondary = Color.White,
    secondaryContainer = SunsetOrange.copy(alpha = 0.2f),
    onSecondaryContainer = SunsetOrangeDark,
    surface = LightSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7EDF5),
    onSurfaceVariant = Color(0xFF44474F),
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    outline = Color(0xFFBFC8D6),
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = DeepBlue,
    primaryContainer = SkyBlueDark.copy(alpha = 0.3f),
    onPrimaryContainer = SkyBlue,
    secondary = SunsetOrange,
    onSecondary = Color.Black,
    secondaryContainer = SunsetOrangeDark.copy(alpha = 0.3f),
    onSecondaryContainer = SunsetOrange,
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4C6D0),
    background = Color(0xFF0A1628),
    onBackground = Color(0xFFE6E1E5),
    outline = Color(0xFF3A4A5C),
)

// ─── Typography ──────────────────────────────────────────────────

val WeatherTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 56.sp,
        lineHeight = 64.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
)

// ─── Theme Composable ────────────────────────────────────────────

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WeatherTypography,
        content = content
    )
}
