package hr.ferit.moodtracker.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.moodtracker.data.*
import hr.ferit.moodtracker.data.local.MoodDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MoodDatabase.getDatabase(application)
    private val dao = db.moodDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    val userId = MutableStateFlow(auth.currentUser?.uid ?: "")

    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries

    private val _happyPhotos = MutableStateFlow<List<HappyPhoto>>(emptyList())
    val happyPhotos: StateFlow<List<HappyPhoto>> = _happyPhotos

    private val _activityEntries = MutableStateFlow<List<ActivityEntry>>(emptyList())
    val activityEntries: StateFlow<List<ActivityEntry>> = _activityEntries

    private val _analyticsData = MutableStateFlow(AnalyticsData())
    val analyticsData: StateFlow<AnalyticsData> = _analyticsData

    private var refreshJob: Job? = null

    init {
        auth.addAuthStateListener { 
            val uid = it.currentUser?.uid ?: ""
            userId.value = uid
            
            if (uid != "") {
                val userMap = hashMapOf(
                    "uid" to uid,
                    "email" to (it.currentUser?.email ?: ""),
                    "lastLogin" to Date()
                )
                firestore.collection("users").document(uid).set(userMap)
                
                fetchData()
                startRefreshing()
            } else {
                refreshJob?.cancel()
            }
        }
    }

    fun fetchData() {
        val uid = userId.value
        if (uid == "") return

        viewModelScope.launch {
            try {
                val resMoods = firestore.collection("users").document(uid).collection("mood_entries").get().await()
                val listMoods = resMoods.toObjects(MoodEntry::class.java)
                withContext(Dispatchers.IO) {
                    dao.insertMoodEntries(listMoods)
                }

                val resActs = firestore.collection("users").document(uid).collection("activity_entries").get().await()
                val listActs = resActs.toObjects(ActivityEntry::class.java)
                withContext(Dispatchers.IO) {
                    dao.insertActivityEntries(listActs)
                }

                val resPhotos = firestore.collection("users").document(uid).collection("happy_photos").get().await()
                val listPhotos = resPhotos.toObjects(HappyPhoto::class.java)
                withContext(Dispatchers.IO) {
                    dao.insertHappyPhotos(listPhotos)
                }
            } catch (e: Exception) {
                Log.e("MOOD_APP", "Greška kod sinkronizacije: " + e.message)
            }
        }
    }

    private fun startRefreshing() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val uid = userId.value
            
            launch {
                dao.getMoodEntriesByUser(uid).collect { entries ->
                    _moodEntries.value = entries
                    _analyticsData.value = calculateAnalytics(entries)
                }
            }
            
            launch {
                dao.getHappyPhotosByUser(uid).collect { _happyPhotos.value = it }
            }
            
            launch {
                dao.getActivityEntriesByUser(uid).collect { _activityEntries.value = it }
            }
        }
    }

    fun addMoodEntry(value: Int, note: String, activities: List<String>, date: Date, startTime: String, endTime: String) {
        val uid = userId.value
        if (uid == "") return
        
        val newEntry = MoodEntry(UUID.randomUUID().toString(), uid, value, note, date, activities, startTime, endTime)
        
        viewModelScope.launch {
            withContext(Dispatchers.IO) { dao.insertMoodEntry(newEntry) }
            firestore.collection("users").document(uid).collection("mood_entries").document(newEntry.id).set(newEntry)
        }
    }

    fun addActivity(type: String, duration: Int) {
        val uid = userId.value
        if (uid == "") return
        
        val newActivity = ActivityEntry(UUID.randomUUID().toString(), uid, type, duration, Date())
        
        viewModelScope.launch {
            withContext(Dispatchers.IO) { dao.insertActivityEntry(newActivity) }
            firestore.collection("users").document(uid).collection("activity_entries").document(newActivity.id).set(newActivity)
        }
    }

    fun uploadHappyPhoto(uri: Uri, description: String) {
        val uid = userId.value
        if (uid == "") return
        
        val context = getApplication<Application>().applicationContext
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val input = context.contentResolver.openInputStream(uri)
                    val output = file.outputStream()
                    input?.copyTo(output)
                    input?.close()
                    output.close()
                }
                
                val path = Uri.fromFile(file).toString()
                val photo = HappyPhoto(UUID.randomUUID().toString(), uid, path, description, Date())
                
                withContext(Dispatchers.IO) { dao.insertHappyPhoto(photo) }
                firestore.collection("users").document(uid).collection("happy_photos").document(photo.id).set(photo)
            } catch (e: Exception) {
                Log.e("MOOD_APP", "Greška sa slikom: ${e.message}")
            }
        }
    }

    fun clearAllLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAllMoods()
            dao.deleteAllActivities()
            dao.deleteAllPhotos()
        }
    }

    fun calculateAnalytics(entries: List<MoodEntry>): AnalyticsData {
        if (entries.isEmpty()) return AnalyticsData()
        
        var sum = 0.0
        for (m in entries) {
            sum += m.moodValue
        }
        val average = sum / entries.size
        
        var tip = "Nastavi bilježiti svoje raspoloženje za personalizirane savjete!"
        if (average >= 4.0) tip = "Sjajno se osjećaš! Nastavi s aktivnostima koje te usrećuju."
        else if (average >= 3.0) tip = "Uglavnom si dobro. Možda bi šetnja dodatno pomogla?"
        else tip = "Primjećujemo da si lošije volje. Pokušaj se odmoriti ili razgovarati s nekim."
        
        // Find most frequent activity when mood is high
        val goodMoodActivities = entries.filter { it.moodValue >= 4 }.flatMap { it.activities }
        val bestActivity = goodMoodActivities.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        
        return AnalyticsData(average, bestActivity, entries.size, tip)
    }
}
