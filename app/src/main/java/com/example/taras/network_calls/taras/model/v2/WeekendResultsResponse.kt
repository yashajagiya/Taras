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
import kotlinx.serialization.json.JsonPrimitive

object StringOrIntSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringOrInt", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return try { decoder.decodeString() } catch (_: Exception) { "" }
        return try {
            val element = jsonDecoder.decodeJsonElement()
            when {
                element is JsonNull -> ""
                element is JsonPrimitive -> element.content
                else -> element.toString()
            }
        } catch (_: Exception) {
            ""
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        val intVal = value.toIntOrNull()
        if (intVal != null) {
            encoder.encodeInt(intVal)
        } else {
            encoder.encodeString(value)
        }
    }
}

@Immutable
@Serializable
data class WeekendResultsResponse(
    val round: Int = 0,
    @SerialName("race_name") val raceName: String = "",
    val country: String = "",
    @SerialName("circuit_id") val circuitId: String = "",
    @SerialName("circuit_name") val circuitName: String = "",
    @SerialName("date_range") val dateRange: String = "",
    @SerialName("weekend_format") val weekendFormat: String = "conventional",
    @SerialName("has_sprint") val hasSprint: Boolean = false,
    val sessions: WeekendSessions = WeekendSessions()
)

@Immutable
@Serializable
data class WeekendSessions(
    @SerialName("practice_1") val practice1: List<PracticeSessionResult>? = null,
    @SerialName("practice_2") val practice2: List<PracticeSessionResult>? = null,
    @SerialName("practice_3") val practice3: List<PracticeSessionResult>? = null,
    val qualifying: List<QualifyingSessionResult>? = null,
    @SerialName("sprint_qualifying") val sprintQualifying: List<SprintQualifyingSessionResult>? = null,
    @SerialName("sprint_race") val sprintRace: List<RaceClassificationResult>? = null,
    val race: List<RaceClassificationResult>? = null
)

@Immutable
@Serializable
data class PracticeSessionResult(
    @Serializable(with = StringOrIntSerializer::class) val position: String = "0",
    @SerialName("driver_number") val driverNumber: Int? = null,
    @SerialName("driver_name") val driverName: String = "",
    @SerialName("driver_code") val driverCode: String = "",
    val team: String = "",
    @SerialName("time_or_gap") val timeOrGap: String = "",
    val laps: Int = 0
)

@Immutable
@Serializable
data class QualifyingSessionResult(
    @Serializable(with = StringOrIntSerializer::class) val position: String = "0",
    @SerialName("driver_number") val driverNumber: Int? = null,
    @SerialName("driver_name") val driverName: String = "",
    val team: String = "",
    val q1: String = "",
    val q2: String = "",
    val q3: String = "",
    val laps: Int = 0
)

@Immutable
@Serializable
data class SprintQualifyingSessionResult(
    @Serializable(with = StringOrIntSerializer::class) val position: String = "0",
    @SerialName("driver_number") val driverNumber: Int? = null,
    @SerialName("driver_name") val driverName: String = "",
    val team: String = "",
    val sq1: String = "",
    val sq2: String = "",
    val sq3: String = "",
    val laps: Int = 0
)

@Immutable
@Serializable
data class RaceClassificationResult(
    @Serializable(with = StringOrIntSerializer::class) val position: String = "0",
    @SerialName("driver_number") val driverNumber: Int? = null,
    @SerialName("driver_name") val driverName: String = "",
    val team: String = "",
    val laps: Int = 0,
    @SerialName("time_or_retired") val timeOrRetired: String = "",
    val points: Int = 0
)
