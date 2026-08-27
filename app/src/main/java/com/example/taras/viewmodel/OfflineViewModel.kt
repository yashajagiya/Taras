package com.example.taras.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taras.core.common.CurrentData

class OfflineViewModel(context: Context) : ViewModel() {

    private val log = "OfflineViewModel"

    private val currentData = CurrentData(context)

    val currentSessionStatus = currentData.isCurrentSessionSaved



}