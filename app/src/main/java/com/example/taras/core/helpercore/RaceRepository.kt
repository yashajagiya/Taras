package com.example.taras.core.helpercore

import android.content.Context
import android.util.Log
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.engine.RaceStateEngine
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.F1RacesInfoResponse
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo
import kotlinx.coroutines.flow.firstOrNull

class RaceRepository(
    private val racesDataService: TarasDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)
) {
    suspend fun getNextRaceData(context: Context): Pair<UiState<CurrentRace?>, SessionInfo?> {
        return try {
            val racesData = racesDataService.getRaceInfoData()

            // Save full season to cache
            try {
                val currentData = CurrentData(context)
                val json = NetworkModule.json.encodeToString(racesData)
                currentData.saveRacesData(json)
            } catch (e: Exception) {
                Log.e("RaceRepository", "Error saving to cache", e)
            }

            processRacesData(racesData)
        } catch (e: Exception) {
            Log.e("RaceRepository", "Error fetching from API, trying cache", e)
            try {
                val currentData = CurrentData(context)
                val cachedJson = currentData.racesData.firstOrNull()
                if (cachedJson != null) {
                    val cachedData = NetworkModule.json.decodeFromString<F1RacesInfoResponse>(cachedJson)
                    processRacesData(cachedData)
                } else {
                    Pair(UiState.Error(e.toAppError()), null)
                }
            } catch (cacheEx: Exception) {
                Log.e("RaceRepository", "Error fetching from cache", cacheEx)
                Pair(UiState.Error(e.toAppError()), null)
            }
        }
    }

    private fun processRacesData(racesData: F1RacesInfoResponse): Pair<UiState<CurrentRace?>, SessionInfo?> {
        val upcomingRaces = RaceStateEngine.findCurrentOrUpcomingRaces(racesData.races)
        val currentRace = upcomingRaces.firstOrNull()
        val nextSessionInfo = RaceStateEngine.findNextSessionInfo(upcomingRaces, forWidget = true)
        return Pair(UiState.Success(currentRace), nextSessionInfo)
    }
}