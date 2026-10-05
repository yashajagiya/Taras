package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.db.TopThreeDriversDAO
import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.core.repository.F1InfoRepository
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.CareerStats
import com.example.taras.network_calls.taras.model.DriverPerRace
import com.example.taras.network_calls.taras.model.DriverSeasonStats
import com.example.taras.network_calls.taras.model.Quote
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class DriversViewModel(
    private val topThreeDriversDAO: TopThreeDriversDAO,
    private val f1InfoRepository: F1InfoRepository = F1InfoRepository.instance
) : ViewModel() {
    private val logTag = "DriversViewModel"

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _driversList =
        MutableStateFlow<UiState<List<DriverDetailResponseV2>>>(UiState.Loading)
    val driversList = _driversList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchDriverData(isRefresh = false)
    }

    val combinedLowDrivers = _driversList.map { state ->
        when (state) {
            is UiState.Success -> {
                val combined = state.data.map { driver ->
                    DriverUiModel(
                        driverNumber = driver.number,
                        rank = driver.standings.rank,
                        name = driver.name,
                        teamName = driver.team.name,
                        points = driver.standings.points.toString(),
                        teamColor = driver.team.colorHex,
                        headshotUrl = driver.images.portrait,
                        carNumberImage = driver.images.numberLogo,
                        fullName = driver.name,
                        nationality = driver.nationality
                    )
                }.toImmutableList()
                UiState.Success(combined)
            }
            is UiState.Error -> UiState.Error(state.message)
            UiState.Loading -> UiState.Loading
        }
    }.onEach { state ->
        if (state is UiState.Success) {
            saveTopThreeToDb(state.data.take(3))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    val topThree = combine(combinedLowDrivers, topThreeDriversDAO.getAll()) { state, dbList ->
        if (state is UiState.Success) {
            val top3 = state.data.take(3).toImmutableList()
            UiState.Success(top3)
        } else if (dbList.isNotEmpty()) {
            val combined = dbList.map { entity ->
                DriverUiModel(
                    driverNumber = null,
                    rank = entity.position,
                    name = entity.name,
                    teamName = entity.team,
                    points = entity.points.toString(),
                    teamColor = null,
                    headshotUrl = null,
                    carNumberImage = null,
                    fullName = entity.name,
                    nationality = ""
                )
            }.toImmutableList()
            UiState.Success(combined)
        } else {
            state
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    private var lastSavedTopThree: List<DriverUiModel>? = null

    private fun saveTopThreeToDb(drivers: List<DriverUiModel>) {
        val top3 = drivers.take(3)
        if (top3 == lastSavedTopThree) return
        lastSavedTopThree = top3

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entities = top3.mapIndexed { index, uiModel ->
                    TopThreeDriversEntity(
                        id = index + 1,
                        position = uiModel.rank,
                        name = uiModel.name,
                        points = uiModel.points.filter { it.isDigit() || it == '.' }.toFloatOrNull()
                            ?: 0f,
                        team = uiModel.teamName
                    )
                }
                topThreeDriversDAO.insertAll(*entities.toTypedArray())
                Log.d(logTag, "Successfully saved top 3 drivers to DB")
            } catch (e: Exception) {
                Log.e(logTag, "Error saving top 3 drivers to DB", e)
            }
        }
    }

    val combinedDetailedDrivers = _driversList.map { state ->
        when (state) {
            is UiState.Success -> {
                val combined = state.data.map { driver ->
                    DriverDetailUiModel(
                        rank = driver.standings.rank,
                        driverNumber = driver.number?.toString() ?: driver.name,
                        slug = driver.id,
                        url = driver.f1Url,
                        fullName = driver.name,
                        firstName = driver.firstName,
                        lastName = driver.lastName,
                        shortName = driver.name,
                        abbreviation = driver.code,
                        nationality = driver.nationality,
                        country = driver.nationality,
                        teamName = driver.team.name,
                        teamColor = driver.team.colorHex.orEmpty(),
                        accessibleColor = driver.team.accessibleColor.orEmpty(),
                        headshotUrl = driver.images.portrait,
                        carNumberImage = driver.images.numberLogo,
                        dateOfBirth = driver.biography.dateOfBirth,
                        placeOfBirth = driver.biography.placeOfBirth,
                        bioText = driver.biography.paragraphs.toImmutableList(),
                        quote = driver.biography.quote,
                        championshipPoints = driver.standings.points,
                        championshipPointsDisplay = driver.standings.points.toString(),
                        races = driver.standings.races.map { r ->
                            DriverPerRace(
                                name = r.raceName,
                                displayName = r.raceCode,
                                played = r.played,
                                value = r.points,
                                displayValue = r.displayValue
                            )
                        }.toImmutableList(),
                        seasonStats = driver.seasonStats,
                        careerStats = driver.careerStats
                    )
                }.toImmutableList()
                UiState.Success(combined)
            }
            is UiState.Error -> UiState.Error(state.message)
            UiState.Loading -> UiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    fun fetchDriverData(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_driversList.value !is UiState.Success) {
                _driversList.value = UiState.Loading
            }

            try {
                val drivers = f1InfoRepository.getDriversV2(forceRefresh = isRefresh)
                _driversList.value = UiState.Success(drivers)
            } catch (e: Exception) {
                Log.e(logTag, "Error in fetchDriverData", e)
                _driversList.value = UiState.Error(e.toAppError())
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

@Immutable
data class DriverUiModel(
    val driverNumber: Int?,
    val rank: Int,
    val name: String,
    val teamName: String,
    val points: String,
    val teamColor: String?,
    val headshotUrl: String?,
    val carNumberImage: String?,
    val fullName: String?,
    val nationality: String
)

@Immutable
data class DriverDetailUiModel(
    val rank: Int,
    val driverNumber: String,
    val slug: String,
    val url: String,

    val fullName: String,
    val firstName: String,
    val lastName: String,
    val shortName: String,
    val abbreviation: String,

    val nationality: String,
    val country: String,
    val teamName: String,
    val teamColor: String,
    val accessibleColor: String,

    val headshotUrl: String?,
    val carNumberImage: String?,

    val dateOfBirth: String,
    val placeOfBirth: String,
    val bioText: ImmutableList<String>,
    val quote: Quote?,

    val championshipPoints: Int,
    val championshipPointsDisplay: String,
    val races: ImmutableList<DriverPerRace>,

    val seasonStats: DriverSeasonStats?,
    val careerStats: CareerStats?
)

fun DriverDetailUiModel.toDriverUiModel(): DriverUiModel {
    return DriverUiModel(
        driverNumber = driverNumber.toIntOrNull(),
        rank = rank,
        name = fullName,
        teamName = teamName,
        points = championshipPointsDisplay,
        teamColor = teamColor,
        headshotUrl = headshotUrl,
        carNumberImage = carNumberImage,
        fullName = fullName,
        nationality = nationality
    )
}

class DriversViewModelFactory(private val dao: TopThreeDriversDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriversViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriversViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}