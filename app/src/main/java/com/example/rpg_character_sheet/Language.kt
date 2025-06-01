package com.example.rpg_character_sheet

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class Language(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val characterId: Int,
    val languageName: String
)