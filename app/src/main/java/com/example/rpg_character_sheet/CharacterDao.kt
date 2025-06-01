package com.example.rpg_character_sheet

import androidx.lifecycle.LiveData
import androidx.room.*
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

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): LiveData<List<Character>>

    @Query("SELECT * FROM races")
    fun getAllRaces(): LiveData<List<Race>>

    @Query("SELECT * FROM classes")
    fun getAllClasses(): LiveData<List<CharacterClass>>

    @Query("SELECT * FROM backgrounds")
    fun getAllBackgrounds(): LiveData<List<Background>>

    @Query("SELECT * FROM alignments")
    fun getAllAlignments(): LiveData<List<Alignment>>

    @Query("SELECT * FROM characters WHERE characterId = :id")
    fun getCharacterById(id: Int): LiveData<Character>

    @Query("SELECT * FROM subraces WHERE raceId = :raceId")
    suspend fun getSubracesByRaceId(raceId: Int): List<Subrace>

    @Query("SELECT * FROM subclasses WHERE classId = :classId")
    suspend fun getSubclassesByClassId(classId: Int): List<Subclass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: java.lang.Character): Long

    @Update
    suspend fun updateCharacter(character: java.lang.Character)

    @Delete
    suspend fun deleteCharacter(character: java.lang.Character)

}