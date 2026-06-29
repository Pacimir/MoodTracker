package hr.ferit.moodtracker.data

import java.util.Date

data class UserData(
    val uid: String = "",
    val email: String = "",
    val lastLogin: Date = Date()
)
