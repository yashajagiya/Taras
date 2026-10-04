package com.example.taras.core.engine

import com.example.taras.core.common.SessionType
import com.example.taras.core.helpercore.formatCountdown
import com.example.taras.core.helpercore.formatCountdownWidgets
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.network_calls.taras.model.RaceEvent
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.ParsedSession
import com.example.taras.viewmodel.SessionInfo
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

object RaceStateEngine {

    /** Buffer after race start before considering the weekend completed (approx 3 hours) */
    val RACE_COMPLETION_BUFFER = 3.hours

    /**
     * Parses all valid sessions for a [RaceEvent] and returns them ordered chronologically.
     */
    fun parseSessions(race: RaceEvent): List<ParsedSession> {
        val schedule = race.schedule
        return listOfNotNull(
            createParsedSession(SessionType.FP1, schedule.fp1?.date, schedule.fp1?.time),
            createParsedSession(SessionType.FP2, schedule.fp2?.date, schedule.fp2?.time),
            createParsedSession(SessionType.FP3, schedule.fp3?.date, schedule.fp3?.time),
            createParsedSession(SessionType.SPRINT_QUALIFYING, schedule.sprintQualy?.date, schedule.sprintQualy?.time),
            createParsedSession(SessionType.SPRINT_RACE, schedule.sprintRace?.date, schedule.sprintRace?.time),
            createParsedSession(SessionType.QUALIFYING, schedule.qualy?.date, schedule.qualy?.time),
            createParsedSession(SessionType.RACE, schedule.race?.date, schedule.race?.time)
        ).sortedBy { it.instant }
    }

    private fun createParsedSession(sessionType: SessionType, date: String?, time: String?): ParsedSession? {
        val instant = parseSessionTimeToInstant(date, time) ?: return null
        return ParsedSession(sessionType, instant)
    }

    /**
     * Converts a raw [RaceEvent] into a UI-ready [CurrentRace] domain model.
     */
    fun mapToCurrentRace(race: RaceEvent): CurrentRace {
        val parsedSessions = parseSessions(race)
        return CurrentRace(
            roundNumber = race.round,
            circuitId = race.circuit.circuitId,
            raceName = race.raceName,
            circuitName = race.circuit.circuitName,
            driverId = "",
            name = race.winner?.fullName ?: "",
            number = race.winner?.drivernumber ?: 0,
            winnerTeam = race.winner?.teamWinner ?: "",
            trackImage = race.circuit.trackImage.orEmpty(),
            parsedSessions = parsedSessions.toImmutableList()
        )
    }

    /**
     * Gets the instant when the race weekend begins (the start of its first session, usually FP1).
     */
    fun getWeekendStartInstant(sessions: List<ParsedSession>): Instant? {
        return sessions.firstOrNull()?.instant
    }

    /**
     * Gets the instant when the race weekend is considered completed (Race start + buffer).
     */
    fun getWeekendEndInstant(sessions: List<ParsedSession>): Instant? {
        val raceInstant = sessions.firstOrNull { it.sessionType == SessionType.RACE }?.instant
            ?: sessions.lastOrNull()?.instant
        return raceInstant?.plus(RACE_COMPLETION_BUFFER)
    }

    /**
     * Determines the [RaceWeekendState] for a specific [CurrentRace] at a given moment in time.
     */
    fun determineWeekendState(
        race: CurrentRace,
        now: Instant = Clock.System.now()
    ): RaceWeekendState {
        val sessions = race.parsedSessions
        if (sessions.isEmpty()) {
            return if (race.name.isNotBlank()) RaceWeekendState.Completed(race, race.name) else RaceWeekendState.OffSeason
        }

        val weekendStart = getWeekendStartInstant(sessions)
        val weekendEnd = getWeekendEndInstant(sessions)
        val raceInstant = sessions.firstOrNull { it.sessionType == SessionType.RACE }?.instant

        return when {
            weekendStart != null && now < weekendStart -> {
                val nextSession = sessions.firstOrNull { it.instant > now }
                val timeUntil = nextSession?.let { it.instant - now }
                RaceWeekendState.Upcoming(
                    race = race,
                    nextSession = nextSession,
                    timeUntilNextSession = timeUntil
                )
            }
            weekendEnd != null && now > weekendEnd -> {
                RaceWeekendState.Completed(
                    race = race,
                    winnerName = race.name.ifBlank { null }
                )
            }
            else -> {
                val isRaceDay = raceInstant != null && (now >= raceInstant.minus(12.hours) && now <= (weekendEnd ?: now))
                val nextSession = sessions.firstOrNull { it.instant > now }
                RaceWeekendState.Active(
                    race = race,
                    currentOrNextSession = nextSession,
                    isRaceDay = isRaceDay
                )
            }
        }
    }

