package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.rss.RssRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Stable
class NewsViewModel(
    private val rssRepository: RssRepository = RssRepository()
) : ViewModel() {

    private val logTag = "NewsViewModel"

    private val _news = MutableStateFlow<UiState<ImmutableList<RssItem>>>(UiState.Loading)
    val news: StateFlow<UiState<ImmutableList<RssItem>>> = _news.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch(context = Dispatchers.IO) {
            _news.value = UiState.Loading
            try {
                val newsItems = rssRepository.getF1News().toImmutableList()
                _news.value = UiState.Success(newsItems)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                Log.e(logTag, "Error fetching F1 news RSS feed", e)
                _news.value = UiState.Error(e.message ?: "Failed to load F1 news")
            }
        }
    }
}