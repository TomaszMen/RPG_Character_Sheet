package com.example.rpg_character_sheet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import table_entities.Character
import table_entities.Item
import table_entities.Weapon
import table_entities.Armor
import table_entities.Spell
import table_entities.AbilityScore
import table_entities.Alignment
import table_entities.Background
import table_entities.CharacterClass
import table_entities.CharacterCurrency
import table_entities.CharacterDeathSaves
import table_entities.CharacterFeature
import table_entities.CharacterInventory
import table_entities.CharacterLanguage
import table_entities.CharacterSavingThrow
import table_entities.CharacterSkill
import table_entities.CharacterSpell
import table_entities.CharacterSpellSlot
import table_entities.Feature
import table_entities.Subrace
import table_entities.Subclass
import table_entities.Skill
import table_entities.Race
import table_entities.Language
import table_entities.ClassSpell

@Database(entities = [
    Character::class,
    Item::class,
    Weapon::class,
    Armor::class,
    Spell::class,
    AbilityScore::class,
    Alignment::class,
    Background::class,
    CharacterClass::class,
    CharacterCurrency::class,
    CharacterDeathSaves::class,
    CharacterFeature::class,
    CharacterInventory::class,
    CharacterLanguage::class,
    CharacterSavingThrow::class,
    CharacterSkill::class,
    CharacterSpell::class,
    CharacterSpellSlot::class,
    Feature::class,
    Subrace::class,
    Subclass::class,
    Skill::class,
    Race::class,
    Language::class,
    ClassSpell::class
                     ], version = 2, exportSchema = false)

abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: CharacterDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
            }
        }

        fun getDatabase(context: Context): CharacterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CharacterDatabase::class.java,
                    "character_database"
                )   .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .createFromAsset("project_database.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}