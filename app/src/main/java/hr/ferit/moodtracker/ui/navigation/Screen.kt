package hr.ferit.moodtracker.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddMood : Screen("add_mood")
    object History : Screen("history")
    object Album : Screen("album")
    object ActivityTracker : Screen("activity_tracker")
}
