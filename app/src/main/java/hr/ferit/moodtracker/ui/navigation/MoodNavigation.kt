package hr.ferit.moodtracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.ferit.moodtracker.ui.screens.AddMoodScreen
import hr.ferit.moodtracker.ui.screens.HomeScreen
import hr.ferit.moodtracker.ui.screens.HistoryScreen
import hr.ferit.moodtracker.ui.screens.AlbumScreen
import hr.ferit.moodtracker.ui.screens.ActivityTrackerScreen

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(Screen.Home.route, "Početna", Icons.Default.Home)
    object History : BottomBarScreen(Screen.History.route, "Povijest", Icons.Default.History)
    object Album : BottomBarScreen(Screen.Album.route, "Album", Icons.Default.PhotoLibrary)
}

@Composable
fun MoodNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.History,
        BottomBarScreen.Album
    )

    Scaffold(
        bottomBar = {
            if (bottomBarScreens.any { it.route == currentDestination?.route }) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Home.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddMood.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj raspoloženje")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.AddMood.route) {
                AddMoodScreen(navController)
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Album.route) {
                AlbumScreen()
            }
            composable(Screen.ActivityTracker.route) {
                ActivityTrackerScreen(navController)
            }
        }
    }
}
