package com.example.rpg_character_sheet


/* Definition of routes to each screen */

sealed class Screens(val route: String) {
    data object CharactersScreen : Screens("characters_screen")
    data object CharacterAddScreen : Screens("character_add_screen")
    data object CharacterEditScreen : Screens("character_edit_screen/{characterId}") {
        fun createRoute(characterId: Int): String = "character_edit_screen/$characterId"
    }
}