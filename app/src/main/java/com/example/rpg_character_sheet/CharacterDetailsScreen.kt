package com.example.rpg_character_sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController


@Composable
fun CharacterDetailsScreen(characterId: Int, viewModel: CharacterViewModel, navController: NavHostController) {
	viewModel.selectCharacter(characterId)
	val character = viewModel.getSelectedCharacter().collectAsState(initial = null).value


}