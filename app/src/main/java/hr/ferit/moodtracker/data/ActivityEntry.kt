package hr.ferit.moodtracker.data

import com.google.firebase.Timestamp

data class ActivityEntry(
    val id: String = "",
    val userId: String = "",
    val activityType: String = "", // e.g., "Walking", "Running"
    val durationMinutes: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
)
