package hr.ferit.moodtracker.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.moodtracker.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddMoodScreen(navController: NavController, viewModel: MoodViewModel = viewModel()) {
    val context = LocalContext.current
    var moodValue by remember { mutableFloatStateOf(3f) }
    var note by remember { mutableStateOf("") }
    val activities = listOf("Šetnja", "Trčanje", "Čitanje", "Druženje", "Vježbanje")
    val selectedActivities = remember { mutableStateListOf<String>() }
    
    var selectedDate by remember { mutableStateOf(Date()) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var isTestMode by remember { mutableStateOf(false) }
    
    val dateSdf = remember { SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()) }

    fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { time = selectedDate }
        DatePickerDialog(context, { _, year, month, day ->
            val newCal = Calendar.getInstance().apply {
                time = selectedDate
                set(year, month, day)
            }
            selectedDate = newCal.time
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showTimePicker(initialTime: String, onTimeSelected: (String) -> Unit) {
        val parts = initialTime.split(":")
        TimePickerDialog(context, { _, h, m ->
            onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", h, m))
        }, parts[0].toInt(), parts[1].toInt(), true).show()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unos aktivnosti", style = MaterialTheme.typography.headlineMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Test Mode", style = MaterialTheme.typography.labelSmall)
                Switch(checked = isTestMode, onCheckedChange = { isTestMode = it })
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Datum: ${dateSdf.format(selectedDate)}", style = MaterialTheme.typography.titleSmall)
                if (isTestMode) {
                    Button(onClick = { showDatePicker() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Promijeni datum")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TimeDisplay("Od:", startTime) { showTimePicker(startTime) { startTime = it } }
                    TimeDisplay("Do:", endTime) { showTimePicker(endTime) { endTime = it } }
                }
            }
        }
        
        Text("Raspoloženje: ${moodValue.toInt()}")
        Slider(value = moodValue, onValueChange = { moodValue = it }, valueRange = 1f..5f, steps = 3)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Što si radio/la?", style = MaterialTheme.typography.titleMedium)
        
        activities.chunked(3).forEach { rowActivities ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowActivities.forEach { activity ->
                    val isSelected = selectedActivities.contains(activity)
                    FilterChip(
                        selected = isSelected,
                        onClick = { if (isSelected) selectedActivities.remove(activity) else selectedActivities.add(activity) },
                        label = { Text(activity) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Bilješka") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { 
                viewModel.addMoodEntry(moodValue.toInt(), note, selectedActivities.toList(), selectedDate, startTime, endTime)
                navController.popBackStack() 
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Spremi") }
    }
}

@Composable
fun TimeDisplay(label: String, time: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        TextButton(onClick = onClick) {
            Text(time, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
