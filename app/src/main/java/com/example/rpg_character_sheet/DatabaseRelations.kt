package com.example.rpg_character_sheet

import androidx.room.Embedded
import androidx.room.Relation

data class DatabaseRelations(
    @Embedded
    val character: Character,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    )
    val languages: List<Language>
)