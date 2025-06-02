package com.example.rpg_character_sheet

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import table_entities.Character


/* Definition of how screens look and how to interact with them */

// Accessible from bottom bar
@Composable
fun CharactersScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
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
				Box(
					modifier = Modifier
						.fillParentMaxWidth()
						.padding(8.dp)  // Padding of items
						.shadow(4.dp, shape = RoundedCornerShape(8.dp), clip = true)
						.clickable {  // Action to take upon clicking on one of the items
							navController.navigate(Screens.CharacterEditScreen.route)
						}
						.padding(8.dp)  // Inner padding of the content

				) {
					// Content of the item
					Text(
						text = characters[it].characterName,
						fontSize = 20.sp
					)
					// nazwa, klasa, poziom, rasa
				}
			}
		}
	}
}

@Composable
fun CharacterDetailsScreen(characterId: Int, navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
	viewModel.selectCharacter(characterId)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value
}

@Composable
fun CharacterEquipmentScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value
}

@Composable
fun CharacterActionsScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value
}

@Composable
fun CharacterPlayScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value
}

// Not accessible from bottom bar
@Composable
fun CharacterAddScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)

	// var all enterable fields

	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = { /* viewModel.insertCharacter(...) */ navController.navigate(Screens.CharactersScreen.route) },
				containerColor = Color.White
			) {
				Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
			}
		}
	) { paddingValues ->
		Text(
			text = "Add",
			fontSize = 20.sp,
			modifier = Modifier.padding(paddingValues)
		)
	}
}

@Composable
fun CharacterEditScreen(navController: NavHostController) {
	val viewModel: CharacterViewModel = viewModel(
		LocalViewModelStoreOwner.current!!,
		"CharacterViewModel",
		CharacterViewModelFactory(LocalContext.current.applicationContext as Application)
	)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value

	// var all enterable fields

	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = { /* viewModel.editCharacter(...) */ navController.navigate(Screens.CharactersScreen.route) },
				containerColor = Color.White
			) {
				Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
			}
		}
	) { paddingValues ->
		Text(
			text = "Edit",
			fontSize = 20.sp,
			modifier = Modifier.padding(paddingValues)
		)
	}
}