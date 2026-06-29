package hr.ferit.moodtracker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import hr.ferit.moodtracker.data.ActivityEntry
import hr.ferit.moodtracker.data.HappyPhoto
import hr.ferit.moodtracker.data.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class MoodViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
    
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries

    private val _happyPhotos = MutableStateFlow<List<HappyPhoto>>(emptyList())
    val happyPhotos: StateFlow<List<HappyPhoto>> = _happyPhotos

    private val _activityEntries = MutableStateFlow<List<ActivityEntry>>(emptyList())
    val activityEntries: StateFlow<List<ActivityEntry>> = _activityEntries

    init {
        fetchMoodEntries()
        fetchHappyPhotos()
        fetchActivityEntries()
    }

    private fun fetchMoodEntries() {
        if (currentUserId.isEmpty()) return
        db.collection("mood_entries")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _moodEntries.value = snapshot.toObjects(MoodEntry::class.java)
                }
            }
    }

    private fun fetchHappyPhotos() {
        if (currentUserId.isEmpty()) return
        db.collection("happy_photos")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _happyPhotos.value = snapshot.toObjects(HappyPhoto::class.java)
                }
            }
    }

    private fun fetchActivityEntries() {
        if (currentUserId.isEmpty()) return
        db.collection("activity_entries")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _activityEntries.value = snapshot.toObjects(ActivityEntry::class.java)
                }
            }
    }

    fun addMoodEntry(moodValue: Int, note: String, activities: List<String>) {
        if (currentUserId.isEmpty()) return
        val entry = MoodEntry(
            userId = currentUserId,
            moodValue = moodValue,
            note = note,
            activities = activities,
            timestamp = Timestamp.now()
        )
        db.collection("mood_entries").add(entry)
    }

    fun addActivity(type: String, durationMinutes: Int) {
        if (currentUserId.isEmpty()) return
        val entry = ActivityEntry(
            userId = currentUserId,
            activityType = type,
            durationMinutes = durationMinutes,
            timestamp = Timestamp.now()
        )
        db.collection("activity_entries").add(entry)
    }

    fun uploadHappyPhoto(uri: Uri, description: String) {
        if (currentUserId.isEmpty()) return
        val fileName = "${currentUserId}_${UUID.randomUUID()}"
        val ref = storage.reference.child("images/$fileName")
        
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                val photo = HappyPhoto(
                    userId = currentUserId,
                    imageUrl = downloadUri.toString(),
                    description = description,
                    timestamp = Timestamp.now()
                )
                db.collection("happy_photos").add(photo)
            }
        }
    }
}
