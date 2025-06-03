package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun CharacterDetailsScreen(characterId: Int, viewModel: CharacterViewModel, navController: NavHostController) {
	viewModel.selectCharacter(characterId)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value

	if (character != null) {

		val tempStrState = remember { mutableStateOf(character.strength.toString()) }
		val tempDexState = remember { mutableStateOf(character.dexterity.toString()) }
		val tempConState = remember { mutableStateOf(character.constitution.toString()) }
		val tempIntState = remember { mutableStateOf(character.intelligence.toString()) }
		val tempWisState = remember { mutableStateOf(character.wisdom.toString()) }
		val tempChaState = remember { mutableStateOf(character.charisma.toString()) }
		val tempLevelState = remember { mutableStateOf(character.level.toString()) }
		val tempNameState = remember { mutableStateOf(character.characterName) }

		Scaffold(
			floatingActionButton = {
				FloatingActionButton(
					onClick = {
						if (
							tempNameState.value != "" &&
							tempStrState.value != "" &&
							tempDexState.value != "" &&
							tempConState.value != "" &&
							tempIntState.value != "" &&
							tempWisState.value != "" &&
							tempChaState.value != "" &&
							tempLevelState.value != ""
						) {
							viewModel.updateCharacterName(character, tempNameState.value)
							viewModel.updateCharacterStats(
								character.characterId,
								tempStrState.value.toInt(),
								tempDexState.value.toInt(),
								tempConState.value.toInt(),
								tempIntState.value.toInt(),
								tempWisState.value.toInt(),
								tempChaState.value.toInt()
							)
							viewModel.updateCharacterLevel(
								character, tempLevelState.value.toInt()
						) }
							  },
					containerColor = Color.White
				) {
					Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
				}
			}
		) { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				// Name
				OutlinedTextField(
					value = tempNameState.value,
					onValueChange = {
						tempNameState.value = it },
					label = { Text("Name") },
					modifier = Modifier.fillMaxWidth()
				)

				// Race
				val characterRace by viewModel.getRaceByIdAsPair(character.raceId)
					.collectAsState(initial = null)
				val races by viewModel.getAllRacesAsPairs().collectAsState(initial = null)
				if (races != null && characterRace != null) {
					Spinner(races, characterRace, "Race") {
						viewModel.updateCharacterRace(character, it.first)
						viewModel.updateCharacterSubrace(character, 30)  // Set to no subrace
					}
				}

				// Subrace
				val characterSubrace by viewModel.getSubraceByIdAsPair(character.subraceId)
					.collectAsState(initial = null)
				val subraces by viewModel.getSubracesOfRaceAsPairs(character.raceId)
					.collectAsState(initial = null)
				if (subraces != null && characterSubrace != null) {
					Spinner(subraces, characterSubrace, "Subrace") {
						viewModel.updateCharacterSubrace(character, it.first)
					}
				}

				// Class
				val characterClass by viewModel.getClassByIdAsPair(character.classId)
					.collectAsState(initial = null)
				val classes by viewModel.getAllClassesAsPair().collectAsState(initial = null)
				if (characterClass != null && classes != null) {
					Spinner(classes, characterClass, "Class") {
						viewModel.updateCharacterClass(character, it.first)
						viewModel.updateCharacterSubclass(character, 41)  // Set to no subclass
					}
				}

				// Subclass
				val characterSubclass by viewModel.getSubclassByIdAsPair(character.subclassId)
					.collectAsState(initial = null)
				val subclasses by viewModel.getSubclassesOfClassAsPairs(character.classId)
					.collectAsState(initial = null)
				if (characterSubclass != null && subclasses != null) {
					Spinner(subclasses, characterSubclass, "Class") {
						viewModel.updateCharacterSubclass(character, it.first)
					}
				}

				// Stats

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceEvenly
				) {
					OutlinedTextField(
						value = tempStrState.value,
						onValueChange = {
							tempStrState.value = it
						},
						label = { Text("STR") },
						modifier = Modifier.width(60.dp)
					)

					OutlinedTextField(
						value = tempDexState.value,
						onValueChange = {
							tempDexState.value = it
						},
						label = { Text("DEX") },
						modifier = Modifier.width(60.dp)
					)

					OutlinedTextField(
						value = tempConState.value,
						onValueChange = {
							tempConState.value = it
						},
						label = { Text("CON") },
						modifier = Modifier.width(60.dp)
					)

					OutlinedTextField(
						value = tempIntState.value,
						onValueChange = {
							tempIntState.value = it
						},
						label = { Text("INT") },
						modifier = Modifier.width(60.dp)
					)

					OutlinedTextField(
						value = tempWisState.value,
						onValueChange = {
							tempWisState.value = it
						},
						label = { Text("WIS") },
						modifier = Modifier.width(60.dp)
					)

					OutlinedTextField(
						value = tempChaState.value,
						onValueChange = {
							tempChaState.value = it
						},
						label = { Text("CHA") },
						modifier = Modifier.width(60.dp)
					)
				}

				OutlinedTextField(
					value = tempLevelState.value,
					onValueChange = {
						tempLevelState.value = it
					},
					label = { Text("LVL") },
					modifier = Modifier.width(60.dp)
				)
			}
		}
	} else {
		Text(
			text = "Character details not found",
			textAlign = TextAlign.Center
		)
	}
}