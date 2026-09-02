package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.DriverPerRaceResponce
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.taras.model.CareerStats
import com.example.taras.network_calls.taras.model.DriverPerRace
import com.example.taras.network_calls.taras.model.DriverSeasonStats
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.core.db.TopThreeDriversDAO
import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.network_calls.taras.model.Quote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.milliseconds

@Stable
class DriversViewModel(
    private val topThreeDriversDAO: TopThreeDriversDAO
) : ViewModel() {
    private val logTag = "DriversViewModel"

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _drivers = MutableStateFlow<UiState<DriverPerRaceResponce>>(UiState.Loading)
    val drivers = _drivers.asStateFlow()

    private val _driversInfoData =
        MutableStateFlow<UiState<ImmutableList<F1DriversInfoResponse>>>(UiState.Loading)
    val driversInfoData = _driversInfoData.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchDriverData(isRefresh = false)
    }


    // code for openf1 api
    // private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)

    //private val _drivers = MutableStateFlow<UiState<List<DriverStanding>>>(UiState.Loading)

//    private val _driverDetails = MutableStateFlow<UiState<List<DriverDetail>>>(UiState.Loading)
//    val driverDetails = _driverDetails.asStateFlow()

//    private val _driverDetails = MutableStateFlow<UiState<ImmutableList<DriverDetail>>>(UiState.Loading)
//    val driverDetails = _driverDetails.asStateFlow()


    val combinedLowDrivers = combine(_drivers, _driversInfoData) { driversState, driverinfoState ->
        if (driversState is UiState.Success) {
            val drivers = driversState.data.entries
            val info = (driverinfoState as? UiState.Success)?.data ?: emptyList()
            val combined = drivers.map { driver ->
                val detail = info.find { it.hero.number.toIntOrNull() == driver.driverNumber }
                DriverUiModel(
                    driverNumber = driver.driverNumber,
                    rank = driver.rank,
                    name = driver.name,
                    teamName = detail?.hero?.team ?: driver.teamName,
                    points = driver.championshipPts.displayValue,
                    teamColor = detail?.hero?.teamColor,
                    headshotUrl = detail?.hero?.driverImage,
                    carNumberImage = detail?.hero?.driverNumberLogo,
                    fullName = driver.name,
                    nationality = driver.nationality
                )
            }.toImmutableList()
            UiState.Success(combined)
        } else if (driversState is UiState.Error || driverinfoState is UiState.Error) {
            UiState.Error("Something went wrong")
        } else {
            UiState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    val topThree = combine(combinedLowDrivers, topThreeDriversDAO.getAll()) { state, dbList ->
        if (state is UiState.Success) {
            val top3 = state.data.take(3).toImmutableList()
            saveTopThreeToDb(top3)
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

    private fun saveTopThreeToDb(drivers: List<DriverUiModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entities = drivers.take(3).mapIndexed { index, uiModel ->
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

    val combinedDetailedDrivers =
        combine(_drivers, _driversInfoData) { driversState, driverInfoState ->

            if (driversState is UiState.Success && driverInfoState is UiState.Success) {
                val drivers = driversState.data.entries
                val infoList = driverInfoState.data

                val combined = drivers.map { driver ->
                    val detail =
                        infoList.find { it.hero.number.toIntOrNull() == driver.driverNumber }

                    DriverDetailUiModel(
                        rank = driver.rank,
                        driverNumber = driver.driverNumber?.toString() ?: driver.name,
                        slug = detail?.slug.orEmpty(),
                        url = detail?.url.orEmpty(),

                        fullName = driver.name,
                        firstName = detail?.hero?.firstName.orEmpty(),
                        lastName = detail?.hero?.lastName.orEmpty(),
                        shortName = driver.shortName,
                        abbreviation = driver.abbreviation,

                        nationality = driver.nationality,
                        country = detail?.hero?.country.orEmpty(),
                        teamName = detail?.hero?.team ?: driver.teamName,
                        teamColor = detail?.hero?.teamColor.orEmpty(),
                        accessibleColor = detail?.hero?.accessibleColor.orEmpty(),

                        headshotUrl = detail?.hero?.driverImage,
                        carNumberImage = detail?.hero?.driverNumberLogo,

                        dateOfBirth = detail?.biography?.dateOfBirth.orEmpty(),
                        placeOfBirth = detail?.biography?.placeOfBirth.orEmpty(),
                        bioText = detail?.biography?.text?.toImmutableList() ?: persistentListOf(),
                        quote = detail?.biography?.quote,

                        championshipPoints = driver.championshipPts.value,
                        championshipPointsDisplay = driver.championshipPts.displayValue,
                        races = driver.races.toImmutableList(),

                        seasonStats = detail?.seasonStats,
                        careerStats = detail?.careerStats
                    )
                }.toImmutableList()

                UiState.Success(combined)

            } else if (driversState is UiState.Error) {
                UiState.Error(driversState.message ?: "Error loading driver standings.")
            } else if (driverInfoState is UiState.Error) {
                UiState.Error(driverInfoState.message ?: "Error loading driver information.")
            } else {
                UiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun fetchDriverData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_drivers.value !is UiState.Success) {
                _drivers.value = UiState.Loading
                _driversInfoData.value = UiState.Loading
            }

            try {
                supervisorScope {

                    val driversDeferred =
                        async(Dispatchers.IO) { tarasDataService.getDriverStandings() }

                    val driversInfoDeferred =
                        async(Dispatchers.IO) { tarasDataService.getDriverInfoData() }

                    try {
                        _driversInfoData.value =
                            UiState.Success(driversInfoDeferred.await().toImmutableList())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching drivers info API", e)
                        _driversInfoData.value = UiState.Error("Error fetching drivers info")
                    }

                    try {
                        _drivers.value = UiState.Success(driversDeferred.await())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching driver standings API", e)
                        _drivers.value = UiState.Error("Error fetching driver standings")
                    }


                    //  val driversDeferred = async(Dispatchers.IO) { openF1Service.getDriverStandings() }
                    // val detailsDeferred = async(Dispatchers.IO) { tarasDataService.getDriverDetails() }

//                    val detailsDeferred =
//                        async(Dispatchers.IO) { tarasDataService.getDriverDetailsANDImage() }

//                    try {
//                        _driverDetails.value = UiState.Success(detailsDeferred.await())
//                    } catch (e: Exception) {
//                        Log.e(logTag, "Error fetching driver details API", e)
//                        _driverDetails.value = UiState.Error("Something went wrong")
//                    }

//                    try {
//                        _driverDetails.value = UiState.Success(detailsDeferred.await().toImmutableList())
//                    } catch (e: Exception) {
//                        Log.e(logTag, "Error fetching driver details AND image API", e)
//                        _driverDetails.value = UiState.Error("Error fetching driver details")
//                    }


                }
            } catch (e: Exception) {

                Log.e(logTag, "Error in fetchDriverData", e)
                _drivers.value = UiState.Error("Something went wrong")

                _driversInfoData.value = UiState.Error("Error fetching drivers info")


                // _driverDetails.value = UiState.Error("Something went wrong")

//                _driverDetails.value = UiState.Error("Something went wrong")
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

class DriversViewModelFactory(private val dao: TopThreeDriversDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriversViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriversViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}