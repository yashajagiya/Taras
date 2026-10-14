package com.example.taras.core.di

import android.content.Context
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.OfflineDataStoreAppearance
import com.example.taras.core.common.UserPreferences
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.db.TopThreeDriversDAO
import com.example.taras.core.db.tcg.TcgCardDao
import com.example.taras.core.repository.F1LiveRepository
import com.example.taras.core.tcg.repository.TcgRepository
import com.example.taras.data.repository.AppearanceRepositoryImpl
import com.example.taras.data.repository.DriverRepositoryImpl
import com.example.taras.data.repository.LiveTimingRepositoryImpl
import com.example.taras.data.repository.NewsRepositoryImpl
import com.example.taras.data.repository.RaceRepositoryImpl
import com.example.taras.data.repository.ResultsRepositoryImpl
import com.example.taras.data.repository.TeamRepositoryImpl
import com.example.taras.data.repository.UserPreferencesRepositoryImpl
import com.example.taras.domain.repository.AppearanceRepository
import com.example.taras.domain.repository.DriverRepository
import com.example.taras.domain.repository.LiveTimingRepository
import com.example.taras.domain.repository.NewsRepository
import com.example.taras.domain.repository.RaceRepository
import com.example.taras.domain.repository.ResultsRepository
import com.example.taras.domain.repository.TeamRepository
import com.example.taras.domain.repository.UserPreferencesRepository
import com.example.taras.domain.usecase.GetDriversUseCase
import com.example.taras.domain.usecase.GetLatestQualifyingPositionsUseCase
import com.example.taras.domain.usecase.GetLiveTimingUseCase
import com.example.taras.domain.usecase.GetNewsUseCase
import com.example.taras.domain.usecase.GetRaceCalendarUseCase
import com.example.taras.domain.usecase.GetTeamsUseCase
import com.example.taras.domain.usecase.GetWeekendResultsUseCase
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService

/**
 * Dependency Injection container interface for Taras.
 * Follows the Android Architecture Guide recommendation for manual DI / Service Locator.
 */
interface AppContainer {
    val context: Context

    // Local Data Sources
    val appDatabase: AppDatabase
    val topThreeDriversDao: TopThreeDriversDAO
    val tcgCardDao: TcgCardDao
    val userPreferences: UserPreferences
    val currentData: CurrentData
    val appearanceDataStore: OfflineDataStoreAppearance

    // Remote Data Sources
    val tarasDataService: TarasDataService

    // Domain Repositories
    val driverRepository: DriverRepository
    val teamRepository: TeamRepository
    val raceRepository: RaceRepository
    val newsRepository: NewsRepository
    val resultsRepository: ResultsRepository
    val liveTimingRepository: LiveTimingRepository
    val tcgRepository: TcgRepository
    val userPreferencesRepository: UserPreferencesRepository
    val appearanceRepository: AppearanceRepository

    // Domain Use Cases
    val getDriversUseCase: GetDriversUseCase
    val getTeamsUseCase: GetTeamsUseCase
    val getRaceCalendarUseCase: GetRaceCalendarUseCase
    val getNewsUseCase: GetNewsUseCase
    val getWeekendResultsUseCase: GetWeekendResultsUseCase
    val getLiveTimingUseCase: GetLiveTimingUseCase
    val getLatestQualifyingPositionsUseCase: GetLatestQualifyingPositionsUseCase
}

/**
 * Default implementation of AppContainer lazily instantiating components.
 */
class DefaultAppContainer(override val context: Context) : AppContainer {

    override val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val topThreeDriversDao: TopThreeDriversDAO by lazy {
        appDatabase.topThreeDriversDao()
    }

    override val tcgCardDao: TcgCardDao by lazy {
        appDatabase.tcgCardDao()
    }

    override val userPreferences: UserPreferences by lazy {
        UserPreferences(context)
    }

    override val currentData: CurrentData by lazy {
        CurrentData(context)
    }

    override val appearanceDataStore: OfflineDataStoreAppearance by lazy {
        OfflineDataStoreAppearance(context)
    }

    override val tarasDataService: TarasDataService by lazy {
        NetworkModule.tarasDataService
    }

    // Repositories
    override val driverRepository: DriverRepository by lazy {
        DriverRepositoryImpl(tarasDataService, topThreeDriversDao)
    }

    override val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(tarasDataService)
    }

    override val raceRepository: RaceRepository by lazy {
        RaceRepositoryImpl(tarasDataService, currentData)
    }

    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl()
    }

    override val resultsRepository: ResultsRepository by lazy {
        ResultsRepositoryImpl(tarasDataService)
    }

    override val liveTimingRepository: LiveTimingRepository by lazy {
        LiveTimingRepositoryImpl(F1LiveRepository.instance)
    }

    override val tcgRepository: TcgRepository by lazy {
        TcgRepository(tcgCardDao)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(userPreferences)
    }

    override val appearanceRepository: AppearanceRepository by lazy {
        AppearanceRepositoryImpl(appearanceDataStore)
    }

    // Use Cases
    override val getDriversUseCase: GetDriversUseCase by lazy {
        GetDriversUseCase(driverRepository)
    }

    override val getTeamsUseCase: GetTeamsUseCase by lazy {
        GetTeamsUseCase(teamRepository)
    }

    override val getRaceCalendarUseCase: GetRaceCalendarUseCase by lazy {
        GetRaceCalendarUseCase(raceRepository)
    }

    override val getNewsUseCase: GetNewsUseCase by lazy {
        GetNewsUseCase(newsRepository)
    }

    override val getWeekendResultsUseCase: GetWeekendResultsUseCase by lazy {
        GetWeekendResultsUseCase(resultsRepository)
    }

    override val getLiveTimingUseCase: GetLiveTimingUseCase by lazy {
        GetLiveTimingUseCase(liveTimingRepository)
    }

    override val getLatestQualifyingPositionsUseCase: GetLatestQualifyingPositionsUseCase by lazy {
        GetLatestQualifyingPositionsUseCase(resultsRepository)
    }
}
