package hr.ferit.moodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hr.ferit.moodtracker.data.local.Converters
import java.util.Date
import java.util.UUID

@Entity(tableName = "activity_entries")
@TypeConverters(Converters::class)
data class ActivityEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val activityType: String = "",
    val durationMinutes: Int = 0,
    val timestamp: Date = Date()
)
