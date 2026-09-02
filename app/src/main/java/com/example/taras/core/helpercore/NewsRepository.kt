package com.example.taras.core.helpercore

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
class NewsRepository(
    private val rssRepository: RssRepository = RssRepository()
) : ViewModel() {

    private val logTag = "NewsRepository"

    private val _news = MutableStateFlow<UiState<ImmutableList<com.example.taras.network_calls.rss.RssItem>>>(UiState.Loading)
    val news: StateFlow<UiState<ImmutableList<com.example.taras.network_calls.rss.RssItem>>> = _news.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchNews(isRefresh = false)
    }

    fun fetchNews(isRefresh: Boolean = false) {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_news.value !is UiState.Success) {
                _news.value = UiState.Loading
            }
            try {
                val newsItems = rssRepository.getF1News().toImmutableList()
                _news.value = UiState.Success(newsItems)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                Log.e(logTag, "Error fetching F1 news RSS feed", e)
                _news.value = UiState.Error(e.message ?: "Failed to load F1 news")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}