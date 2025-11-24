package com.example.rpg_character_sheet

// Tables
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import table_entities.*


// Creates the CharacterViewModel instance inside UI elements so it doesn't need to passed as an argument
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

    fun getAllBackgroundsAsPairs(): Flow<List<Pair<Int, String>>> {
        return characterDao.getAllBackgrounds().map { characterBackground ->
            characterBackground.map { it.backgroundId to it.backgroundName }
        }
    }

    fun getAllAlignmentsAsPairs(): Flow<List<Pair<Int, String>>> {
        return characterDao.getAllAlignments().map { characterAlignment ->
            characterAlignment.map { it.alignmentId to it.alignmentName }
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

    fun getSubracesForRace(raceId: Int): Flow<List<Pair<Int, String>>> {
        return characterDao.getSubracesOfRace(raceId).map { subraces ->
            subraces.map { it.subraceId to it.subraceName }
        }
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
			characterDao.updateCharacterStats(characterId, str, dex, con, int, wis, cha)
		}
	}

    // Features
    fun getRaceFeatures(raceId: Int): Flow<List<Feature>> {
        return characterDao.getRaceFeatures(raceId)
    }

    fun getSubraceFeatures(subraceId: Int): Flow<List<Feature>> {
        return characterDao.getSubraceFeatures(subraceId)
    }

    fun getClassFeatures(classId: Int): Flow<List<Feature>> {
        return characterDao.getClassFeatures(classId)
    }

    // Skills
    fun getAllSkills(): Flow<List<Skill>> {
        return characterDao.getAllSkills()
    }

    // Character Creation
    fun createNewCharacter(characterData: CharacterCreationData) {
        viewModelScope.launch {
            // Create base character
            val character = Character(
                characterName = characterData.name,
                raceId = characterData.raceId,
                subraceId = characterData.subraceId,
                classId = characterData.classId,
                subclassId = 0,
                backgroundId = characterData.backgroundId,
                alignmentId = characterData.alignmentId,
                level = 1,
                strength = characterData.strength,
                dexterity = characterData.dexterity,
                constitution = characterData.constitution,
                intelligence = characterData.intelligence,
                wisdom = characterData.wisdom,
                charisma = characterData.charisma,
                hitPointMax = characterData.maxHP,
                currentHitPoints = characterData.maxHP,
                armorClass = 10 + characterData.dexterity.modifier(),
                speed = characterData.speed
            )

            val characterId = characterDao.insertAndGetId(character)

            // Add skills
            characterData.skillProficiencies.forEach { skillId ->
                characterDao.insertCharacterSkill(
                    CharacterSkill(
                        characterId = characterId,
                        skillId = skillId,
                        proficiency = 1
                    )
                )
            }

            // Add languages
            characterData.languages.forEach { languageId ->
                characterDao.insertCharacterLanguage(
                    CharacterLanguage(
                        characterId = characterId,
                        languageId = languageId
                    )
                )
            }

            // Add equipment
            characterData.equipment.forEach { itemId ->
                characterDao.insertCharacterInventory(
                    CharacterInventory(
                        characterId = characterId,
                        itemId = itemId,
                        quantity = 1,
                        equipped = false
                    )
                )
            }

            // Add spells if any
            characterData.spells.forEach { spellId ->
                characterDao.insertCharacterSpell(
                    CharacterSpell(
                        characterId = characterId,
                        spellId = spellId,
                        prepared = true
                    )
                )
            }
        }
    }

    data class CharacterCreationData(
        val name: String = "",
        val raceId: Int = 0,
        val subraceId: Int = 0,
        val classId: Int = 0,
        val backgroundId: Int = 0,
        val alignmentId: Int = 0,
        val strength: Int = 10,
        val dexterity: Int = 10,
        val constitution: Int = 10,
        val intelligence: Int = 10,
        val wisdom: Int = 10,
        val charisma: Int = 10,
        val skillProficiencies: List<Int> = emptyList(),
        val languages: List<Int> = emptyList(),
        val equipment: List<Int> = emptyList(),
        val spells: List<Int> = emptyList(),
        val maxHP: Int = 0,
        val speed: Int = 30
    )

    // Extension function to calculate modifier
    fun Int.modifier(): Int = (this - 10) / 2
}