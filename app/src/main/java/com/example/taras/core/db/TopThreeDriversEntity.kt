package com.example.taras.core.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class TopThreeDriversEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val position: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val points: Float,
    @ColumnInfo val team: String
)