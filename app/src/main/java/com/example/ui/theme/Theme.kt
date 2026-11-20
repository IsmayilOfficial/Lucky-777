package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VegasNeonColorScheme = darkColorScheme(
    primary = RubyNeon,
    onPrimary = IvoryText,
    primaryContainer = RubyDeep,
    onPrimaryContainer = IvoryText,
    secondary = VegasGold,
    onSecondary = MidnightVelvet,
    secondaryContainer = ElevatedCharcoal,
    onSecondaryContainer = VegasGoldBright,
    tertiary = EmeraldWin,
    onTertiary = MidnightVelvet,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = EmeraldWin,
    background = MidnightVelvet,
    onBackground = IvoryText,
    surface = DeepObsidian,
    onSurface = IvoryText,
    surfaceVariant = ElevatedCharcoal,
    onSurfaceVariant = MutedLavender,
    outline = SubtleGoldBorder,
    error = SoftCoral,
    onError = MidnightVelvet,
    errorContainer = CoralContainer,
    onErrorContainer = SoftCoral,
)

@Composable
fun LuckySlotsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VegasNeonColorScheme,
        typography = LuckySlotsTypography,
        content = content,
    )
}
