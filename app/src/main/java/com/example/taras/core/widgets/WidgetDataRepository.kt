package com.example.taras.core.widgets

import android.content.Context
import android.util.Log
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UserPreferences
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import kotlinx.coroutines.flow.firstOrNull

data class TopDriverWidgetItem(
    val position: Int,
    val name: String,
    val team: String,
    val points: String
)

data class FavoriteDriverWidgetData(
    val hasFavorite: Boolean,
    val name: String? = null,
    val number: String? = null,
    val rank: String? = null,
    val points: String? = null,
    val team: String? = null,
    val lastRaceName: String? = null,
    val lastRacePosition: String? = null,
    val lastRacePoints: String? = null,
    val lastRaceTime: String? = null
)

data class PodiumDriverWidgetItem(
    val position: Int,
    val driverName: String,
    val driverNumber: String,
    val team: String,
    val timeOrGap: String,
    val points: String
)

data class RaceResultWidgetData(
    val raceName: String,
    val circuitName: String,
    val country: String,
    val date: String,
    val podium: List<PodiumDriverWidgetItem>
)

class WidgetDataRepository(
    private val dataService: TarasDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)
) {
    private val tag = "WidgetDataRepo"

    suspend fun getOverviewData(context: Context): OverviewResponse? {
        val currentData = CurrentData(context)
        return try {
            val response = dataService.getOverview()
            try {
                val json = NetworkModule.json.encodeToString(response)
                currentData.saveOverviewData(json)
            } catch (e: Exception) {
                Log.e(tag, "Error saving overview data to cache", e)
            }
            response
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching overview, trying cache", e)
            try {
                val cached = currentData.overviewData.firstOrNull()
                if (!cached.isNullOrBlank()) {
                    NetworkModule.json.decodeFromString<OverviewResponse>(cached)
                } else null
            } catch (cacheEx: Exception) {
                Log.e(tag, "Error reading cached overview", cacheEx)
                null
            }
        }
    }

    suspend fun getTopThreeStandings(context: Context): List<TopDriverWidgetItem> {
        val db = AppDatabase.getDatabase(context)
        val dao = db.topThreeDriversDao()

        // 1. Try Room Database first (offline-first)
        try {
            val cachedEntities = dao.getAll().firstOrNull()
            if (!cachedEntities.isNullOrEmpty()) {
                return cachedEntities.take(3).map { entity ->
                    val pointsDisplay = if (entity.points % 1f == 0f) {
                        "${entity.points.toInt()} pts"
                    } else {
                        "${entity.points} pts"
                    }
                    TopDriverWidgetItem(
                        position = entity.position,
                        name = entity.name,
                        team = entity.team,
                        points = pointsDisplay
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading top 3 from Room", e)
        }

        // 2. Fallback: Fetch from API and save to Room
        return try {
            val standings = dataService.getStandings()
            val top3 = standings.drivers.take(3)
            val entities = top3.mapIndexed { index, driver ->
                TopThreeDriversEntity(
                    id = index + 1,
                    position = driver.rank,
                    name = driver.name,
                    points = driver.points.toFloat(),
                    team = driver.teamName
                )
            }
            try {
                dao.insertAll(*entities.toTypedArray())
            } catch (e: Exception) {
                Log.e(tag, "Error inserting top 3 into Room", e)
            }
            entities.map {
                val pts = if (it.points % 1f == 0f) "${it.points.toInt()} pts" else "${it.points} pts"
                TopDriverWidgetItem(
                    position = it.position,
                    name = it.name,
                    team = it.team,
                    points = pts
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching standings from API", e)
            emptyList()
        }
    }

    suspend fun getFavoriteDriverData(context: Context): FavoriteDriverWidgetData {
        val userPreferences = UserPreferences(context)
        val name = userPreferences.favoriteDriverNameFlow.firstOrNull()
        if (name.isNullOrBlank()) {
            return FavoriteDriverWidgetData(hasFavorite = false)
        }

        val number = userPreferences.favoriteDriverNumberFlow.firstOrNull()
        var rank = userPreferences.favoriteDriverRankFlow.firstOrNull()
        var points = userPreferences.favoriteDriverPointsFlow.firstOrNull()
        var team = userPreferences.favoriteDriverTeamFlow.firstOrNull()

        var lastRaceName: String? = null
        var lastRacePos: String? = null
        var lastRacePts: String? = null
        var lastRaceTime: String? = null

        try {
            val raceResult = fetchLastRaceResult(context)
            if (raceResult != null) {
                lastRaceName = raceResult.raceName
                val driverResult = raceResult.sessions.race?.find { res ->
                    res.driverName.equals(name, ignoreCase = true) ||
                            res.driverName.contains(name, ignoreCase = true) ||
                            name.contains(res.driverName, ignoreCase = true) ||
                            (!number.isNullOrBlank() && res.driverNumber?.toString() == number)
                }
                if (driverResult != null) {
                    lastRacePos = driverResult.position
                    lastRacePts = driverResult.points.toString()
                    lastRaceTime = driverResult.timeOrRetired
                    if (team.isNullOrBlank() && driverResult.team.isNotBlank()) {
                        team = driverResult.team
                        try {
                            userPreferences.saveFavoriteDriverTeam(team, triggerWidgetUpdate = false)
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading last race for favorite driver", e)
        }

        // If team, rank, or points are missing, fetch from standings
        if (team.isNullOrBlank() || rank.isNullOrBlank() || points.isNullOrBlank()) {
            try {
                val standings = dataService.getStandings()
                val driverEntry = standings.drivers.find { entry ->
                    entry.name.equals(name, ignoreCase = true) ||
                            entry.name.contains(name, ignoreCase = true) ||
                            name.contains(entry.name, ignoreCase = true)
                }
                if (driverEntry != null) {
                    if (team.isNullOrBlank() && driverEntry.teamName.isNotBlank()) {
                        team = driverEntry.teamName
                    }
                    if (rank.isNullOrBlank()) {
                        rank = driverEntry.rank.toString()
                    }
                    if (points.isNullOrBlank()) {
                        points = "${driverEntry.points} pts"
                    }
                    val currentRank = rank
                    val currentPts = points
                    try {
                        userPreferences.updateFavoriteDriverStats(
                            rank = currentRank,
                            points = currentPts,
                            teamName = team,
                            triggerWidgetUpdate = false
                        )
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                Log.e(tag, "Error resolving driver stats from standings", e)
            }
        }

        return FavoriteDriverWidgetData(
            hasFavorite = true,
            name = name,
            number = number,
            rank = rank,
            points = points,
            team = team,
            lastRaceName = lastRaceName,
            lastRacePosition = lastRacePos,
            lastRacePoints = lastRacePts,
            lastRaceTime = lastRaceTime
        )
    }

    suspend fun getRaceResultData(context: Context): RaceResultWidgetData? {
        val raceResult = fetchLastRaceResult(context) ?: return null
        val podium = raceResult.sessions.race?.take(3)?.mapIndexed { index, res ->
            PodiumDriverWidgetItem(
                position = res.position.toIntOrNull() ?: (index + 1),
                driverName = res.driverName,
                driverNumber = res.driverNumber?.toString() ?: "",
                team = res.team,
                timeOrGap = res.timeOrRetired,
                points = res.points.toString()
            )
        } ?: emptyList()
        return RaceResultWidgetData(
            raceName = raceResult.raceName,
            circuitName = raceResult.circuitName,
            country = raceResult.country,
            date = raceResult.dateRange,
            podium = podium
        )
    }

    private suspend fun fetchLastRaceResult(context: Context): WeekendResultsResponse? {
        val currentData = CurrentData(context)
        return try {
            val response = dataService.getLatestResults()
            try {
                val json = NetworkModule.json.encodeToString(response)
                currentData.saveLastRaceResult(json)
            } catch (e: Exception) {
                Log.e(tag, "Error saving last race result to cache", e)
            }
            response
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching race results, trying cache", e)
            try {
                val cached = currentData.lastRaceResult.firstOrNull()
                if (!cached.isNullOrBlank()) {
                    NetworkModule.json.decodeFromString<WeekendResultsResponse>(cached)
                } else null
            } catch (cacheEx: Exception) {
                Log.e(tag, "Error reading cached race result", cacheEx)
                null
            }
        }
    }
}
