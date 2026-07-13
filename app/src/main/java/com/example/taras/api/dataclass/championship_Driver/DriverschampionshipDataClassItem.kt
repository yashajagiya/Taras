package com.example.taras.api.dataclass.championship_Driver

data class DriverschampionshipDataClassItem(
    val driver_number: Int,
    val meeting_key: Int,
    val points_current: Int,
    val points_start: Int,
    val position_current: Int,
    val position_start: Int,
    val session_key: Int
)