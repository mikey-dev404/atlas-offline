package com.mikey.atlasoffline

import androidx.compose.ui.graphics.Color

object AtlasColors {
    // Grayscale Core
    val Black = Color(0xFF0A0A0A)
    val DarkGray = Color(0xFF1A1A1A)
    val MediumGray = Color(0xFF2A2A2A)
    val Gray = Color(0xFF3A3A3A)
    val LightGray = Color(0xFF4A4A4A)
    val Silver = Color(0xFF6A6A6A)
    val LightSilver = Color(0xFF9A9A9A)
    val White = Color(0xFFFFFFFF)
    val OffWhite = Color(0xFFF5F5F5)

    // Backgrounds
    val Background = Color(0xFF0A0A0A)
    val BackgroundLight = Color(0xFF1A1A1A)
    val BackgroundMedium = Color(0xFF2A2A2A)

    // Glass Effects
    val GlassLight = Color(0x15FFFFFF)
    val GlassMedium = Color(0x25FFFFFF)
    val GlassHeavy = Color(0x35FFFFFF)
    val GlassDark = Color(0x40000000)

    // Text
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xCCCCCCCC)
    val TextTertiary = Color(0x99999999)
    val TextMuted = Color(0x66666666)

    // Accents (minimal color)
    val Accent = Color(0xFFFFFFFF)
    val AccentDim = Color(0x80FFFFFF)

    // Borders
    val Border = Color(0x30FFFFFF)
    val BorderLight = Color(0x15FFFFFF)
    val BorderHeavy = Color(0x50FFFFFF)

    // Status
    val Success = Color(0xFFE5E5E5)
    val Warning = Color(0xFFD0D0D0)
    val Error = Color(0xFFB0B0B0)

    // Gradients
    val BackgroundGradient = listOf(
        Color(0xFF0A0A0A),
        Color(0xFF1A1A1A),
        Color(0xFF0A0A0A)
    )

    val GlassGradient = listOf(
        Color(0x25FFFFFF),
        Color(0x10FFFFFF)
    )

    val AccentGradient = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFE5E5E5),
        Color(0xFFCCCCCC)
    )
}
