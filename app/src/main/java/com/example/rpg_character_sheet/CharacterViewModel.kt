package com.example.rpg_character_sheet

// Tables
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import table_entities.*


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
	private val _selectedCharacterId = MutableStateFlow(1)

	// Allows only reading the data
	val characters: StateFlow<List<Character>> get() = _characters
	val selectedCharacterId: StateFlow<Int> get() = _selectedCharacterId


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

	fun selectCharacter(characterId: Int) {
		_selectedCharacterId.value = characterId
	}

	fun getSelectedCharacter() : Flow<Character> {
		return characterDao.getCharacterById(_selectedCharacterId.value)
	}

	fun getCharacterById(characterId: Int): Flow<Character> {
		return characterDao.getCharacterById(characterId)
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

	fun updateCharacterName(character: Character, newName: String) {
		viewModelScope.launch {
			characterDao.updateCharacterName(character.characterId, newName)
		}
	}

	fun updateCharacterLevel(character: Character, newLevel: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterLevel(character.characterId, newLevel)
		}
	}

	fun deleteCharacter(character: Character) {
		viewModelScope.launch {
			characterDao.delete(character)
		}
	}

	// Classes
	fun getClassById(classId: Int): Flow<CharacterClass> {
		return characterDao.getClassById(classId)
	}

	fun getClassByIdAsPair(classId: Int): Flow<Pair<Int, String>> {
		return characterDao.getClassById(classId).map { it.classId to it.className }
	}

	fun getAllClassesAsPair(): Flow<List<Pair<Int, String>>> {
		return characterDao.getAllClasses().map { characterClass ->
			characterClass.map { it.classId to it.className }
		}
	}

	fun updateCharacterClass(character: Character, newClassId: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterClass(character.characterId, newClassId)
		}
	}

	// Subclasses
	fun getSubclassByIdAsPair(subclassId: Int): Flow<Pair<Int, String>> {
		return characterDao.getSubclassById(subclassId).map { it.subclassId to it.subclassName }
	}

	fun getAllSubclasses(): Flow<List<Subclass>> {
		return characterDao.getAllSubclasses()
	}

	fun getSubclassesOfClassAsPairs(classId: Int): Flow<List<Pair<Int, String>>> {
		return characterDao.getSubclassesOfClass(classId).map { subclass ->
			subclass.map { it.subclassId to it.subclassName }
		}
	}

	fun updateCharacterSubclass(character: Character, newSubclassId: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterSubclass(character.characterId, newSubclassId)
		}
	}

	// Races
	fun getRaceById(raceId: Int): Flow<Race> {
		return characterDao.getRaceById(raceId)
	}

	fun getAllItems(): Flow<List<Item>> {
		return characterDao.getAllItems()
	}

	fun getCharacterInventory(characterId: Int): Flow<List<CharacterInventory>> {
		return characterDao.getCharacterInventory(characterId)
	}

	fun addItemToInventory(characterId: Int, itemId: Int) {
		viewModelScope.launch {
			// Check if item already exists in inventory
			val existing = characterDao.getCharacterInventory(characterId).firstOrNull()?.find { it.itemId == itemId }

			if (existing != null) {
				// If exists, increment quantity
				val updated = existing.copy(quantity = existing.quantity + 1)
				characterDao.updateCharacterInventory(updated)
			} else {
				// If not exists, add new entry
				val newItem = CharacterInventory(
					characterId = characterId,
					itemId = itemId,
					quantity = 1,
					equipped = false
				)
				characterDao.insertCharacterInventory(newItem)
			}
		}
	}

	fun removeItemFromInventory(characterInventory: CharacterInventory) {
		viewModelScope.launch {
			if (characterInventory.quantity > 1) {
				// If more than one, decrement quantity
				val updated = characterInventory.copy(quantity = characterInventory.quantity - 1)
				characterDao.updateCharacterInventory(updated)
			} else {
				// If only one, remove entirely
				characterDao.deleteCharacterInventory(characterInventory)
			}
		}
	}

	fun getCharacterWeapons(characterId: Int): Flow<List<WeaponAndItem>> {
		return characterDao.getCharacterWeapons(characterId)
	}

	fun getCharacterSpells(characterId: Int): Flow<List<Spell>> {
		return characterDao.getCharacterSpells(characterId)
	}

	fun getCharacterFeatures(characterId: Int): Flow<List<Feature>> {
		return characterDao.getCharacterFeatures(characterId)
	}

	fun getCharacterSpellSlots(characterId: Int): Flow<List<CharacterSpellSlot>> {
		return characterDao.getCharacterSpellSlots(characterId)
	}

	fun getRaceByIdAsPair(raceId: Int): Flow<Pair<Int, String>> {
		return characterDao.getRaceById(raceId).map { it.raceId to it.raceName }
	}

	fun getAllRacesAsPairs(): Flow<List<Pair<Int, String>>> {
		return characterDao.getAllRaces().map { race ->
			race.map { it.raceId to it.raceName }
		}
	}

	fun updateCharacterRace(character: Character, newRaceId: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterRace(character.characterId, newRaceId)
		}
	}

	// Subraces
	fun getSubraceById(subraceId: Int): Flow<Subrace> {
		return characterDao.getSubraceById(subraceId)
	}

	fun getAllSubraces(): Flow<List<Subrace>> {
		return characterDao.getAllSubraces()
	}

	fun getSubraceByIdAsPair(subraceId: Int): Flow<Pair<Int, String>> {
		return characterDao.getSubraceById(subraceId).map { it.subraceId to it.subraceName }
	}

	fun getSubracesOfRaceAsPairs(raceId: Int): Flow<List<Pair<Int, String>>> {
		return characterDao.getSubracesOfRace(raceId).map { subrace ->
			subrace.map { it.subraceId to it.subraceName }
		}
	}

	fun updateCharacterSubrace(character: Character, newSubraceId: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterSubrace(character.characterId, newSubraceId)
		}
	}

	// Stats
	fun updateCharacterStats(characterId: Int, str: Int, dex: Int, con: Int, int: Int, wis: Int, cha: Int) {
		viewModelScope.launch {
			updateCharacterStats(characterId, str, dex, con, int, wis, cha)
		}
	}
}