package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.api.apiClass.F1ApiObject
import com.example.taras.api.dataclass.championship_Driver.DriverschampionshipDataClassItem
import com.example.taras.api.dataclass.championship_Team.TeamsChampionshipDataClassItem
import com.example.taras.api.dataclass.driverData.DriverDetailDataClassItem
import com.example.taras.rss.RssRepository
import com.prof18.rssparser.model.RssItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class F1DataViewModel : ViewModel() {
    private val logtagViewModel = "logF1DataViewModel"

    private val apiF1DataInterface = F1ApiObject.getApiservice()
    private val rssRepository = RssRepository()

    private val _drivers = MutableStateFlow<List<DriverschampionshipDataClassItem>>(emptyList())
    private val _teams = MutableStateFlow<List<TeamsChampionshipDataClassItem>>(emptyList())
    private val _driverdetail = MutableStateFlow<List<DriverDetailDataClassItem>>(emptyList())
    private val _news = MutableStateFlow<List<RssItem>>(emptyList())

    val drivers = _drivers.asStateFlow()
    val teams: StateFlow<List<TeamsChampionshipDataClassItem>> = _teams.asStateFlow()
    val driverdetail: StateFlow<List<DriverDetailDataClassItem>> = _driverdetail.asStateFlow()
    val news: StateFlow<List<RssItem>> = _news.asStateFlow()

    init {
        fetchapiData()
    }

    private fun fetchapiData() {
        viewModelScope.launch {
            try {
                val driverResponse = async { apiF1DataInterface.getDriverStanding() }
                _drivers.value = driverResponse.await()
                val teamesResponse = async { apiF1DataInterface.getTeamStanding() }
                _teams.value = teamesResponse.await()
                val driverdetailsResponse = async { apiF1DataInterface.getDriverDetails() }
                _driverdetail.value = driverdetailsResponse.await()

                val newsResponse = async { rssRepository.getF1News() }
                _news.value = newsResponse.await()

            } catch (e: Exception) {
                Log.d(logtagViewModel, "driver data ${e.toString()}")
            }
        }
    }
}