package com.lily.lilyiumplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Red500,
    onPrimary = Neutral100,
    secondary = Neutral100,
    onSecondary = Red950,
    tertiary = Yellow500,
    onTertiary = Yellow1000,
    primaryContainer = Red700,
    onPrimaryContainer = Neutral100,
    secondaryContainer = Red600,
    onSecondaryContainer = Neutral100,
    tertiaryContainer = Red800,
    onTertiaryContainer = Neutral100,
    error = Red300,
    onError = Red900,
    errorContainer = Red750,
    onErrorContainer = Pink200,

    surface = Neutral950,
    onSurface = Neutral100,
    onSurfaceVariant = Neutral200,
    surfaceContainer = Neutral500,
    surfaceContainerHighest = Neutral600,
    surfaceContainerHigh = Neutral550,
    surfaceContainerLow = Neutral450,
    surfaceContainerLowest = Neutral400,

    outline = Red700,
    outlineVariant = Neutral250,



)

private val LightColorScheme = lightColorScheme(
    primary = Red500,
    onPrimary = Neutral100,
    secondary = Neutral1000,
    onSecondary = Neutral100,
    tertiary = Yellow500,
    onTertiary = Neutral100,
    primaryContainer = Red700,
    onPrimaryContainer = Neutral100,
    secondaryContainer = Red600,
    onSecondaryContainer = Neutral100,
    tertiaryContainer = Red800,
    onTertiaryContainer = Neutral100,
    error = Red650,
    onError = Neutral100,
    errorContainer = Pink200,
    onErrorContainer = Red850,

    surface = Pink100,
    onSurface = Neutral1000,
    onSurfaceVariant = Neutral300,
    surfaceContainer = Yellow250,
    surfaceContainerHighest = Yellow450,
    surfaceContainerHigh = Yellow400,
    surfaceContainerLow = Yellow300,
    surfaceContainerLowest = Yellow350,

    outline = Red700,
    outlineVariant = Yellow350,

)

//@Composable
//fun LilyiumTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = false,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}

// Theme.kt
@Composable
fun LilyiumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        dynamicColor && darkTheme  -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme                  -> DarkColorScheme
        else                       -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}