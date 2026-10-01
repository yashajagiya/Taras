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
                rssRepository.getF1News()
                    .onSuccess { items ->
                        _news.value = UiState.Success(items.toImmutableList())
                    }
                    .onFailure { e ->
                        Log.e(logTag, "Error fetching F1 news RSS feed", e)
                        _news.value = UiState.Error(e.message ?: "Failed to load F1 news")
                    }
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
