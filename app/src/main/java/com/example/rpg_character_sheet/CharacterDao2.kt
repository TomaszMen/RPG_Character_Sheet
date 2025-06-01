package com.example.rpg_character_sheet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CharacterDao2 {
	@Insert
	suspend fun insert(character: Character)

	@Update
	suspend fun update(character: Character)

	@Delete
	suspend fun delete(character: Character)

	@Query("SELECT * FROM characters ORDER BY name ASC")
	fun getAllCharacters(): Flow<List<Character>>

	@Query("SELECT * FROM characters WHERE id = :id")
	fun getCharacterById(id: Int): Flow<Character?>
}