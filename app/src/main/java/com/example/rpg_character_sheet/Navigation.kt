package com.example.rpg_character_sheet

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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

	val application = LocalContext.current.applicationContext as Application
	val viewModelFactory = remember { CharacterViewModelFactory(application) }
	val viewModel: CharacterViewModel = viewModel(factory = viewModelFactory)


    Scaffold(
		bottomBar = { BottomMenu(viewModel, navController) },
        content = { innerPadding ->
            Box(
				modifier = Modifier.padding(innerPadding)
            ) {
                NavGraph(viewModel, navController)
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
        Screens.CharacterDetailsScreen.route,  // Don't use it, instead use the createRoute method directly
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
fun BottomMenu(viewModel: CharacterViewModel, navController: NavHostController) {
    val selectedCharacterScreens = listOf(
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

		NavigationBarItem(
			label = { Text(text = BottomBar.CharacterDetailsScreen.title) },
			icon = { Icon(imageVector = BottomBar.CharacterDetailsScreen.icon, contentDescription = "icon") },
			enabled = currentDestination?.route != BottomBar.CharactersScreen.route &&
					currentDestination?.route != Screens.CharacterAddScreen.route &&
					currentDestination?.route != Screens.CharacterEditScreen.route,
			selected = currentDestination?.route == BottomBar.CharacterDetailsScreen.route,
			onClick = { navController.navigate(
				Screens.CharacterDetailsScreen.createRoute(viewModel.selectedCharacterId.value)
			) }
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
fun NavGraph(viewModel: CharacterViewModel, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.CharactersScreen.route
    ) {
        // Navigating to CharactersScreen
        composable(route = Screens.CharactersScreen.route) { CharactersScreen(viewModel, navController) }

		// Navigating to CharacterDetailsScreen
		composable(
			route = Screens.CharacterDetailsScreen.route,
			arguments = listOf(navArgument("characterId") { type = NavType.IntType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getInt("characterId")
			if (id != null) {
				CharacterDetailsScreen(id, viewModel, navController)
			}
		}

		// Navigating to CharacterEquipmentScreen
		composable(route = Screens.CharacterEquipmentScreen.route) { CharacterEquipmentScreen(viewModel, navController) }

		// Navigating to CharacterActionsScreen
		composable(route = Screens.CharacterActionsScreen.route) { CharacterActionsScreen(viewModel, navController) }

		// Navigating to CharacterPlayScreen
		composable(route = Screens.CharacterPlayScreen.route) { CharacterPlayScreen(viewModel, navController) }

		// Navigating to CharacterAddScreen
		composable(route = Screens.CharacterAddScreen.route) { CharacterAddScreen(viewModel, navController) }

        // Navigating to CharacterEditScreen
        composable(route = Screens.CharacterEditScreen.route) { CharacterEditScreen(viewModel, navController) }
    }
}