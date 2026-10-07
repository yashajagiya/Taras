package com.example.taras.core.db.tcg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.taras.core.tcg.model.RarityTier
import kotlinx.coroutines.flow.Flow

@Dao
interface TcgCardDao {

    @Query("SELECT * FROM tcg_cards ORDER BY cardNumber ASC")
    fun getAllCards(): Flow<List<TcgCardEntity>>

    @Query("SELECT * FROM tcg_cards WHERE id = :id LIMIT 1")
    fun getCardById(id: String): Flow<TcgCardEntity?>

    @Query("SELECT * FROM tcg_cards WHERE id = :id LIMIT 1")
    suspend fun getCardByIdDirect(id: String): TcgCardEntity?

    @Query("SELECT * FROM tcg_cards WHERE teamId = :teamId ORDER BY cardNumber ASC")
    fun getCardsByTeam(teamId: String): Flow<List<TcgCardEntity>>

    @Query("SELECT * FROM tcg_cards WHERE tier = :tier ORDER BY cardNumber ASC")
    fun getCardsByTier(tier: RarityTier): Flow<List<TcgCardEntity>>

    @Query("SELECT COUNT(*) FROM tcg_cards")
    suspend fun getCardCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCards(cards: List<TcgCardEntity>)

    @Query("DELETE FROM tcg_cards WHERE id NOT IN (:validIds)")
    suspend fun deleteCardsNotIn(validIds: List<String>)

    @Query("DELETE FROM user_tcg_inventory WHERE cardId NOT IN (:validIds)")
    suspend fun deleteInventoryNotIn(validIds: List<String>)

    // --- User Inventory Queries ---

    @Query("SELECT * FROM user_tcg_inventory")
    fun getUserInventory(): Flow<List<UserCardInventoryEntity>>

    @Query("SELECT * FROM user_tcg_inventory WHERE cardId = :cardId LIMIT 1")
    fun getUserCard(cardId: String): Flow<UserCardInventoryEntity?>

    @Upsert
    suspend fun upsertInventoryItem(item: UserCardInventoryEntity)

    @Query("UPDATE user_tcg_inventory SET isScratchCompleted = :isCompleted WHERE cardId = :cardId")
    suspend fun updateScratchCompleted(cardId: String, isCompleted: Boolean)
}
