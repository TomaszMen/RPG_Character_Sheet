package com.example.rpg_character_sheet

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


/* Definition of app navigation */

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Scaffold(
		bottomBar = { BottomMenu(navController = navController) },
        content = { innerPadding ->
            Box(
				modifier = Modifier.padding(innerPadding)
            ) {
                NavGraph(navController = navController)
            }
		}
    )
}

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object CharactersScreen : BottomBar(
        Screens.CharactersScreen.route,
        "Characters",
        Icons.Default.Home
    )

    data object CharacterDetailsScreen : BottomBar(
        Screens.CharacterDetailsScreen.route,
        "Details",
        Icons.Default.AccountCircle
    )

    data object CharacterEquipmentScreen : BottomBar(
        Screens.CharacterEquipmentScreen.route,
        "Equipment",
        Icons.Default.Menu
    )

    data object CharacterActionsScreen : BottomBar(
        Screens.CharacterActionsScreen.route,
        "Actions",
        Icons.Default.Star
    )

    data object CharacterPlayScreen : BottomBar(
        Screens.CharacterPlayScreen.route,
        "Play",
        Icons.Default.PlayArrow
    )
}

@Composable
fun BottomMenu(navController: NavHostController) {
    val selectedCharacterScreens = listOf(
        BottomBar.CharacterDetailsScreen,
        BottomBar.CharacterEquipmentScreen,
        BottomBar.CharacterActionsScreen,
        BottomBar.CharacterPlayScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.LightGray
    ) {
        // Main screen
        NavigationBarItem(
            label = { Text(text = BottomBar.CharactersScreen.title) },
            icon = { Icon(imageVector = BottomBar.CharactersScreen.icon, contentDescription = "icon") },
            selected = currentDestination?.route == BottomBar.CharactersScreen.route,
            onClick = { navController.navigate(BottomBar.CharactersScreen.route) }
        )

        selectedCharacterScreens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = "icon") },
                enabled = currentDestination?.route != BottomBar.CharactersScreen.route &&
				          currentDestination?.route != Screens.CharacterAddScreen.route &&
				          currentDestination?.route != Screens.CharacterEditScreen.route,
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
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

		// Navigating to CharacterDetailsScreen
		composable(
			route = Screens.CharacterDetailsScreen.route,
			arguments = listOf(navArgument("characterId") { type = NavType.StringType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getInt("characterId")
			if (id != null) {
				CharacterDetailsScreen(id, navController)
			}
		}

		// Navigating to CharacterEquipmentScreen
		composable(route = Screens.CharacterEquipmentScreen.route) { CharacterEquipmentScreen(navController) }

		// Navigating to CharacterActionsScreen
		composable(route = Screens.CharacterActionsScreen.route) { CharacterActionsScreen(navController) }

		// Navigating to CharacterPlayScreen
		composable(route = Screens.CharacterPlayScreen.route) { CharacterPlayScreen(navController) }

		// Navigating to CharacterAddScreen
		composable(route = Screens.CharacterAddScreen.route) { CharacterAddScreen(navController) }

        // Navigating to CharacterEditScreen
        composable(route = Screens.CharacterEditScreen.route) { CharacterEditScreen(navController) }
    }
}