package hr.ferit.moodtracker.data

import com.google.firebase.Timestamp

data class HappyPhoto(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
