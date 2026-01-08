package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun CharacterEditScreen(viewModel: CharacterViewModel, navController: NavHostController) {
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