    /**
     * Filters all races to those that are currently active or upcoming.
     * If all races in the calendar have passed, returns the final race.
     */
    fun findCurrentOrUpcomingRaces(
        races: List<RaceEvent>,
        now: Instant = Clock.System.now()
    ): List<CurrentRace> {
        if (races.isEmpty()) return emptyList()

        val activeOrUpcoming = races.mapNotNull { raceEvent ->
            val mapped = mapToCurrentRace(raceEvent)
            val weekendEnd = getWeekendEndInstant(mapped.parsedSessions)
            if (weekendEnd != null && weekendEnd >= now) {
                mapped
            } else null
        }

        return activeOrUpcoming.ifEmpty {
            listOf(mapToCurrentRace(races.last()))
        }
    }

    /**
     * Finds the single active or next upcoming race.
     */
    fun findCurrentRace(
        races: List<RaceEvent>,
        now: Instant = Clock.System.now()
    ): CurrentRace? {
        return findCurrentOrUpcomingRaces(races, now).firstOrNull()
    }

    /**
     * Finds the closest upcoming session across a list of [CurrentRace]s and constructs [SessionInfo].
     */
    fun findNextSessionInfo(
        races: List<CurrentRace>,
        now: Instant = Clock.System.now(),
        forWidget: Boolean = false
    ): SessionInfo? {
        for (race in races) {
            val upcomingSession = race.parsedSessions
                .filter { it.instant > now }
                .minByOrNull { it.instant }

            if (upcomingSession != null) {
                val duration = upcomingSession.instant - now
                val countdownStr = if (forWidget) {
                    formatCountdownWidgets(duration)
                } else {
                    formatCountdown(duration)
                }

                return SessionInfo(
                    roundNumber = race.roundNumber,
                    sessionName = upcomingSession.name,
                    countdown = countdownStr,
                    sessionTime = upcomingSession.instant.toString(),
                    targetInstant = upcomingSession.instant,
                    circuitName = race.circuitName,
                    raceName = race.raceName
                )
            }
        }
        return null
    }

    /**
     * Determines which [RaceEvent] should be selected for displaying results in [ResultViewModel].
     *
     * - If a race weekend has started FP1, its results (e.g. FP1/Qualifying) are active -> return it.
     * - If a race weekend has NOT yet begun (FP1 in future), users want to see the results
     *   of the PREVIOUS completed race.
     * - If all races are completed, returns the last race.
     */
    fun findSelectedRaceForResult(
        races: List<RaceEvent>,
        now: Instant = Clock.System.now()
    ): RaceEvent? {
        if (races.isEmpty()) return null

        val nextRaceIndex = races.indexOfFirst { race ->
            val sessions = parseSessions(race)
            val weekendEnd = getWeekendEndInstant(sessions)
            weekendEnd != null && weekendEnd >= now
        }

        if (nextRaceIndex == -1) {
            // All races ended
            return races.lastOrNull()
        }

        val targetRace = races[nextRaceIndex]
        val sessions = parseSessions(targetRace)
        val fp1Start = getWeekendStartInstant(sessions)

        return if (fp1Start != null && fp1Start > now) {
            // Weekend hasn't started yet; show previous race's results if available
            if (nextRaceIndex > 0) races[nextRaceIndex - 1] else targetRace
        } else {
            // Weekend is underway or completed
            targetRace
        }
    }
}
