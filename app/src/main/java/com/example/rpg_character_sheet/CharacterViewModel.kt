package com.example.rpg_character_sheet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val characterDao = CharacterDatabase.getDatabase(application).characterDao()
    val allCharacters: LiveData<List<Character>> = characterDao.getAllCharacters()

    fun getCharacterById(id: Int): LiveData<Character> = characterDao.getCharacterById(id)

    fun insertCharacter(character: Character) {
        scope.launch { characterDao.insert(character) }
    }

    fun updateCharacter(character: Character) {
        scope.launch { characterDao.update(character) }
    }

    fun delete(character: Character) {
        scope.launch { characterDao.delete(character) }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getAllCharacters(): Any {
        return allCharacters;
    }
}