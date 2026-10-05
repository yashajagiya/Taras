package com.example.taras.network_calls.taras.model.v2

import androidx.compose.runtime.Immutable
import com.example.taras.network_calls.taras.model.SeasonStats
import com.example.taras.network_calls.taras.model.TeamSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class TeamDetailResponseV2(
    val id: String = "",
    val name: String = "",
    @SerialName("full_team_name") val fullTeamName: String = "",
    val base: String = "",
    @SerialName("team_chief") val teamChief: String = "",
    @SerialName("technical_chief") val technicalChief: String = "",
    val chassis: String = "",
    @SerialName("power_unit") val powerUnit: String = "",
    @SerialName("reserve_driver") val reserveDriver: String = "",
    @SerialName("first_team_entry") val firstTeamEntry: String = "",
    val colors: TeamColorsV2 = TeamColorsV2(),
    val images: TeamImagesV2 = TeamImagesV2(),
    val drivers: List<String> = emptyList(),
    val standings: TeamStandingsSummaryV2 = TeamStandingsSummaryV2(),
    val biography: String = "",
    @SerialName("season_2026") val seasonStats: SeasonStats? = null,
    @SerialName("team_summary") val teamSummary: TeamSummary? = null,
    @SerialName("f1_url") val f1Url: String = ""
)

@Immutable
@Serializable
data class TeamColorsV2(
    @SerialName("color_argb") val colorArgb: String? = null,
    @SerialName("color_hex") val colorHex: String? = null,
    @SerialName("accessible_color") val accessibleColor: String? = null
)

@Immutable
@Serializable
data class TeamImagesV2(
    val car: String? = null,
    val logo: String? = null
)

@Immutable
@Serializable
data class TeamStandingsSummaryV2(
    val rank: Int = 0,
    val points: Int = 0,
    val races: List<RaceStandingBreakdown> = emptyList()
)
