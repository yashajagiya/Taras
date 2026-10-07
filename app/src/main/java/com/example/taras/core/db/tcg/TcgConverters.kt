package com.example.taras.core.db.tcg

import androidx.room.TypeConverter
import com.example.taras.core.tcg.model.CardMove
import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.model.TelemetryRadarStats
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TcgConverters {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @TypeConverter
    fun fromCardMove(move: CardMove?): String? {
        return move?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toCardMove(jsonString: String?): CardMove? {
        return jsonString?.let { json.decodeFromString<CardMove>(it) }
    }

    @TypeConverter
    fun fromRadarStats(stats: TelemetryRadarStats?): String? {
        return stats?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toRadarStats(jsonString: String?): TelemetryRadarStats? {
        return jsonString?.let { json.decodeFromString<TelemetryRadarStats>(it) }
    }

    @TypeConverter
    fun fromRarityTier(tier: RarityTier?): String? {
        return tier?.name
    }

    @TypeConverter
    fun toRarityTier(name: String?): RarityTier? {
        return name?.let {
            try {
                RarityTier.valueOf(it)
            } catch (e: Exception) {
                RarityTier.C_TIER
            }
        }
    }

    @TypeConverter
    fun fromCardRole(role: CardRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toCardRole(name: String?): CardRole? {
        return name?.let {
            try {
                CardRole.valueOf(it)
            } catch (e: Exception) {
                CardRole.DRIVER
            }
        }
    }
}
