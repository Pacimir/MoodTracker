package hr.ferit.moodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hr.ferit.moodtracker.data.local.Converters
import java.util.Date
import java.util.UUID

@Entity(tableName = "mood_entries")
@TypeConverters(Converters::class)
data class MoodEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val moodValue: Int = 3,
    val note: String = "",
    val timestamp: Date = Date(),
    val activities: List<String> = emptyList(),
    val startTime: String = "",
    val endTime: String = ""
)
