package com.example.rpg_character_sheet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Tables
import table_entities.Character


// Creates the CharacterViewModel instance inside UI elements so it doesn't need to passed as an argument
class CharacterViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return CharacterViewModel(application) as T
	}
}

class CharacterViewModel(application: Application) : ViewModel() {
	// Data access objects
	private val characterDao: CharacterDao = CharacterDatabase.getDatabase(application).characterDao()

	// Allows reading and writing data
	private val _characters = MutableStateFlow<List<Character>>(emptyList())

	// Allows only reading the data
	val characters: StateFlow<List<Character>> get() = _characters


	// Called upon creation of each instance
	init {
		fetchData()
	}

	private fun fetchData() {
		viewModelScope.launch {
			// Fetch characters
			characterDao.getAllCharacters().collect { characterList ->
				_characters.value = characterList
			}
		}
	}

	fun getCharacterById(id: Int): Flow<Character?> {
		return characterDao.getCharacterById(id)
	}

	fun insertCharacter(character: Character) {
		viewModelScope.launch {
			characterDao.insert(character)
		}
	}

	fun updateCharacter(character: Character) {
		viewModelScope.launch {
			characterDao.update(character)
		}
	}

	fun deleteCharacter(character: Character) {
		viewModelScope.launch {
			characterDao.delete(character)
		}
	}
}