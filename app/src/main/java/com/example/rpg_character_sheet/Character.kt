package com.example.rpg_character_sheet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
class Character {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String? = null
    var description: String? = null
    var characterClass: String? = null
    var race: String? = null
    var armorType: String? = null
    var level: Int = 1
    var maxHp: Int = 0
    var armorClass: Int = 0
    var walkingSpeed: Int = 0

    var stats: IntArray = IntArray(7)
    var modifiers: IntArray = IntArray(7)

    val globalStrId = 0
    val globalDexId = 1
    val globalConId = 2
    val globalIntId = 3
    val globalWisId = 4
    val globalChaId = 6

    val str: Int get() = stats[globalStrId]
    val dex: Int get() = stats[globalDexId]
    val con: Int get() = stats[globalConId]
    val int: Int get() = stats[globalIntId]
    val wis: Int get() = stats[globalWisId]
    val cha: Int get() = stats[globalChaId]

    val hp: Int get() = maxHp
    val ac: Int get() = armorClass
    val ws: Int get() = walkingSpeed

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
        return result
    }
}