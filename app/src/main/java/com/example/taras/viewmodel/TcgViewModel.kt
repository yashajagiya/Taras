package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.db.tcg.UserCardInventoryEntity
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.repository.TcgRepository
import com.example.taras.core.tcg.sound.TcgSoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BinderCardItem(
    val card: TcgCardEntity,
    val inventory: UserCardInventoryEntity?,
    val isUnlocked: Boolean,
    val isHolo: Boolean,
    val quantity: Int,
    val isScratchCompleted: Boolean
)

data class BinderUiState(
    val allBinderItems: List<BinderCardItem> = emptyList(),
    val filteredItems: List<BinderCardItem> = emptyList(),
    val selectedTier: RarityTier? = null,
    val selectedTeamId: String? = null,
    val totalCollected: Int = 0,
    val totalCards: Int = 23,
    val unrevealedCard: TcgCardEntity? = null
)

class TcgViewModel(
    private val repository: TcgRepository
) : ViewModel() {

    private val _selectedTier = MutableStateFlow<RarityTier?>(null)
    private val _selectedTeamId = MutableStateFlow<String?>(null)
    private val _activePackCard = MutableStateFlow<TcgCardEntity?>(null)

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val uiState: StateFlow<BinderUiState> = combine(
        repository.getAllCards(),
        repository.getUserInventory(),
        _selectedTier,
        _selectedTeamId,
        _activePackCard
    ) { allCards, inventoryList, selectedTier, selectedTeamId, activePackCard ->
        val inventoryMap = inventoryList.associateBy { it.cardId }

        val allItems = allCards.map { card ->
            val userEntry = inventoryMap[card.id]
            BinderCardItem(
                card = card,
                inventory = userEntry,
                isUnlocked = userEntry != null,
                isHolo = userEntry?.isHoloVariant == true,
                quantity = userEntry?.quantity ?: 0,
                isScratchCompleted = userEntry?.isScratchCompleted ?: false
            )
        }

        val filtered = allItems.filter { item ->
            val tierMatch = selectedTier == null || item.card.tier == selectedTier
            val teamMatch = selectedTeamId == null || item.card.teamId.equals(selectedTeamId, ignoreCase = true)
            tierMatch && teamMatch
        }

        val collectedCount = allItems.count { it.isUnlocked }
        val pendingPackCard = activePackCard ?: allItems.firstOrNull { it.isUnlocked && !it.isScratchCompleted }?.card

        BinderUiState(
            allBinderItems = allItems,
            filteredItems = filtered,
            selectedTier = selectedTier,
            selectedTeamId = selectedTeamId,
            totalCollected = collectedCount,
            totalCards = allCards.size.coerceAtLeast(23),
            unrevealedCard = pendingPackCard
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BinderUiState()
    )

    fun selectTierFilter(tier: RarityTier?) {
        _selectedTier.value = tier
    }

    fun selectTeamFilter(teamId: String?) {
        _selectedTeamId.value = teamId
    }

    fun completeScratch(cardId: String) {
        viewModelScope.launch {
            repository.completeScratch(cardId)
            _activePackCard.value = null
            TcgSoundManager.playRevealChime()
        }
    }

    fun openMysteryPack(onDrawn: (TcgCardEntity) -> Unit) {
        viewModelScope.launch {
            val drawn = repository.drawMysteryPackCard()
            repository.unlockCard(
                cardId = drawn.id,
                isHolo = (drawn.tier == RarityTier.S_TIER),
                isScratchCompleted = false
            )
            _activePackCard.value = drawn
            TcgSoundManager.playRadioBeep()
            onDrawn(drawn)
        }
    }
}
