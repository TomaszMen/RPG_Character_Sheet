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

    @Query("SELECT * FROM backgrounds")
    fun getAllBackgrounds(): Flow<List<Background>>

    @Query("SELECT * FROM alignments")
    fun getAllAlignments(): Flow<List<Alignment>>

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

    @Query("SELECT * FROM features WHERE sourceType = 'Race' AND sourceId = :raceId")
    fun getRaceFeatures(raceId: Int): Flow<List<Feature>>

    @Query("SELECT * FROM features WHERE sourceType = 'Subrace' AND sourceId = :subraceId")
    fun getSubraceFeatures(subraceId: Int): Flow<List<Feature>>

    @Query("SELECT * FROM features WHERE sourceType = 'Class' AND sourceId = :classId AND levelRequirement = 1")
    fun getClassFeatures(classId: Int): Flow<List<Feature>>

    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<Skill>>

    @Query("SELECT * FROM class_spells WHERE classId = :classId")
    fun getClassSpells(classId: Int): Flow<List<ClassSpell>>

   @Query("SELECT * FROM spells WHERE spellId IN (:spellIds)")
    fun getSpellsByIds(spellIds: List<Int>): Flow<List<Spell>>

    @Insert
    suspend fun insertCharacterSkill(characterSkill: CharacterSkill)

    // Get character skills for the selected character
    @Query("SELECT * FROM character_skills WHERE characterId = :characterId")
    fun getCharacterSkills(characterId: Int): Flow<List<CharacterSkill>>

    // Get character saving throws for the selected character
    @Query("SELECT * FROM character_saving_throws WHERE characterId = :characterId")
    fun getCharacterSavingThrows(characterId: Int): Flow<List<CharacterSavingThrow>>

    // Get character skills with skill names (optional, ale przydatne)
    @Transaction
    @Query("""
        SELECT cs.*, s.skillName, s.abilityScore 
        FROM character_skills cs 
        JOIN skills s ON cs.skillId = s.skillId 
        WHERE cs.characterId = :characterId
    """)
    fun getCharacterSkillsWithDetails(characterId: Int): Flow<List<CharacterSkillWithDetails>>

    // Update HP
    @Query("UPDATE characters SET currentHitPoints = :currentHP WHERE characterId = :characterId")
    suspend fun updateCurrentHP(characterId: Int, currentHP: Int)

    // Update temporary HP
    @Query("UPDATE characters SET temporaryHitPoints = :tempHP WHERE characterId = :characterId")
    suspend fun updateTemporaryHP(characterId: Int, tempHP: Int)

    // Update AC
    @Query("UPDATE characters SET armorClass = :ac WHERE characterId = :characterId")
    suspend fun updateArmorClass(characterId: Int, ac: Int)

    // Update initiative
    @Query("UPDATE characters SET initiative = :initiative WHERE characterId = :characterId")
    suspend fun updateInitiative(characterId: Int, initiative: Int)

    // Get equipped armor AC bonus
    @Query("""
        SELECT COALESCE(SUM(a.armorClass), 0) 
        FROM character_inventory ci 
        JOIN items i ON ci.itemId = i.itemId 
        LEFT JOIN armors a ON ci.itemId = a.itemId 
        WHERE ci.characterId = :characterId 
        AND ci.equipped = 1 
        AND i.itemType = 'Armor'
    """)
    fun getEquippedArmorAC(characterId: Int): Flow<Int>

    // Get shield AC bonus
    @Query("""
        SELECT COALESCE(SUM(a.armorClass), 0) 
        FROM character_inventory ci 
        JOIN items i ON ci.itemId = i.itemId 
        LEFT JOIN armors a ON ci.itemId = a.itemId 
        WHERE ci.characterId = :characterId 
        AND ci.equipped = 1 
        AND a.armorType = 'Shield'
    """)
    fun getEquippedShieldAC(characterId: Int): Flow<Int>

    // Calculate total AC based on equipped items and stats
    @Transaction
    @Query("""
        SELECT 
            c.*,
            COALESCE(SUM(a.armorClass), 0) as armorBonus,
            COALESCE(SUM(s.armorClass), 0) as shieldBonus
        FROM characters c
        LEFT JOIN character_inventory ci ON c.characterId = ci.characterId AND ci.equipped = 1
        LEFT JOIN items i ON ci.itemId = i.itemId
        LEFT JOIN armors a ON ci.itemId = a.itemId AND a.armorType != 'Shield'
        LEFT JOIN armors s ON ci.itemId = s.itemId AND s.armorType = 'Shield'
        WHERE c.characterId = :characterId
        GROUP BY c.characterId
    """)
    fun getCharacterWithACCalculation(characterId: Int): Flow<CharacterWithAC>

    @Insert
    suspend fun insertCharacterLanguage(characterLanguage: CharacterLanguage)

    @Insert
    suspend fun insertCharacterSpell(characterSpell: CharacterSpell)

    @Insert
    suspend fun insertAndGetId(character: Character): Long

    @Query("SELECT * FROM items WHERE itemType = 'Gear' OR itemType = 'Tool'")
    fun getStarterEquipment(): Flow<List<Item>>

    // Get character's equipped items
    @Query("SELECT * FROM character_inventory WHERE characterId = :characterId AND equipped = 1")
    fun getEquippedItems(characterId: Int): Flow<List<CharacterInventory>>

    // Update equipped status
    @Query("UPDATE character_inventory SET equipped = :equipped WHERE inventoryId = :inventoryId")
    suspend fun updateEquippedStatus(inventoryId: Int, equipped: Boolean)

    // Count equipped weapons
    @Query("SELECT COUNT(*) FROM character_inventory ci JOIN items i ON ci.itemId = i.itemId WHERE ci.characterId = :characterId AND ci.equipped = 1 AND i.itemType = 'Weapon'")
    suspend fun countEquippedWeapons(characterId: Int): Int

    // Get equipped armor
    @Query("SELECT * FROM character_inventory ci JOIN items i ON ci.itemId = i.itemId WHERE ci.characterId = :characterId AND ci.equipped = 1 AND i.itemType = 'Armor'")
    fun getEquippedArmor(characterId: Int): Flow<List<CharacterInventory>>

    // Get weapons (for filtering)
    @Query("SELECT * FROM items WHERE itemType = 'Weapon'")
    fun getAllWeapons(): Flow<List<Item>>

    // Get armor (for filtering)
    @Query("SELECT * FROM items WHERE itemType = 'Armor'")
    fun getAllArmor(): Flow<List<Item>>

    // Get other items (for filtering)
    @Query("SELECT * FROM items WHERE itemType NOT IN ('Weapon', 'Armor')")
    fun getOtherItems(): Flow<List<Item>>

    // Get available feats for a given level and class
    @Query("""
    SELECT * FROM features 
    WHERE sourceType = 'Feat' 
    AND levelRequirement <= :level
    ORDER BY levelRequirement ASC
    """)
    fun getAvailableFeatsByLevel(level: Int): Flow<List<Feature>>

    // Get class features available at specific level
    @Query("""
    SELECT * FROM features 
    WHERE sourceType IN ('Class', 'Subclass') 
    AND sourceId = :classId 
    AND levelRequirement <= :level
    ORDER BY levelRequirement ASC
    """)
    fun getClassFeaturesByLevel(classId: Int, level: Int): Flow<List<Feature>>

    // Get subclasses for a specific class
    @Query("SELECT * FROM subclasses WHERE classId = :classId")
    fun getSubclassesForClass(classId: Int): Flow<List<Subclass>>

    // Insert a character feature
    @Insert
    suspend fun insertCharacterFeature(characterFeature: CharacterFeature)

    // Delete a character feature by characterId and featureId
    @Query("""
    DELETE FROM character_features 
    WHERE characterId = :characterId AND featureId = :featureId
    """)
    suspend fun deleteCharacterFeature(characterId: Int, featureId: Int)

    // Check if a character already has a specific feature
    @Query("""
    SELECT COUNT(*) FROM character_features 
    WHERE characterId = :characterId AND featureId = :featureId
    """)
    suspend fun hasCharacterFeature(characterId: Int, featureId: Int): Int

    @Query("UPDATE character_spell_slots SET usedSlots = :usedSlots WHERE spellSlotId = :spellSlotId")
    suspend fun updateSpellSlotUsed(spellSlotId: Int, usedSlots: Int)

    // Add this function for deleting character spells by characterId and spellId
    @Query("DELETE FROM character_spells WHERE characterId = :characterId AND spellId = :spellId")
    suspend fun deleteCharacterSpell(characterId: Int, spellId: Int)

    // Add this function for getting all spells (for filtering)
    @Query("SELECT * FROM spells ORDER BY spellLevel ASC, spellName ASC")
    fun getAllSpells(): Flow<List<Spell>>

    // Add this function for getting spells by level
    @Query("SELECT * FROM spells WHERE spellLevel = :level ORDER BY spellName ASC")
    fun getSpellsByLevel(level: Int): Flow<List<Spell>>

    // Add this function for getting spells by class and level
    @Query("""
    SELECT s.* FROM spells s
    JOIN class_spells cs ON s.spellId = cs.spellId
    WHERE cs.classId = :classId AND s.spellLevel <= :maxLevel
    ORDER BY s.spellLevel ASC, s.spellName ASC
""")
    fun getSpellsByClassAndLevel(classId: Int, maxLevel: Int): Flow<List<Spell>>

    // Add this function for getting spells by school
    @Query("SELECT * FROM spells WHERE school = :school ORDER BY spellLevel ASC, spellName ASC")
    fun getSpellsBySchool(school: String): Flow<List<Spell>>

    // Add this function for inserting character spell slots
    @Insert
    suspend fun insertCharacterSpellSlot(characterSpellSlot: CharacterSpellSlot)

    // Add this function for deleting character spell slots
    @Query("DELETE FROM character_spell_slots WHERE characterId = :characterId")
    suspend fun deleteCharacterSpellSlots(characterId: Int)

    // Add this function for getting character spell slots by characterId and level
    @Query("SELECT * FROM character_spell_slots WHERE characterId = :characterId AND spellLevel = :spellLevel")
    fun getCharacterSpellSlot(characterId: Int, spellLevel: Int): Flow<CharacterSpellSlot?>

    // Add this function for updating total spell slots
    @Query("UPDATE character_spell_slots SET totalSlots = :totalSlots WHERE spellSlotId = :spellSlotId")
    suspend fun updateSpellSlotTotal(spellSlotId: Int, totalSlots: Int)

    // Add this function for getting prepared spells for a character
    @Query("""
    SELECT s.* FROM spells s
    JOIN character_spells cs ON s.spellId = cs.spellId
    WHERE cs.characterId = :characterId AND cs.prepared = 1
    ORDER BY s.spellLevel ASC, s.spellName ASC
""")
    fun getPreparedSpells(characterId: Int): Flow<List<Spell>>

    // Add this function for updating spell preparation status
    @Query("UPDATE character_spells SET prepared = :prepared WHERE characterId = :characterId AND spellId = :spellId")
    suspend fun updateSpellPreparation(characterId: Int, spellId: Int, prepared: Int)

    // Add this function for checking if a character has a specific spell
    @Query("SELECT COUNT(*) FROM character_spells WHERE characterId = :characterId AND spellId = :spellId")
    suspend fun hasSpell(characterId: Int, spellId: Int): Int

    // Add this function for getting character spells with preparation status
    @Query("""
    SELECT s.*, cs.prepared FROM spells s
    JOIN character_spells cs ON s.spellId = cs.spellId
    WHERE cs.characterId = :characterId
    ORDER BY s.spellLevel ASC, s.spellName ASC
""")
    fun getCharacterSpellsWithPreparation(characterId: Int): Flow<List<SpellWithPreparation>>

    // Add this data class for spells with preparation status
    data class SpellWithPreparation(
        @Embedded val spell: Spell,
        val prepared: Int
    )

    // Add this function for getting spell slots summary
    @Query("""
    SELECT 
        css.spellLevel,
        SUM(css.totalSlots) as totalSlots,
        SUM(css.usedSlots) as usedSlots
    FROM character_spell_slots css
    JOIN characters c ON css.characterId = c.characterId
    WHERE c.characterId = :characterId
    GROUP BY css.spellLevel
    ORDER BY css.spellLevel ASC
""")
    fun getSpellSlotsSummary(characterId: Int): Flow<List<SpellSlotSummary>>

    // Add this data class for spell slot summary
    data class SpellSlotSummary(
        val spellLevel: Int,
        val totalSlots: Int,
        val usedSlots: Int
    )

    // Add this function for resetting spell slots (short rest)
    @Query("UPDATE character_spell_slots SET usedSlots = 0 WHERE characterId = :characterId")
    suspend fun resetSpellSlots(characterId: Int)

    // Add this function for getting spellcasting ability based on class
    @Query("""
    SELECT 
        CASE 
            WHEN className IN ('Bard', 'Sorcerer', 'Paladin', 'Warlock') THEN 'CHA'
            WHEN className IN ('Cleric', 'Druid', 'Ranger') THEN 'WIS'
            WHEN className IN ('Wizard', 'Eldritch Knight', 'Arcane Trickster') THEN 'INT'
            ELSE 'NONE'
        END as ability
    FROM classes
    WHERE classId = :classId
""")
    fun getSpellcastingAbility(classId: Int): Flow<String>

    // Add this function for getting available cantrips based on class and level
    @Query("""
    SELECT s.* FROM spells s
    JOIN class_spells cs ON s.spellId = cs.spellId
    WHERE cs.classId = :classId AND s.spellLevel = 0
    ORDER BY s.spellName ASC
""")
    fun getCantripsForClass(classId: Int): Flow<List<Spell>>

    // Add this function for getting ritual spells
    @Query("""
    SELECT s.* FROM spells s
    JOIN character_spells cs ON s.spellId = cs.spellId
    WHERE cs.characterId = :characterId AND s.ritual = 1
    ORDER BY s.spellLevel ASC, s.spellName ASC
""")
    fun getRitualSpells(characterId: Int): Flow<List<Spell>>

    // Add this function for getting concentration spells
    @Query("""
    SELECT s.* FROM spells s
    JOIN character_spells cs ON s.spellId = cs.spellId
    WHERE cs.characterId = :characterId AND s.concentration = 1
    ORDER BY s.spellLevel ASC, s.spellName ASC
""")
    fun getConcentrationSpells(characterId: Int): Flow<List<Spell>>

}
data class WeaponAndItem(
    @Embedded val weapon: Weapon,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "itemId"
    )
    val item: Item
)

data class CharacterSkillWithDetails(
    @Embedded val characterSkill: CharacterSkill,
    @Relation(
        parentColumn = "skillId",
        entityColumn = "skillId"
    )
    val skill: Skill
)

data class CharacterWithAC(
    @Embedded val character: Character,
    val armorBonus: Int = 0,
    val shieldBonus: Int = 0
)