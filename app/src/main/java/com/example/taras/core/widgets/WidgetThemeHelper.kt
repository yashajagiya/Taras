@file:SuppressLint("RestrictedApi")

package com.example.taras.core.widgets

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProviders
import androidx.glance.unit.ColorProvider
import com.example.taras.core.common.OfflineDataStoreAppearance
import com.example.taras.ui.theme.DarkColorScheme
import com.example.taras.ui.theme.LightColorScheme
import kotlinx.coroutines.flow.firstOrNull

/**
 * Custom FixedColorProvider implementing Glance ColorProvider directly to avoid
 * the Android Lint RestrictedApi false positive on ColorProviderKt.ColorProvider.
 */
class FixedColorProvider(private val color: Color) : ColorProvider {
    override fun getColor(context: Context): Color = color
}

fun fixedColorProvider(color: Color): ColorProvider = FixedColorProvider(color)

val WidgetGoldProvider: ColorProvider = FixedColorProvider(Color(0xFFFFD700))
val WidgetSilverProvider: ColorProvider = FixedColorProvider(Color(0xFFC8D1D9))
val WidgetBronzeProvider: ColorProvider = FixedColorProvider(Color(0xFFCD7F32))
val WidgetWhiteColorProvider: ColorProvider = FixedColorProvider(Color.White)
val WidgetDarkColorProvider: ColorProvider = FixedColorProvider(Color(0xFF141218))

fun getTeamColor(teamName: String?): Color {
    val clean = teamName?.trim()?.lowercase() ?: return Color(0xFFE80020)
    return when {
        clean.contains("red bull") -> Color(0xFF3671C6)
        clean.contains("ferrari") -> Color(0xFFE80020)
        clean.contains("mclaren") -> Color(0xFFFF8000)
        clean.contains("mercedes") -> Color(0xFF27F4D2)
        clean.contains("aston martin") -> Color(0xFF229971)
        clean.contains("alpine") -> Color(0xFF0090FF)
        clean.contains("williams") -> Color(0xFF64C4FF)
        clean.contains("rb") || clean.contains("racing bull") || clean.contains("cash app") || clean.contains("alphatauri") -> Color(0xFF6692FF)
        clean.contains("sauber") || clean.contains("kick") || clean.contains("alfa") -> Color(0xFF52E252)
        clean.contains("haas") -> Color(0xFFB6BABD)
        else -> Color(0xFFE80020)
    }
}

fun getTeamColorProvider(teamName: String?): ColorProvider {
    return FixedColorProvider(getTeamColor(teamName))
}

fun resolveWidgetThemeColors(widgetTheme: String?, appTheme: String?): ColorProviders {
    return when (widgetTheme) {
        "Light" -> androidx.glance.material3.ColorProviders(
            light = LightColorScheme,
            dark = LightColorScheme
        )
        "System Default" -> {
            when (appTheme) {
                "Dark" -> androidx.glance.material3.ColorProviders(
                    light = DarkColorScheme,
                    dark = DarkColorScheme
                )
                "Light" -> androidx.glance.material3.ColorProviders(
                    light = LightColorScheme,
                    dark = LightColorScheme
                )
                else -> androidx.glance.material3.ColorProviders(
                    light = LightColorScheme,
                    dark = DarkColorScheme
                )
            }
        }
        // Default is "Dark" for widgets
        else -> androidx.glance.material3.ColorProviders(
            light = DarkColorScheme,
            dark = DarkColorScheme
        )
    }
}

@Composable
fun rememberWidgetThemeColors(): ColorProviders {
    val context = androidx.glance.LocalContext.current
    val appearanceStore = androidx.compose.runtime.remember(context) { OfflineDataStoreAppearance(context) }
    val widgetTheme by appearanceStore.widgetThemeData.collectAsState(initial = "Dark")
    val appTheme by appearanceStore.appearanceData.collectAsState(initial = "Light")
    return resolveWidgetThemeColors(widgetTheme, appTheme)
}

suspend fun getWidgetThemeColors(context: Context): ColorProviders {
    val appearanceStore = OfflineDataStoreAppearance(context)
    val widgetTheme = try {
        appearanceStore.widgetThemeData.firstOrNull() ?: "Dark"
    } catch (_: Exception) {
        "Dark"
    }
    val appTheme = try {
        appearanceStore.appearanceData.firstOrNull() ?: "Light"
    } catch (_: Exception) {
        "Light"
    }
    return resolveWidgetThemeColors(widgetTheme, appTheme)
}
