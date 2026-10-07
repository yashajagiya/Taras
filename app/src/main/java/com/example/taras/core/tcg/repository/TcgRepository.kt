package com.example.taras.core.tcg.repository

import com.example.taras.core.db.tcg.TcgCardDao
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.db.tcg.UserCardInventoryEntity
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.seed.RosterSeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class TcgRepository(
    private val tcgCardDao: TcgCardDao
) {

    suspend fun ensureSeeded() {
        // Upsert all current 23 driver cards with latest API images and telemetry
        tcgCardDao.insertAllCards(RosterSeedData.allCards)
        
        // Purge any cards and inventory not in the official 23 cards roster
        val validIds = RosterSeedData.allCards.map { it.id }
        tcgCardDao.deleteCardsNotIn(validIds)
        tcgCardDao.deleteInventoryNotIn(validIds)

        // Seed initial starter inventory if user doesn't have any cards yet
        val currentInv = tcgCardDao.getUserInventory().first()
        if (currentInv.isEmpty()) {
            tcgCardDao.upsertInventoryItem(
                UserCardInventoryEntity(
                    cardId = "card_fer_44",
                    isHoloVariant = true,
                    isScratchCompleted = true
                )
            )
            tcgCardDao.upsertInventoryItem(
                UserCardInventoryEntity(
                    cardId = "card_rbr_03",
                    isHoloVariant = true,
                    isScratchCompleted = true
                )
            )
            tcgCardDao.upsertInventoryItem(
                UserCardInventoryEntity(
                    cardId = "card_mcl_01",
                    isHoloVariant = false,
                    isScratchCompleted = true
                )
            )
        }
    }

    fun getAllCards(): Flow<List<TcgCardEntity>> = tcgCardDao.getAllCards()

    fun getCardById(id: String): Flow<TcgCardEntity?> = tcgCardDao.getCardById(id)

    suspend fun getCardByIdDirect(id: String): TcgCardEntity? = tcgCardDao.getCardByIdDirect(id)

    fun getCardsByTeam(teamId: String): Flow<List<TcgCardEntity>> = tcgCardDao.getCardsByTeam(teamId)

    fun getCardsByTier(tier: RarityTier): Flow<List<TcgCardEntity>> = tcgCardDao.getCardsByTier(tier)

    fun getUserInventory(): Flow<List<UserCardInventoryEntity>> = tcgCardDao.getUserInventory()

    fun getUserCard(cardId: String): Flow<UserCardInventoryEntity?> = tcgCardDao.getUserCard(cardId)

    suspend fun unlockCard(cardId: String, isHolo: Boolean, isScratchCompleted: Boolean) {
        val existing = tcgCardDao.getUserCard(cardId).first()
        val newQuantity = (existing?.quantity ?: 0) + 1
        tcgCardDao.upsertInventoryItem(
            UserCardInventoryEntity(
                cardId = cardId,
                quantity = newQuantity,
                isHoloVariant = isHolo || (existing?.isHoloVariant == true),
                isScratchCompleted = isScratchCompleted
            )
        )
    }

    suspend fun completeScratch(cardId: String) {
        tcgCardDao.updateScratchCompleted(cardId, true)
    }

    /**
     * Draws a card according to the weekly pack drop probabilities:
     * - S-Tier: 5%
     * - A-Tier: 15%
     * - B-Tier: 35%
     * - C-Tier: 45%
     */
    suspend fun drawMysteryPackCard(): TcgCardEntity {
        val roll = Random.nextFloat() // 0.0 to 1.0
        val targetTier = when {
            roll < 0.05f -> RarityTier.S_TIER
            roll < 0.20f -> RarityTier.A_TIER
            roll < 0.55f -> RarityTier.B_TIER
            else -> RarityTier.C_TIER
        }

        val allInTier = RosterSeedData.allCards.filter { it.tier == targetTier }
        val pool = if (allInTier.isNotEmpty()) allInTier else RosterSeedData.allCards
        return pool.random()
    }
}
