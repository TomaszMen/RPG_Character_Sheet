package com.example.rpg_character_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


/* Definition of app navigation */

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Scaffold(
      content = { innerPadding ->
          Box(
              modifier = Modifier.padding(innerPadding)
          ) {
            NavGraph(navController = navController)
          }
      }
    )
}

// Specifies how each route is taken
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.CharactersScreen.route
    ) {
        // Navigating to CharactersScreen
        composable(route = Screens.CharactersScreen.route) { CharactersScreen(navController) }

        // Navigating to CharacterAddScreen
        composable(route = Screens.CharacterAddScreen.route) { CharacterAddScreen(navController) }

        // Navigating to CharacterEditScreen
        composable(
            route = Screens.CharacterEditScreen.route,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("characterId")
            if (id != null) {
                CharacterEditScreen(id, navController)
            }
        }
    }
}