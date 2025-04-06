package com.example.rpg_character_sheet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Character::class], version = 1, exportSchema = false)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var instance: CharacterDatabase? = null

        fun getDatabase(context: Context): CharacterDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): CharacterDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CharacterDatabase::class.java,
                "character_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}