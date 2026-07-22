package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.rss.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class NewsViewModel : ViewModel() {
    private val rssRepository = RssRepository()
    private val _news = MutableStateFlow<UiState<List<RssItem>>>(UiState.Loading)
    val news: StateFlow<UiState<List<RssItem>>> = _news.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch (Dispatchers.IO){
            _news.value = UiState.Loading
            try {
                _news.value = UiState.Success(rssRepository.getF1News())
            } catch (e: Exception) {
                _news.value = UiState.Error("Something went wrong")
            }
        }
    }
}