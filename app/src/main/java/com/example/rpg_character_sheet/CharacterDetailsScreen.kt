package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
			Text(
				text = character.characterName,
				fontSize = 20.sp,
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center
			)

			// Race
			val characterRace by viewModel.getRaceById(character.raceId).collectAsState(initial = null)
			Text(
				text = characterRace?.raceName ?: "No race",
				fontSize = 15.sp,
				textAlign = TextAlign.Center
			)

			// Subrace
			if (character.subraceId != null) {
				val characterSubrace by viewModel.getSubraceById(character.subraceId).collectAsState(initial = null)
				Text(
					text = characterSubrace?.subraceName ?: "No subrace",
					fontSize = 15.sp,
					textAlign = TextAlign.Center
				)

				//val subraces by viewModel.getSubracesOfRaceAsPairs(character.raceId).collectAsState(initial = null)
				//val subracePair by viewModel.getSubraceByIdAsPair(character.subraceId).collectAsState(initial = null)

				//Spinner(subraces!!, subracePair!!) {  }
			} else {
				Text(
					text = "No subrace",
					fontSize = 15.sp,
					textAlign = TextAlign.Center
				)
			}

			//


		}
	} else {
		Text(
			text = "Character not found",
			textAlign = TextAlign.Center
		)
	}
}