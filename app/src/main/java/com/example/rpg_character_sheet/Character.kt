package com.example.rpg_character_sheet

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class Character(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var characterClass: String? = null,
    var race: String? = null,
    var background: String? = null,
    var alignment: String? = null,
    var armorType: String? = null,
    var level: Int = 1,
    var maxHp: Int = 0,
    var currentHp: Int = 0,
    var armorClass: Int = 10,
    var initiative: Int = 0,
    var walkingSpeed: Int = 30,
    var stats: IntArray = IntArray(6),
    var modifiers: IntArray = IntArray(6),
    var proficiencyBonus: Int = 2,
    var personalityTraits: String? = null,
    var ideals: String? = null,
    var bonds: String? = null,
    var flaws: String? = null,
    @Ignore
    val skills: MutableMap<Skill, Boolean> = mutableMapOf()

) {
    enum class Skill(val associatedStat: Int) {
        ACROBATICS(1),    // DEX
        ANIMAL_HANDLING(4),// WIS
        ARCANA(3),        // INT
        ATHLETICS(0),     // STR
        DECEPTION(5),     // CHA
        HISTORY(3),       // INT
        INSIGHT(4),       // WIS
        INTIMIDATION(5),  // CHA
        INVESTIGATION(3), // INT
        MEDICINE(4),      // WIS
        NATURE(3),        // INT
        PERCEPTION(4),    // WIS
        PERFORMANCE(5),   // CHA
        PERSUASION(5),    // CHA
        RELIGION(3),      // INT
        SLEIGHT_OF_HAND(1),// DEX
        STEALTH(1),       // DEX
        SURVIVAL(4);      // WIS
    }

    companion object {
        const val STR = 0
        const val DEX = 1
        const val CON = 2
        const val INT = 3
        const val WIS = 4
        const val CHA = 5

        private const val PROFICIENCY_BONUS_LEVEL_THRESHOLD = 4
    }

    init {
        initSkills()
        updateAll()
    }

    private fun initSkills() {
        Skill.values().forEach { skills[it] = false }
    }

    fun getDisplayName() = name ?: "N/A"
    fun getCharacterClassDisplay() = characterClass ?: "N/A"
    fun getHP() = maxHp
    fun getAC() = armorClass
    fun getWS() = walkingSpeed

    fun getStr() = stats[STR]
    fun getDex() = stats[DEX]
    fun getCon() = stats[CON]
    fun getInt() = stats[INT]
    fun getWis() = stats[WIS]
    fun getCha() = stats[CHA]

    fun updateAll() {
        updateModifiers()
        updateProficiencyBonus()
        updateArmorStats()
        updateMaxHp()
        updateInitiative()
    }

    private fun updateModifiers() {
        modifiers = stats.map { (it - 10) / 2 }.toIntArray()
    }

    private fun updateProficiencyBonus() {
        proficiencyBonus = 2 + (level - 1) / 4
    }

    fun getSkillModifier(skill: Skill): Int {
        val base = modifiers[skill.associatedStat]
        return if (skills[skill] == true) base + proficiencyBonus else base
    }

    fun getPassivePerception(): Int {
        return 10 + getSkillModifier(Skill.PERCEPTION)
    }

    private fun updateInitiative() {
        initiative = modifiers[DEX]
    }

    fun updateArmorStats() {
        armorClass = when (armorType) {
            "No armor" -> 10 + modifiers[DEX]
            "Padded" -> 11 + modifiers[DEX]
            "Leather" -> 11 + modifiers[DEX]
            "Studded leather" -> 12 + modifiers[DEX]
            "Hide" -> 12 + minOf(modifiers[DEX], 2)
            "Chain shirt" -> 13 + minOf(modifiers[DEX], 2)
            "Scale mail" -> 14 + minOf(modifiers[DEX], 2)
            "Breastplate" -> 14 + minOf(modifiers[DEX], 2)
            "Half plate" -> 15 + minOf(modifiers[DEX], 2)
            "Ring mail" -> 14
            "Chain mail" -> 16
            "Splint" -> 17
            "Plate" -> 18
            else -> 10 + modifiers[DEX]
        }
    }

    private fun updateWalkingSpeed() {
        walkingSpeed = when (race) {
            "Dwarf", "Halfling", "Gnome" -> 25
            "Dragonborn", "Elf", "Human", "Half-Orc", "Tiefling" -> 30
            else -> 30
        }
    }

    fun updateMaxHp() {
        val conModifier = modifiers[CON]
        val hitDie = when (characterClass) {
            "Barbarian" -> 12
            "Fighter", "Paladin", "Ranger" -> 10
            "Bard", "Cleric", "Druid", "Monk", "Rogue", "Warlock" -> 8
            "Sorcerer", "Wizard" -> 6
            else -> 8
        }
        maxHp = hitDie + conModifier + (level - 1) * (hitDie / 2 + 1 + conModifier)
        if (currentHp > maxHp) currentHp = maxHp
    }

    // Equality and hashcode updated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (characterClass != other.characterClass) return false
        if (race != other.race) return false
        if (background != other.background) return false
        if (alignment != other.alignment) return false
        if (armorType != other.armorType) return false
        if (level != other.level) return false
        if (maxHp != other.maxHp) return false
        if (currentHp != other.currentHp) return false
        if (armorClass != other.armorClass) return false
        if (initiative != other.initiative) return false
        if (walkingSpeed != other.walkingSpeed) return false
        if (!stats.contentEquals(other.stats)) return false
        if (!modifiers.contentEquals(other.modifiers)) return false
        if (skills != other.skills) return false
        if (proficiencyBonus != other.proficiencyBonus) return false
        if (personalityTraits != other.personalityTraits) return false
        if (ideals != other.ideals) return false
        if (bonds != other.bonds) return false
        return flaws == other.flaws
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (characterClass?.hashCode() ?: 0)
        result = 31 * result + (race?.hashCode() ?: 0)
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (alignment?.hashCode() ?: 0)
        result = 31 * result + (armorType?.hashCode() ?: 0)
        result = 31 * result + level
        result = 31 * result + maxHp
        result = 31 * result + currentHp
        result = 31 * result + armorClass
        result = 31 * result + initiative
        result = 31 * result + walkingSpeed
        result = 31 * result + stats.contentHashCode()
        result = 31 * result + modifiers.contentHashCode()
        result = 31 * result + skills.hashCode()
        result = 31 * result + proficiencyBonus
        result = 31 * result + (personalityTraits?.hashCode() ?: 0)
        result = 31 * result + (ideals?.hashCode() ?: 0)
        result = 31 * result + (bonds?.hashCode() ?: 0)
        result = 31 * result + (flaws?.hashCode() ?: 0)
        return result
    }
}