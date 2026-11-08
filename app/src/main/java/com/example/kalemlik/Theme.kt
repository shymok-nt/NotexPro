package com.example.kalemlik

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object KalemlikTokens {
    val Background = Color(0xFF1F2124)
    val Navigation = Color(0xFF17181B)
    val Surface = Color(0xFF2B2F36)
    val Divider = Color(0xFF3A3F48)
    val PrimaryText = Color.White
    val SecondaryText = Color(0xFF9AA0AB)
    val SectionText = Color(0xFF6F7683)
    val Accent = Color(0xFF3B5BDB)
    val Paper = Color(0xFFFBF8F1)
    val PaperLine = Color(0xFFDCE3F0)
    val InkPalette = listOf(Color(0xFF1B2A6B), Color(0xFFC2301F), Color(0xFF2F8F6B), Color(0xFFFFE27A))
    val CoverPalette = listOf(Color(0xFF3B5BDB), Color(0xFF2F8F6B), Color(0xFFC2562F), Color(0xFF6D4FB3))
    val ButtonCorner = 12.dp
    val CardCorner = 16.dp
    val CoverCorner = 14.dp
}

private val KalemlikColors = darkColorScheme(
    primary = KalemlikTokens.Accent,
    onPrimary = Color.White,
    background = KalemlikTokens.Background,
    onBackground = KalemlikTokens.PrimaryText,
    surface = KalemlikTokens.Surface,
    onSurface = KalemlikTokens.PrimaryText,
    outline = KalemlikTokens.Divider
)

@Composable
fun KalemlikTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = KalemlikColors, content = content)
}
