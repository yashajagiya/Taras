package com.example.taras.network_calls.taras.model.v2

import androidx.compose.runtime.Immutable
import com.example.taras.network_calls.taras.model.CareerStats
import com.example.taras.network_calls.taras.model.DriverSeasonStats
import com.example.taras.network_calls.taras.model.Quote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DriverDetailResponseV2(
    val id: String = "",
    val number: Int? = null,
    val code: String = "",
    val name: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val nationality: String = "",
    val team: DriverTeamInfoV2 = DriverTeamInfoV2(),
    val images: DriverImagesV2 = DriverImagesV2(),
    val standings: DriverStandingsSummaryV2 = DriverStandingsSummaryV2(),
    val biography: DriverBiographyV2 = DriverBiographyV2(),
    @SerialName("season_2026") val seasonStats: DriverSeasonStats? = null,
    @SerialName("career_stats") val careerStats: CareerStats? = null,
    @SerialName("f1_url") val f1Url: String = ""
)

@Immutable
@Serializable
data class DriverTeamInfoV2(
    val id: String = "",
    val name: String = "",
    @SerialName("color_argb") val colorArgb: String? = null,
    @SerialName("color_hex") val colorHex: String? = null,
    @SerialName("accessible_color") val accessibleColor: String? = null
)

@Immutable
@Serializable
data class DriverImagesV2(
    val portrait: String? = null,
    @SerialName("number_logo") val numberLogo: String? = null
)

@Immutable
@Serializable
data class DriverStandingsSummaryV2(
    val rank: Int = 0,
    val points: Int = 0,
    val races: List<RaceStandingBreakdown> = emptyList()
)

@Immutable
@Serializable
data class DriverBiographyV2(
    @SerialName("date_of_birth") val dateOfBirth: String = "",
    @SerialName("place_of_birth") val placeOfBirth: String = "",
    val quote: Quote? = null,
    val paragraphs: List<String> = emptyList()
)
