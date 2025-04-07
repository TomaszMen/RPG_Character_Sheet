package com.example.rpg_character_sheet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var characterClass: String? = null,
    var race: String? = null,
    var armorType: String? = null,
    var level: Int = 1,
    var maxHp: Int = 0,
    var armorClass: Int = 10,
    var walkingSpeed: Int = 30,
    var stats: IntArray = IntArray(6),
    var modifiers: IntArray = IntArray(6)
) {
    companion object {
        const val globalStrId = 0
        const val globalDexId = 1
        const val globalConId = 2
        const val globalIntId = 3
        const val globalWisId = 4
        const val globalChaId = 5
    }

    fun updateModifiers() {
        // Implementation to be added
    }

    fun updateArmorStats() {
        when (armorType) {
            "No armor" -> {
                armorClass = 13
                walkingSpeed = 35
            }
            "Light armor" -> {
                armorClass = 15
                walkingSpeed = 30
            }
            "Heavy armor" -> {
                armorClass = 18
                walkingSpeed = 20
            }
            else -> {
                armorClass = 10
                walkingSpeed = 30
            }
        }
    }

    fun getDisplayName() = name ?: "N/A"
    fun getCharacterClassDisplay() = characterClass ?: "N/A"
    fun getHP() = maxHp
    fun getAC() = armorClass
    fun getWS() = walkingSpeed

    fun getStr() = stats[globalStrId]
    fun getDex() = stats[globalDexId]
    fun getCon() = stats[globalConId]
    fun getInt() = stats[globalIntId]
    fun getWis() = stats[globalWisId]
    fun getCha() = stats[globalChaId]

    fun updateMaxHp() {
        val hpPerLevel = when (characterClass) {
            "Barbarian" -> 16
            "Bard", "Cleric", "Ranger", "Rogue" -> 8
            "Druid", "Monk", "Paladin" -> 10
            "Fighter" -> 12
            "Sorcerer", "Warlock", "Wizard" -> 6
            else -> 8
        }
        maxHp = hpPerLevel * level
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (characterClass != other.characterClass) return false
        if (race != other.race) return false
        if (armorType != other.armorType) return false
        if (level != other.level) return false
        if (maxHp != other.maxHp) return false
        if (armorClass != other.armorClass) return false
        if (walkingSpeed != other.walkingSpeed) return false
        if (!stats.contentEquals(other.stats)) return false
        if (!modifiers.contentEquals(other.modifiers)) return false
        if (!stats.contentEquals(other.stats)) return false
        if (!modifiers.contentEquals(other.modifiers)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (characterClass?.hashCode() ?: 0)
        result = 31 * result + (race?.hashCode() ?: 0)
        result = 31 * result + (armorType?.hashCode() ?: 0)
        result = 31 * result + level
        result = 31 * result + maxHp
        result = 31 * result + armorClass
        result = 31 * result + walkingSpeed
        result = 31 * result + stats.contentHashCode()
        result = 31 * result + modifiers.contentHashCode()
        result = 31 * result + stats.contentHashCode()
        result = 31 * result + modifiers.contentHashCode()
        return result
    }
}