package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.common.UiState
import com.example.taras.rss.RssItem
import com.example.taras.rss.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val rssRepository = RssRepository()
    private val _news = MutableStateFlow<UiState<List<RssItem>>>(UiState.Loading)
    val news: StateFlow<UiState<List<RssItem>>> = _news.asStateFlow()

    init {
        fetchNews()
    }

    private fun fetchNews() {
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