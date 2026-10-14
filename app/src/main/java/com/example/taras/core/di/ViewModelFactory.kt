package com.example.taras.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taras.viewmodel.AppearanceViewModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.NewsViewModel
import com.example.taras.viewmodel.PaddockViewModel
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.ResultViewModel
import com.example.taras.viewmodel.TcgViewModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.UserViewModel

/**
 * Universal ViewModelProvider.Factory for Clean Architecture & MVVM.
 * Resolves dependencies from AppContainer automatically.
 */
class TarasViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DriversViewModel::class.java) -> {
                DriversViewModel(
                    driverRepository = container.driverRepository
                ) as T
            }
            modelClass.isAssignableFrom(TeamsViewModel::class.java) -> {
                TeamsViewModel(
                    teamRepository = container.teamRepository
                ) as T
            }
            modelClass.isAssignableFrom(RacesViewModel::class.java) -> {
                RacesViewModel(
                    raceRepository = container.raceRepository,
                    currentData = container.currentData
                ) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(
                    newsRepository = container.newsRepository
                ) as T
            }
            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(
                    resultsRepository = container.resultsRepository,
                    driverRepository = container.driverRepository,
                    liveTimingRepository = container.liveTimingRepository,
                    raceRepository = container.raceRepository
                ) as T
            }
            modelClass.isAssignableFrom(PaddockViewModel::class.java) -> {
                PaddockViewModel(
                    getLatestQualifyingPositionsUseCase = container.getLatestQualifyingPositionsUseCase,
                    liveTimingRepository = container.liveTimingRepository
                ) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(
                    userPreferencesRepository = container.userPreferencesRepository,
                    userPreferences = container.userPreferences
                ) as T
            }
            modelClass.isAssignableFrom(AppearanceViewModel::class.java) -> {
                AppearanceViewModel(
                    appearanceRepository = container.appearanceRepository,
                    offlineDataStoreAppearance = container.appearanceDataStore
                ) as T
            }
            modelClass.isAssignableFrom(TcgViewModel::class.java) -> {
                TcgViewModel(
                    repository = container.tcgRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
