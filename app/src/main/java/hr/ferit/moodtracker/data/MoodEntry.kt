package hr.ferit.moodtracker.data

import com.google.firebase.Timestamp

data class MoodEntry(
    val id: String = "",
    val userId: String = "",
    val moodValue: Int = 3, // 1-5 scale
    val note: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val activities: List<String> = emptyList()
)
