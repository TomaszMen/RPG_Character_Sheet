package com.example.rpg_character_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController


@Composable
fun CharactersScreen(viewModel: CharacterViewModel, navController: NavHostController) {
	val characters by viewModel.characters.collectAsStateWithLifecycle()

	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = { navController.navigate(Screens.CharacterAddScreen.route) },
				containerColor = Color.White
			) {
				Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
			}
		}
	) { paddingValues ->
		// List of characters
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			items(characters.size) {
				// Item
				Card(
					modifier = Modifier
						.padding(8.dp)
						.fillMaxWidth()
						.clickable {  // Action to take upon clicking on one of the items
							navController.navigate(
								Screens.CharacterDetailsScreen.createRoute(characters[it].characterId)
							)
						},
					shape = RoundedCornerShape(8.dp),
					elevation = CardDefaults.cardElevation(4.dp),
					colors = CardDefaults.cardColors(containerColor = Color.White)
				) {  // Content of the item
					val characterClass by viewModel.getClassById(characters[it].classId).collectAsState(initial = null)
					val characterRace by viewModel.getRaceById(characters[it].raceId).collectAsState(initial = null)

					Column(
						modifier = Modifier
							.padding(8.dp)
							.fillMaxWidth(),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							text = characters[it].characterName,
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
							textAlign = TextAlign.Center
						)

						Text(
							text = characterRace?.raceName ?: "Race not found",
							fontSize = 15.sp,
							textAlign = TextAlign.Center
						)

						Text(
							text = "Level " + characters[it].level,
							fontSize = 15.sp,
							textAlign = TextAlign.Center
						)

						Text(
							text = characterClass?.className ?: "Class not found",
							fontSize = 15.sp,
							textAlign = TextAlign.Center
						)
					}
				}
			}
		}
	}
}