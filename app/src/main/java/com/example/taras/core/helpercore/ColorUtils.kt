package com.example.taras.core.helpercore

import androidx.compose.ui.graphics.Color

public fun String.toComposeColor(): Color {
    return try {
        val hex = this.removePrefix("0x").removePrefix("#")
        val colorLong = when (hex.length) {
            6 -> ("FF$hex").toLong(16) // Add alpha FF for 6-digit hex
            8 -> hex.toLong(16)
            else -> return Color.Transparent
        }
        Color(colorLong)
    } catch (e: Exception) {
        Color.Transparent
    }
}
