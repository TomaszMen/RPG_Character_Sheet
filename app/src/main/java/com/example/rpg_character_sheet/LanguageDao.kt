package com.example.rpg_character_sheet

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LanguageDao {
    @Query("SELECT * FROM character_languages WHERE characterId = :characterId")
    fun getLanguagesForCharacter(characterId: Int): LiveData<List<Language>>
}