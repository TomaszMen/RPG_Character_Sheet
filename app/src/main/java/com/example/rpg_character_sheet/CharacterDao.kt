package com.example.rpg_character_sheet

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CharacterDao {
    @Insert
    suspend fun insert(character: Character)

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * FROM characters ORDER BY name ASC")
    fun getAllCharacters(): LiveData<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): LiveData<Character>

    @Transaction
    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterWithLanguages(characterId: Int): DatabaseRelations

    @Insert
    suspend fun insertLanguage(language: LanguageDatabase)

    @Delete
    suspend fun deleteLanguage(language: LanguageDatabase)

    @Query("DELETE FROM character_languages WHERE characterId = :characterId")
    suspend fun deleteAllLanguagesForCharacter(characterId: Int)

}