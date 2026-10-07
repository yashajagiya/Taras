package com.example.taras.core.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taras.core.db.tcg.TcgCardDao
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.db.tcg.TcgConverters
import com.example.taras.core.db.tcg.UserCardInventoryEntity
import com.example.taras.core.tcg.seed.RosterSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Dao
interface TopThreeDriversDAO {

    @Query("SELECT * FROM TopThreeDriversEntity ORDER BY position ASC")
    fun getAll(): Flow<List<TopThreeDriversEntity>>

    @Upsert
    suspend fun insertAll(vararg topThreeDrivers: TopThreeDriversEntity)

}

@Database(
    entities = [
        TopThreeDriversEntity::class,
        TcgCardEntity::class,
        UserCardInventoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(TcgConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topThreeDriversDao(): TopThreeDriversDAO
    abstract fun tcgCardDao(): TcgCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taras_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate cards and starter inventory on DB creation
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.tcgCardDao()
                                    dao.insertAllCards(RosterSeedData.allCards)
                                    // Give user starter unlocked cards (e.g., Hamilton, Verstappen, Norris)
                                    dao.upsertInventoryItem(
                                        UserCardInventoryEntity(
                                            cardId = "card_fer_44",
                                            isHoloVariant = true,
                                            isScratchCompleted = true
                                        )
                                    )
                                    dao.upsertInventoryItem(
                                        UserCardInventoryEntity(
                                            cardId = "card_rbr_03",
                                            isHoloVariant = true,
                                            isScratchCompleted = true
                                        )
                                    )
                                    dao.upsertInventoryItem(
                                        UserCardInventoryEntity(
                                            cardId = "card_mcl_01",
                                            isHoloVariant = false,
                                            isScratchCompleted = true
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}