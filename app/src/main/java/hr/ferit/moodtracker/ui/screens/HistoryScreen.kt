package hr.ferit.moodtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.moodtracker.data.MoodEntry
import hr.ferit.moodtracker.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: MoodViewModel = viewModel()) {
    val entries by viewModel.moodEntries.collectAsState()
    val activities by viewModel.activityEntries.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Povijest i Analitika", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (entries.isNotEmpty()) {
            val avgMood = entries.map { it.moodValue }.average()
            val totalActivityMinutes = activities.sumOf { it.durationMinutes }
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Analitika", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Prosječno raspoloženje: ${String.format("%.2f", avgMood)} / 5")
                    Text(text = "Ukupno aktivnosti: $totalActivityMinutes min")
                }
            }
        }
        
        Text("Zadnji unosi raspoloženja:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(entries) { entry ->
                MoodItem(entry)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Zadnje aktivnosti:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(activities) { activity ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${activity.activityType}: ${activity.durationMinutes} min")
                        val sdf = SimpleDateFormat("dd.MM.HH:mm", Locale.getDefault())
                        Text(sdf.format(activity.timestamp.toDate()), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun MoodItem(entry: MoodEntry) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Raspoloženje: ${entry.moodValue}/5", style = MaterialTheme.typography.titleMedium)
                val date = entry.timestamp.toDate()
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                Text(text = sdf.format(date), style = MaterialTheme.typography.bodySmall)
            }
            
            if (entry.activities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Aktivnosti: ${entry.activities.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            }

            if (entry.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = entry.note, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
