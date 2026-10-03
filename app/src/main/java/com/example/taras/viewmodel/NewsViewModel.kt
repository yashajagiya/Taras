package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.rss.NewsSource
import com.example.taras.network_calls.rss.NewsSources
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.rss.RssRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class NewsViewModel(
    private val rssRepository: RssRepository = RssRepository()
) : ViewModel() {

    private val logTag = "NewsViewModel"

    private val _rawNews = MutableStateFlow<UiState<List<RssItem>>>(UiState.Loading)

    private val _selectedSourceId = MutableStateFlow(NewsSources.ALL.id)
    val selectedSourceId: StateFlow<String> = _selectedSourceId.asStateFlow()

    val availableSources: List<NewsSource> = NewsSources.FILTER_OPTIONS

    val news: StateFlow<UiState<ImmutableList<RssItem>>> = combine(
        _rawNews,
        _selectedSourceId
    ) { rawState, filterId ->
        when (rawState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(rawState.message)
            is UiState.Success -> {
                val filtered = if (filterId == NewsSources.ALL.id) {
                    rawState.data
                } else {
                    rawState.data.filter { it.sourceId == filterId }
                }
                UiState.Success(filtered.toImmutableList())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchNews(isRefresh = false)
    }

    fun setSourceFilter(sourceId: String) {
        _selectedSourceId.value = sourceId
    }

    fun fetchNews(isRefresh: Boolean = false) {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_rawNews.value !is UiState.Success) {
                _rawNews.value = UiState.Loading
            }
            try {
                rssRepository.getF1News()
                    .onSuccess { items ->
                        _rawNews.value = UiState.Success(items)
                    }
                    .onFailure { e ->
                        Log.e(logTag, "Error fetching multi-source F1 news feeds", e)
                        _rawNews.value = UiState.Error(e.message ?: "Failed to load F1 news")
                    }
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                Log.e(logTag, "Error fetching multi-source F1 news feeds", e)
                _rawNews.value = UiState.Error(e.message ?: "Failed to load F1 news")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
