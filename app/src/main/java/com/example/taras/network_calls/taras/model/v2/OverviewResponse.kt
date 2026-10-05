package com.example.taras.network_calls.taras.model.v2

import androidx.compose.runtime.Immutable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalSerializationApi::class)
object FlexibleWinnerSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleWinner", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val jsonDecoder = decoder as? JsonDecoder ?: return try {
            decoder.decodeString()
        } catch (_: Exception) {
            null
        }
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> null
            is JsonPrimitive -> element.contentOrNull
            is JsonObject -> {
                element["fullName"]?.jsonPrimitive?.contentOrNull
                    ?: element["name"]?.jsonPrimitive?.contentOrNull
                    ?: element["winner"]?.jsonPrimitive?.contentOrNull
            }
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value)
        }
    }
}

@Immutable
@Serializable
data class OverviewResponse(
    val season: Int = 0,
    @SerialName("round_current") val roundCurrent: Int = 0,
    @SerialName("round_total") val roundTotal: Int = 0,
    @SerialName("championship_leader") val championshipLeader: ChampionshipLeader? = null,
    @SerialName("next_event") val nextEvent: NextEventSummary? = null,
    @SerialName("latest_race") val latestRace: LatestRaceSummary? = null
)

@Immutable
@Serializable
data class ChampionshipLeader(
    val driver: LeaderDriver? = null,
    val team: LeaderTeam? = null
)

@Immutable
@Serializable
data class LeaderDriver(
    val id: String = "",
    val number: Int = 0,
    val name: String = "",
    val team: String = "",
    @SerialName("color_hex") val colorHex: String = "",
    val points: Int = 0,
    val image: String = ""
)

@Immutable
@Serializable
data class LeaderTeam(
    val id: String = "",
    val name: String = "",
    @SerialName("color_hex") val colorHex: String = "",
    val points: Int = 0,
    val logo: String = ""
)

@Immutable
@Serializable
data class NextEventSummary(
    val round: Int = 0,
    @SerialName("race_name") val raceName: String = "",
    @SerialName("circuit_name") val circuitName: String = "",
    val country: String = "",
    val schedule: ScheduleV2 = ScheduleV2()
)

@Immutable
@Serializable
data class LatestRaceSummary(
    val round: Int = 0,
    @SerialName("race_name") val raceName: String = "",
    @SerialName("circuit_name") val circuitName: String = "",
    @Serializable(with = FlexibleWinnerSerializer::class)
    val winner: String? = null,
    val podium: List<PodiumItem> = emptyList()
)

@Immutable
@Serializable
data class PodiumItem(
    val position: Int = 0,
    @SerialName("driver_number") val driverNumber: Int = 0,
    val name: String = "",
    val team: String = ""
)
