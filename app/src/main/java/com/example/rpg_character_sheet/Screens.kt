package com.example.rpg_character_sheet


/* Definition of routes to each screen */

sealed class Screens(val route: String) {
	// Accessible from bottom bar
	data object CharactersScreen : Screens("characters_screen")
	data object CharacterDetailsScreen : Screens("character_details_screen/{characterId}") {
		fun createRoute(characterId: Int): String = "character_details_screen/$characterId"
	}
	data object CharacterEquipmentScreen : Screens("character_equipment_screen")
	data object CharacterActionsScreen : Screens("character_actions_screen")
	data object CharacterPlayScreen : Screens("character_play_screen")

	data object CharacterAddScreen : Screens("character_add_screen")
	data object CharacterEditScreen : Screens("character_edit_screen")
}