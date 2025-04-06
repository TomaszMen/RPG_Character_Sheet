package com.example.rpg_character_sheet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val characterDao: CharacterDao
    private val allCharacters: LiveData<List<Character>>

    init {
        val database = CharacterDatabase.getDatabase(application)
        characterDao = database.characterDao()
        allCharacters = characterDao.getAllCharacters()
    }

    fun getAllCharacters(): LiveData<List<Character>> = allCharacters

    fun getCharacterById(id: Int): LiveData<Character> = characterDao.getCharacterById(id)

    fun insertCharacter(character: Character) {
        executorService.execute { characterDao.insert(character) }
    }

    fun updateCharacter(character: Character) {
        executorService.execute { characterDao.update(character) }
    }

    fun delete(character: Character) {
