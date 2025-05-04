package com.example.rpg_character_sheet

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(
    tableName = "character_languages",
    foreignKeys = [ForeignKey(
        entity = Character::class,
        parentColumns = ["id"],
        childColumns = ["characterId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["characterId"])]
)
abstract class LanguageDatabase : RoomDatabase() {
    abstract fun languageDao(): LanguageDao

    companion object {
        @Volatile
        private var INSTANCE: LanguageDatabase? = null

        fun getDatabase(context: Context): LanguageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LanguageDatabase::class.java,
                    "language_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}