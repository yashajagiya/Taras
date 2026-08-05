package com.example.taras.viewmodel

import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.formatCountdownWidgets
import com.example.taras.core.helpercore.getTodayDate
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.core.helpercore.toRemoveDateExtra
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.RaceEvent
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Clock

class RaceRepository {
    private val racesDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    suspend fun getNextRaceData(): Pair<UiState<CurrentRace?>, SessionInfo?> {
        var raceCurrentState: UiState<CurrentRace?> = UiState.Loading
        var nextSessionInfo: SessionInfo? = null

        try {
            val racesData = racesDataService.getRaceInfoData()
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
            raceCurrentState = UiState.Success(currentRace)

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
                        circuitName = race.circuitName,
                        raceName = race.raceName
                    )
                    break
                }
            }

        } catch (e: Exception) {
            raceCurrentState = UiState.Error(e.message ?: "Unknown error")
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
