package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun CharacterDetailsScreen(characterId: Int, viewModel: CharacterViewModel, navController: NavHostController) {
	viewModel.selectCharacter(characterId)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value

	if (character != null) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// Name
			OutlinedTextField(
				value = character.characterName,
				onValueChange = { viewModel.updateCharacterName(character, it) },
				label = { Text("Name") },
				modifier = Modifier.fillMaxWidth()
			)

			// Race
			val characterRace by viewModel.getRaceByIdAsPair(character.raceId).collectAsState(initial = null)
			val races by viewModel.getAllRacesAsPairs().collectAsState(initial = null)
			if (races != null && characterRace != null) {
				Spinner(races, characterRace, "Race") {
					viewModel.updateCharacterRace(character, it.first)
					viewModel.updateCharacterSubrace(character, 30)  // Set to no subrace
				}
			}

			// Subrace
			val characterSubrace by viewModel.getSubraceByIdAsPair(character.subraceId).collectAsState(initial = null)
			val subraces by viewModel.getSubracesOfRaceAsPairs(character.raceId).collectAsState(initial = null)
			if (subraces != null && characterSubrace != null) {
				Spinner(subraces, characterSubrace, "Subrace") {
					viewModel.updateCharacterSubrace(character, it.first)
				}
			}

			// Class
			val characterClass by viewModel.getClassByIdAsPair(character.classId).collectAsState(initial = null)
			val classes by viewModel.getAllClassesAsPair().collectAsState(initial = null)
			if (characterClass != null && classes != null) {
				Spinner(classes, characterClass, "Class") {
					viewModel.updateCharacterClass(character, it.first)
					viewModel.updateCharacterSubclass(character, 41)  // Set to no subclass
				}
			}

			// Subclass
			val characterSubclass by viewModel.getSubclassByIdAsPair(character.subclassId).collectAsState(initial = null)
			val subclasses by viewModel.getSubclassesOfClassAsPairs(character.classId).collectAsState(initial = null)
			if (characterSubclass != null && subclasses != null) {
				Spinner(subclasses, characterSubclass, "Class") {
					viewModel.updateCharacterSubclass(character, it.first)
				}
			}

			// Stats
			val strState = remember { mutableIntStateOf(0) }
			val dexState = remember { mutableIntStateOf(0) }
			val conState = remember { mutableIntStateOf(0) }
			val intState = remember { mutableIntStateOf(0) }
			val wisState = remember { mutableIntStateOf(0) }
			val chaState = remember { mutableIntStateOf(0) }

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				OutlinedTextField(
					value = character.strength.toString(),
					onValueChange = {
						strState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("STR") },
					modifier = Modifier.width(60.dp)
				)

				OutlinedTextField(
					value = character.dexterity.toString(),
					onValueChange = {
						dexState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("DEX") },
					modifier = Modifier.width(60.dp)
				)

				OutlinedTextField(
					value = character.constitution.toString(),
					onValueChange = {
						conState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("CON") },
					modifier = Modifier.width(60.dp)
				)

				OutlinedTextField(
					value = character.intelligence.toString(),
					onValueChange = {
						intState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("INT") },
					modifier = Modifier.width(60.dp)
				)

				OutlinedTextField(
					value = character.wisdom.toString(),
					onValueChange = {
						wisState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("WIS") },
					modifier = Modifier.width(60.dp)
				)

				OutlinedTextField(
					value = character.charisma.toString(),
					onValueChange = {
						chaState.intValue = it.toInt()
						viewModel.updateCharacterStats(
							character.characterId, strState.intValue, dexState.intValue, conState.intValue,
							intState.intValue, wisState.intValue, chaState.intValue
						)},
					label = { Text("CHA") },
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