package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


		}
	} else {
		Text(
			text = "Character not found",
			textAlign = TextAlign.Center
		)
	}
}