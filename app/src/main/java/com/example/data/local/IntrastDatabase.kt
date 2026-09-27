package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlayerProfileEntity::class,
        HeroProgressEntity::class,
        StageProgressEntity::class,
        InventoryEntity::class,
        CloudSaveEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IntrastDatabase : RoomDatabase() {
    abstract fun dao(): IntrastDao

    companion object {
        @Volatile
        private var INSTANCE: IntrastDatabase? = null

        fun getDatabase(context: Context): IntrastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IntrastDatabase::class.java,
                    "intrast_rpg_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
