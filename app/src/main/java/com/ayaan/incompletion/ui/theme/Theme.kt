package com.ayaan.incompletion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun IncompletionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = PrimaryGreen,
            secondary = SecondaryOrange,
            background = Color(0xFF121212),
            error = ErrorRed,
            onPrimary = OnPrimary,
            onSecondary = OnSecondary,
            onBackground = OnBackground,
            onError = OnError
        )
    } else {
        lightColorScheme(
            primary = PrimaryGreen,
            secondary = SecondaryOrange,
            background = BackgroundWhite,
            error = ErrorRed,
            onPrimary = OnPrimary,
            onSecondary = OnSecondary,
            onBackground = OnBackground,
            onError = OnError
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = IncompletionTypography,
        content = content
    )
}

