package com.example.rpg_character_sheet

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface CharacterDao {
    @Insert
    fun insert(character: Character)

    @Update
    fun update(character: Character)

    @Delete
    fun delete(character: Character)

    @Query("SELECT * FROM characters ORDER BY name ASC")
    fun getAllCharacters(): LiveData<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): LiveData<Character>
}