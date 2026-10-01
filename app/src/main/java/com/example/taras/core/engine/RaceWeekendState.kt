package com.example.taras.core.engine

import androidx.compose.runtime.Immutable
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.ParsedSession
import kotlin.time.Duration

/**
 * Represents the distinct states of an F1 race weekend.
 */
@Immutable
sealed interface RaceWeekendState {

    /**
     * The race weekend has not started yet (before FP1).
     * @param race The upcoming race event.
     * @param nextSession The next session to be run (e.g. FP1).
     * @param timeUntilNextSession Duration until that session starts.
     */
    data class Upcoming(
        val race: CurrentRace,
        val nextSession: ParsedSession?,
        val timeUntilNextSession: Duration? = null
    ) : RaceWeekendState

    /**
     * The race weekend is in progress (between FP1 start and Race finish).
     * @param race The active race event.
     * @param currentOrNextSession The next session in the weekend schedule or current session.
     * @param isRaceDay True if the current moment is on the Grand Prix race day.
     */
    data class Active(
        val race: CurrentRace,
        val currentOrNextSession: ParsedSession?,
        val isRaceDay: Boolean
    ) : RaceWeekendState

    /**
     * The race weekend has concluded (after the Grand Prix finish + buffer).
     * @param race The completed race event.
     * @param winnerName Name of the winner if recorded.
     */
    data class Completed(
        val race: CurrentRace,
        val winnerName: String? = null
    ) : RaceWeekendState

    /**
     * No scheduled races or off-season.
     */
    data object OffSeason : RaceWeekendState
}
