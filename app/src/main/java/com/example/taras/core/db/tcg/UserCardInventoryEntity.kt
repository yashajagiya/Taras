package com.example.taras.core.db.tcg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_tcg_inventory")
data class UserCardInventoryEntity(
    @PrimaryKey val cardId: String,
    @ColumnInfo val quantity: Int = 1,
    @ColumnInfo val isHoloVariant: Boolean = false,
    @ColumnInfo val unlockedAt: Long = System.currentTimeMillis(),
    @ColumnInfo val isScratchCompleted: Boolean = true
)
