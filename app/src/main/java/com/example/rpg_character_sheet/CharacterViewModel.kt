package com.example.rpg_character_sheet

// Tables
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
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

    data class StartingEquipmentOption(
        val name: String,
        val items: List<Int> // itemIds
    )

    data class RaceBonus(
        val strength: Int = 0,
        val dexterity: Int = 0,
        val constitution: Int = 0,
        val intelligence: Int = 0,
        val wisdom: Int = 0,
        val charisma: Int = 0,
        val extraHP: Int = 0
    )

    // Extension function for modifier calculation
    private fun Int.modifier(): Int = (this - 10) / 2

    private fun calculateStartingHP(hitDie: Int, constitution: Int): Int {
        val conModifier = constitution.modifier()
        return hitDie + conModifier
    }

    fun calculateRaceBonuses(raceId: Int, subraceId: Int): RaceBonus {
        return when (raceId) {
            // Dwarf
            1 -> when (subraceId) {
                // Hill Dwarf
                1 -> RaceBonus(wisdom = 1, extraHP = 1)
                // Mountain Dwarf
                2 -> RaceBonus(strength = 2)
                else -> RaceBonus(constitution = 2)
            }
            // Elf
            2 -> when (subraceId) {
                // High Elf
                3 -> RaceBonus(dexterity = 2, intelligence = 1)
                // Wood Elf
                4 -> RaceBonus(dexterity = 2, wisdom = 1)
                // Drow
                5 -> RaceBonus(dexterity = 2, charisma = 1)
                else -> RaceBonus(dexterity = 2)
            }
            // Halfling
            3 -> RaceBonus(dexterity = 2)
            // Human
            4 -> RaceBonus(strength = 1, dexterity = 1, constitution = 1,
                intelligence = 1, wisdom = 1, charisma = 1)
            // Dragonborn
            5 -> RaceBonus(strength = 2, charisma = 1)
            // Gnome
            6 -> RaceBonus(intelligence = 2)
            // Half-Elf
            7 -> RaceBonus(charisma = 2)
            // Half-Orc
            8 -> RaceBonus(strength = 2, constitution = 1)
            // Tiefling
            9 -> RaceBonus(intelligence = 1, charisma = 2)
            else -> RaceBonus()
        }
    }

    fun getMaxSkillProficiencies(classId: Int): Int {
        return when (classId) {
            2 -> 3 // Bard
            9 -> 4 // Rogue
            else -> 2 // Most classes
        }
    }

    fun getStartingEquipmentOptions(classId: Int): List<StartingEquipmentOption> {
        return when (classId) {
            // Barbarian
            1 -> listOf(
                StartingEquipmentOption("Standard Equipment", listOf(16, 17, 4)),
                StartingEquipmentOption("100 GP", listOf())
            )
            // Fighter
            5 -> listOf(
                StartingEquipmentOption("Standard Equipment - Chain Mail", listOf(32, 10, 13)),
                StartingEquipmentOption("Standard Equipment - Leather", listOf(48, 2, 28)),
                StartingEquipmentOption("100 GP", listOf())
            )
            // Rogue
            9 -> listOf(
                StartingEquipmentOption("Standard Equipment", listOf(41, 15, 2, 26)),
                StartingEquipmentOption("100 GP", listOf())
            )
            // Wizard
            12 -> listOf(
                StartingEquipmentOption("Standard Equipment", listOf(21, 15)),
                StartingEquipmentOption("100 GP", listOf())
            )
            // Default for other classes
            else -> listOf(
                StartingEquipmentOption("Standard Equipment", listOf()),
                StartingEquipmentOption("100 GP", listOf())
            )
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

    // Background queries
    fun getBackgroundById(backgroundId: Int): Flow<Background> {
        return characterDao.getBackgroundById(backgroundId)
    }

    // Subrace queries
    fun getSubraceById(subraceId: Int): Flow<Subrace> {
        return characterDao.getSubraceById(subraceId)
    }

    // Features queries
    fun getRaceFeatures(raceId: Int): Flow<List<Feature>> {
        return characterDao.getRaceFeatures(raceId)
    }

    fun getSubraceFeatures(subraceId: Int): Flow<List<Feature>> {
        return characterDao.getSubraceFeatures(subraceId)
    }

    fun getClassFeatures(classId: Int): Flow<List<Feature>> {
        return characterDao.getClassFeatures(classId)
    }

    // Skills query
    fun getAllSkills(): Flow<List<Skill>> {
        return characterDao.getAllSkills()
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

    fun getMaxSkillProficiencies(classId: Int, backgroundId: Int): Int {
        // Base class skills
        val classSkills = when (classId) {
            2 -> 3 // Bard
            9 -> 4 // Rogue
            else -> 2 // Most classes
        }

        // Background typically gives 2 skills
        val backgroundSkills = 2

        return classSkills
    }

    // Get alignment by ID
    fun getAlignmentById(alignmentId: Int): Flow<Alignment> {
        return characterDao.getAlignmentById(alignmentId)
    }

    // Get available feats for level and class
    fun getAvailableFeats(level: Int, classId: Int): Flow<List<Feature>> {
        return characterDao.getAvailableFeatsByLevel(level)
    }

    // Get class features available at current level
    fun getClassFeaturesByLevel(classId: Int, level: Int): Flow<List<Feature>> {
        return characterDao.getClassFeaturesByLevel(classId, level)
    }

    // Get subclass by ID
    fun getSubclassById(subclassId: Int): Flow<Subclass> {
        return characterDao.getSubclassById(subclassId)
    }

    // Get subclasses for a specific class
    fun getSubclassesForClass(classId: Int): Flow<List<Subclass>> {
        return characterDao.getSubclassesForClass(classId)
    }

    // Add a feature to character
    fun addFeatureToCharacter(characterId: Int, featureId: Int) {
        viewModelScope.launch {
            // Check if the character already has this feature
            val hasFeature = characterDao.hasCharacterFeature(characterId, featureId)
            if (hasFeature == 0) {
                val characterFeature = CharacterFeature(
                    characterId = characterId,
                    featureId = featureId
                )
                characterDao.insertCharacterFeature(characterFeature)
            }
        }
    }

    // Remove a feature from character
    fun removeFeatureFromCharacter(characterId: Int, featureId: Int) {
        viewModelScope.launch {
            characterDao.deleteCharacterFeature(characterId, featureId)
        }
    }

    // Check if character can choose subclass (level >= 3 and no subclass selected)
    fun canChooseSubclass(character: Character): Boolean {
        return character.level >= 3 && character.subclassId == 0
    }

    // Check if character can choose feat at current level
    fun canChooseFeat(level: Int): Boolean {
        val featLevels = listOf(4, 8, 12, 16, 19)
        return level in featLevels
    }

    // Calculate available feat choices based on level
    fun getAvailableFeatChoices(level: Int): List<Int> {
        val featLevels = listOf(4, 8, 12, 16, 19)
        return featLevels.filter { it <= level }
    }

    // Calculate ASI levels for a character
    fun getAsiLevels(classId: Int): List<Int> {
        return when (classId) {
            // Fighter gets extra ASIs at 6 and 14
            5 -> listOf(4, 6, 8, 12, 14, 16, 19)
            // Rogue gets extra ASI at 10
            9 -> listOf(4, 8, 10, 12, 16, 19)
            // Other classes follow standard progression
            else -> listOf(4, 8, 12, 16, 19)
        }
    }

    // Check if character has a specific feature
    fun hasCharacterFeature(characterId: Int, featureId: Int): Flow<Boolean> {
        return characterDao.getCharacterFeatures(characterId)
            .map { features -> features.any { it.featureId == featureId } }
    }

    // Get prerequisites for a feature
    fun getFeaturePrerequisites(featureId: Int): Flow<List<String>> {
        // This would need additional database structure for prerequisites
        // For now, returning empty list
        return flowOf(emptyList())
    }

    // Get character's subclass information
    fun getCharacterSubclass(characterId: Int): Flow<Subclass?> {
        return characterDao.getCharacterById(characterId).flatMapConcat { character ->
            if (character.subclassId != 0) {
                characterDao.getSubclassById(character.subclassId)
            } else {
                flowOf(null)
            }
        }
    }

    // Get equipped items
    fun getEquippedItems(characterId: Int): Flow<List<CharacterInventory>> {
        return characterDao.getEquippedItems(characterId)
    }

    // Get all weapons
    fun getAllWeapons(): Flow<List<Item>> {
        return characterDao.getAllWeapons()
    }

    // Get all armor
    fun getAllArmor(): Flow<List<Item>> {
        return characterDao.getAllArmor()
    }

    // Get other items
    fun getOtherItems(): Flow<List<Item>> {
        return characterDao.getOtherItems()
    }

    // Equip an item with validation
    fun equipItem(characterId: Int, inventoryItem: CharacterInventory) {
        viewModelScope.launch {
            val item = characterDao.getAllItems().firstOrNull()?.find { it.itemId == inventoryItem.itemId }

            if (item != null) {
                when (item.itemType) {
                    Item.ItemType.Armor -> {
                        // Check if it's a shield
                        val armorDetails = try {
                            // This would require a more complex query to check armor type
                            // For now, we'll assume all armor is body armor
                            // You might want to add armorType to Item entity
                            null
                        } catch (e: Exception) {
                            null
                        }

                        // For now, just equip armor (we'll handle shield logic separately if needed)
                        // Unequip other armor first
                        val equippedArmor = characterDao.getEquippedArmor(characterId).firstOrNull()
                        equippedArmor?.forEach { equipped ->
                            characterDao.updateEquippedStatus(equipped.inventoryId, false)
                        }

                        // Equip this item
                        characterDao.updateEquippedStatus(inventoryItem.inventoryId, true)
                    }
                    Item.ItemType.Weapon -> {
                        val equippedWeaponsCount = characterDao.countEquippedWeapons(characterId)

                        if (equippedWeaponsCount < 5) {
                            characterDao.updateEquippedStatus(inventoryItem.inventoryId, true)
                        } else {
                            // Show error - limit reached (you might want to add error handling)
                            println("Weapon limit reached (max 5)")
                        }
                    }
                    else -> {
                        // For other item types, just toggle equipped status
                        val newEquippedStatus = !inventoryItem.equipped
                        characterDao.updateEquippedStatus(inventoryItem.inventoryId, newEquippedStatus)
                    }
                }
            }
        }
    }

    // Unequip an item
    fun unequipItem(characterId: Int, inventoryItem: CharacterInventory) {
        viewModelScope.launch {
            characterDao.updateEquippedStatus(inventoryItem.inventoryId, false)
        }
    }

    suspend fun getAvailableClassSkills(classId: Int): List<Skill> {
        return when (classId) {
            // Barbarian
            1 -> getSkillsByAbilities(listOf("STR", "DEX", "CON", "WIS"))
            // Bard - can choose any 3
            2 -> characterDao.getAllSkills().first()
            // Cleric
            3 -> getSkillsByAbilities(listOf("WIS", "CHA"))
            // Druid
            4 -> getSkillsByAbilities(listOf("INT", "WIS"))
            // Fighter
            5 -> getSkillsByAbilities(listOf("STR", "DEX", "CON"))
            // Monk
            6 -> getSkillsByAbilities(listOf("STR", "DEX", "WIS"))
            // Paladin
            7 -> getSkillsByAbilities(listOf("WIS", "CHA"))
            // Ranger
            8 -> getSkillsByAbilities(listOf("STR", "DEX", "WIS"))
            // Rogue
            9 -> getSkillsByAbilities(listOf("DEX", "INT", "CHA"))
            // Sorcerer
            10 -> getSkillsByAbilities(listOf("CHA"))
            // Warlock
            11 -> getSkillsByAbilities(listOf("WIS", "CHA"))
            // Wizard
            12 -> getSkillsByAbilities(listOf("INT"))
            else -> characterDao.getAllSkills().first()
        }
    }

    private suspend fun getSkillsByAbilities(abilities: List<String>): List<Skill> {
        val allSkills = characterDao.getAllSkills().first()
        return allSkills.filter { it.abilityScore.toString() in abilities }
    }

	// Stats
	fun updateCharacterStats(characterId: Int, str: Int, dex: Int, con: Int, int: Int, wis: Int, cha: Int) {
		viewModelScope.launch {
			characterDao.updateCharacterStats(characterId, str, dex, con, int, wis, cha)
		}
	}

    fun createNewCharacterSimple(characterData: CharacterCreationDataSimple) {
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
                hitPointMax = calculateStartingHP(characterData.classId, characterData.constitution),
                currentHitPoints = calculateStartingHP(characterData.classId, characterData.constitution),
                armorClass = 10 + characterData.dexterity.modifier(),
                speed = 30 // default speed
            )

            val characterId = characterDao.insertAndGetId(character).toInt()

            // Add skills if any
            characterData.skillProficiencies.forEach { skillId ->
                characterDao.insertCharacterSkill(
                    CharacterSkill(
                        characterId = characterId,
                        skillId = skillId,
                        proficiency = 1
                    )
                )
            }

            // Add equipment if any
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
        }
    }

    // Simple data class without complex features
    data class CharacterCreationDataSimple(
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
        val equipment: List<Int> = emptyList()
    )
}