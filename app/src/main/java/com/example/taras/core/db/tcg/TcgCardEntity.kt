package com.example.taras.core.db.tcg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taras.core.tcg.model.CardMove
import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.model.TelemetryRadarStats
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tcg_cards")
data class TcgCardEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val cardNumber: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val code: String,
    @ColumnInfo val driverNumber: String,
    @ColumnInfo val teamId: String,
    @ColumnInfo val teamName: String,
    @ColumnInfo val primaryColorHex: String,
    @ColumnInfo val secondaryColorHex: String,
    @ColumnInfo val tier: RarityTier,
    @ColumnInfo val ovr: Int,
    @ColumnInfo val role: CardRole,
    @ColumnInfo val powertrain: String,
    @ColumnInfo val debutYear: String,
    @ColumnInfo val nationality: String,
    @ColumnInfo val avatarUrl: String,
    @ColumnInfo val verticalBannerText: String,
    @ColumnInfo val move1: CardMove,
    @ColumnInfo val move2: CardMove,
    @ColumnInfo val weaknessText: String,
    @ColumnInfo val resistanceText: String,
    @ColumnInfo val pitCost: Int = 2,
    @ColumnInfo val radarStats: TelemetryRadarStats
)
