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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CharacterDao {
    // Character queries
    @Insert
    suspend fun insert(character: Character)

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * FROM characters ORDER BY characterName ASC")
    fun getAllCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacterById(characterId: Int): Flow<Character>

    @Query("UPDATE characters SET characterName = :newName WHERE characterId = :characterId")
    suspend fun updateCharacterName(characterId: Int, newName: String)

    @Query("UPDATE characters SET level = :newLevel WHERE characterId = :characterId")
    suspend fun updateCharacterLevel(characterId: Int, newLevel: Int)

    // Class queries
    @Query("SELECT * FROM classes WHERE classId = :classId")
    fun getClassById(classId: Int): Flow<CharacterClass>

    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<CharacterClass>>

    @Query("UPDATE characters SET classId = :classId WHERE characterId = :characterId")
    suspend fun updateCharacterClass(characterId: Int, classId: Int)

    // Subclass queries
    @Query("SELECT * FROM subclasses WHERE subclassId = :subclassId")
    fun getSubclassById(subclassId: Int): Flow<Subclass>

    @Query("SELECT * FROM subclasses")
    fun getAllSubclasses(): Flow<List<Subclass>>

    @Query("SELECT * FROM subclasses JOIN classes ON subclasses.classId = classes.classId WHERE subclasses.classId = :classId")
    fun getSubclassesOfClass(classId: Int): Flow<List<Subclass>>

    @Query("UPDATE characters SET subclassId = :subclassId WHERE characterId = :characterId")
    suspend fun updateCharacterSubclass(characterId: Int, subclassId: Int)

    // Race queries
    @Query("SELECT * FROM races WHERE raceId = :raceId")
    fun getRaceById(raceId: Int): Flow<Race>

    @Query("SELECT * FROM races")
    fun getAllRaces(): Flow<List<Race>>

    @Query("UPDATE characters SET raceId = :raceId WHERE characterId = :characterId")
    suspend fun updateCharacterRace(characterId: Int, raceId: Int)

    // Subrace queries
    @Query("SELECT * FROM subraces WHERE subraceId = :subraceId")
    fun getSubraceById(subraceId: Int): Flow<Subrace>

    @Query("SELECT * FROM subraces")
    fun getAllSubraces(): Flow<List<Subrace>>

    @Query("SELECT * FROM subraces JOIN races ON subraces.raceId = races.raceId WHERE subraces.raceId = :raceId")
    fun getSubracesOfRace(raceId: Int): Flow<List<Subrace>>

    @Query("UPDATE characters SET subraceId = :subraceId WHERE characterId = :characterId")
    suspend fun updateCharacterSubrace(characterId: Int, subraceId: Int)

    // Background queries
    @Query("SELECT * FROM backgrounds WHERE backgroundId = :backgroundId")
    fun getBackgroundById(backgroundId: Int): Flow<Background>

    // Alignment queries
    @Query("SELECT * FROM alignments WHERE alignmentId = :alignmentId")
    fun getAlignmentById(alignmentId: Int): Flow<Alignment>

    // Stats
    @Query("UPDATE characters SET strength = :STR, dexterity = :DEX, constitution = :CON, " +
            "intelligence = :INT, wisdom = :WIS, charisma = :CHA WHERE characterId = :characterId")
    suspend fun updateCharacterStats(characterId: Int, STR: Int, DEX: Int, CON: Int, INT: Int, WIS: Int, CHA: Int)

    @Query("UPDATE characters SET dexterity = :DEX WHERE characterId = :characterId")
    suspend fun updateCharacterDexterity(characterId: Int, DEX: Int)

    @Query("UPDATE characters SET dexterity = :CON WHERE characterId = :characterId")
    suspend fun updateCharacterConstitution(characterId: Int, CON: Int)

    @Query("UPDATE characters SET dexterity = :INT WHERE characterId = :characterId")
    suspend fun updateCharacterIntelligence(characterId: Int, INT: Int)

    @Query("UPDATE characters SET dexterity = :WIS WHERE characterId = :characterId")
    suspend fun updateCharacterWisdom(characterId: Int, WIS: Int)

    @Query("UPDATE characters SET dexterity = :CHA WHERE characterId = :characterId")
    suspend fun updateCharacterCharisma(characterId: Int, CHA: Int)

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM character_inventory WHERE characterId = :characterId")
    fun getCharacterInventory(characterId: Int): Flow<List<CharacterInventory>>

    @Insert
    suspend fun insertCharacterInventory(characterInventory: CharacterInventory)

    @Delete
    suspend fun deleteCharacterInventory(characterInventory: CharacterInventory)

    @Update
    suspend fun updateCharacterInventory(updated: CharacterInventory)

    @Transaction
    @Query(" SELECT * FROM weapons WHERE itemId IN ( SELECT itemId FROM character_inventory WHERE characterId = :characterId AND equipped = 1)")
    fun getCharacterWeapons(characterId: Int): Flow<List<WeaponAndItem>>

    @Query("SELECT s.* FROM spells s JOIN character_spells cs ON s.spellId = cs.spellId WHERE cs.characterId = :characterId")
    fun getCharacterSpells(characterId: Int): Flow<List<Spell>>

    @Query("SELECT f.* FROM features f JOIN character_features cf ON f.featureId = cf.featureId WHERE cf.characterId = :characterId")
    fun getCharacterFeatures(characterId: Int): Flow<List<Feature>>

    @Query("SELECT * FROM character_spell_slots WHERE characterId = :characterId")
    fun getCharacterSpellSlots(characterId: Int): Flow<List<CharacterSpellSlot>>
}
data class WeaponAndItem(
    @Embedded val weapon: Weapon,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "itemId"
    )
    val item: Item
)