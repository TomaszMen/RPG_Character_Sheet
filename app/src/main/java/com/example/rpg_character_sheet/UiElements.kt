package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController


/* Definition of how screens look and how to interact with them */

@Composable
fun CharactersScreen(navController: NavHostController) {
	// viewmodel goes here
	//val characters by viewmodel.characters.collectAsStateWithLifecycle()

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
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			//items(characters.size)
		}
	}
}

@Composable
fun CharacterAddScreen(navController: NavHostController) {
	// viewmodel goes here


}

@Composable
fun CharacterEditScreen(characterId: Int, navController: NavHostController) {
	// viewmodel goes here
	// selectedCharacter = viewmodel.getCharacterById(characterId).collectAsState(initial = null).value


}