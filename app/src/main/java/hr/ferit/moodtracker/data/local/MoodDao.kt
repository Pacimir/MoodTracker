package hr.ferit.moodtracker.data.local

import androidx.room.*
import hr.ferit.moodtracker.data.ActivityEntry
import hr.ferit.moodtracker.data.HappyPhoto
import hr.ferit.moodtracker.data.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodEntriesByUser(userId: String): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntries(entries: List<MoodEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Query("SELECT * FROM activity_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivityEntriesByUser(userId: String): Flow<List<ActivityEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityEntries(entries: List<ActivityEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityEntry(entry: ActivityEntry)

    @Query("SELECT * FROM happy_photos WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHappyPhotosByUser(userId: String): Flow<List<HappyPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHappyPhotos(photos: List<HappyPhoto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHappyPhoto(photo: HappyPhoto)
    
    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoods()

    @Query("DELETE FROM activity_entries")
    suspend fun deleteAllActivities()

    @Query("DELETE FROM happy_photos")
    suspend fun deleteAllPhotos()
}
