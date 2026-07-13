package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.rss.RssRepository
import com.prof18.rssparser.model.RssItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val rssRepository = RssRepository()
    private val _news = MutableStateFlow<List<RssItem>>(emptyList())
    val news: StateFlow<List<RssItem>> = _news.asStateFlow()

    init {
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            _news.value = rssRepository.getF1News()
        }
    }
}