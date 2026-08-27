package com.example.taras.core.helpercore

import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.F1RacesInfoResponse
import com.example.taras.network_calls.taras.model.RaceEvent
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.ParsedSession
import com.example.taras.viewmodel.SessionInfo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.time.Clock
import android.content.Context
import android.util.Log

class RaceRepository {
    private val racesDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    suspend fun getNextRaceData(context: Context): Pair<UiState<CurrentRace?>, SessionInfo?> {
        var raceCurrentState: UiState<CurrentRace?> = UiState.Loading
        var nextSessionInfo: SessionInfo? = null

        try {
            val racesData = racesDataService.getRaceInfoData()
            
            // Save to cache
            try {
                val currentData = CurrentData(context)
                val today = getTodayDate().toRemoveDateExtra()
                val currentRace = racesData.races.firstOrNull { it.schedule.race.date.toRemoveDateExtra() >= today }
                    ?: racesData.races.lastOrNull()
                
                val cacheData = if (currentRace != null) {
                    racesData.copy(races = listOf(currentRace))
                } else racesData
                
                val json = Json.encodeToString(cacheData)
                currentData.saveRacesData(json)
            } catch (e: Exception) {
                Log.e("RaceRepository", "Error saving to cache", e)
            }

            val result = processRacesData(racesData)
            raceCurrentState = result.first
            nextSessionInfo = result.second
        } catch (e: Exception) {
            Log.e("RaceRepository", "Error fetching from API, trying cache", e)
            try {
                val currentData = CurrentData(context)
                val cachedJson = currentData.racesData.firstOrNull()
                if (cachedJson != null) {
                    val cachedData = Json.decodeFromString<F1RacesInfoResponse>(cachedJson)
                    val result = processRacesData(cachedData)
                    raceCurrentState = result.first
                    nextSessionInfo = result.second
                } else {
                    raceCurrentState = UiState.Error(e.message ?: "Unknown error")
                }
            } catch (cacheEx: Exception) {
                Log.e("RaceRepository", "Error fetching from cache", cacheEx)
                raceCurrentState = UiState.Error(e.message ?: "Unknown error")
            }
        }

        return Pair(raceCurrentState, nextSessionInfo)
    }

    private fun processRacesData(racesData: F1RacesInfoResponse): Pair<UiState<CurrentRace?>, SessionInfo?> {
        val races = racesData.races
        val today = getTodayDate().toRemoveDateExtra()

        var upcomingRaces = races.mapNotNull { race ->
            val raceDate = race.schedule.race.date.toRemoveDateExtra()
            if (raceDate >= today) {
                mapToCurrentRace(race)
            } else null
        }

        if (upcomingRaces.isEmpty() && races.isNotEmpty()) {
            upcomingRaces = listOf(mapToCurrentRace(races.last()))
        }

        val currentRace = upcomingRaces.firstOrNull()
        val raceCurrentState = UiState.Success(currentRace)

        var nextSessionInfo: SessionInfo? = null
        val now = Clock.System.now()
        for (race in upcomingRaces) {
            val upcomingSession = race.parsedSessions
                .filter { it.instant > now }
                .minByOrNull { it.instant }

            if (upcomingSession != null) {
                val duration = upcomingSession.instant - now
                nextSessionInfo = SessionInfo(
                    roundNumber = race.roundNumber,
                    sessionName = upcomingSession.name,
                    countdown = formatCountdownWidgets(duration),
                    sessionTime = upcomingSession.instant.toString(),
                    circuitName = race.circuitName,
                    raceName = race.raceName
                )
                break
            }
        }
        return Pair(raceCurrentState, nextSessionInfo)
    }

    private fun mapToCurrentRace(race: RaceEvent): CurrentRace {
        val parsedSessions = listOfNotNull(
            createParsedSession("FP1", race.schedule.fp1.date, race.schedule.fp1.time),
            createParsedSession("FP2", race.schedule.fp2?.date, race.schedule.fp2?.time),
            createParsedSession("FP3", race.schedule.fp3?.date, race.schedule.fp3?.time),
            createParsedSession(
                "Sprint Qualifying",
                race.schedule.sprintQualy?.date,
                race.schedule.sprintQualy?.time
            ),
            createParsedSession(
                "Sprint Race",
                race.schedule.sprintRace?.date,
                race.schedule.sprintRace?.time
            ),
            createParsedSession("Qualifying", race.schedule.qualy.date, race.schedule.qualy.time),
            createParsedSession("Race", race.schedule.race.date, race.schedule.race.time)
        )

        return CurrentRace(
            roundNumber = race.round,
            circuitId = race.circuit.circuitId,
            raceName = race.raceName,
            circuitName = race.circuit.circuitName,
            driverId = "",
            name = race.winner?.fullName ?: "",
            number = race.winner?.drivernumber ?: 0,
            winnerTeam = race.winner?.teamWinner ?: "",
            trackImage = race.circuit.trackImage,
            parsedSessions = parsedSessions.toImmutableList()
        )
    }

    private fun createParsedSession(name: String, date: String?, time: String?): ParsedSession? {
        val instant = parseSessionTimeToInstant(date, time) ?: return null
        return ParsedSession(name, instant)
    }
}