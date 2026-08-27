package com.example.taras.core.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TopThreeDriversDAO {

    @Query("SELECT * FROM TopThreeDriversEntity")
    fun getAll(): Flow<List<TopThreeDriversEntity>>

    @Upsert
    suspend fun insertAll(vararg topThreeDrivers: TopThreeDriversEntity)

}

@Database(entities = [TopThreeDriversEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topThreeDriversDao(): TopThreeDriversDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}