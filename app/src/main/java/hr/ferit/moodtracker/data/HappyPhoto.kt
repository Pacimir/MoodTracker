package hr.ferit.moodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hr.ferit.moodtracker.data.local.Converters
import java.util.Date
import java.util.UUID

@Entity(tableName = "happy_photos")
@TypeConverters(Converters::class)
data class HappyPhoto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Date = Date()
)
