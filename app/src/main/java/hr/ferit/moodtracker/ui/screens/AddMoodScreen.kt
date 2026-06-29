package hr.ferit.moodtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.moodtracker.viewmodel.MoodViewModel

@Composable
fun AddMoodScreen(navController: NavController, viewModel: MoodViewModel = viewModel()) {
    var moodValue by remember { mutableStateOf(3f) }
    var note by remember { mutableStateOf("") }
    val activities = listOf("Šetnja", "Trčanje", "Čitanje", "Druženje", "Vježbanje")
    val selectedActivities = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Kako se osjećaš danas?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Raspoloženje: ${moodValue.toInt()}")
        Slider(
            value = moodValue,
            onValueChange = { moodValue = it },
            valueRange = 1f..5f,
            steps = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Što si danas radio/la?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Using a wrapped Row-like layout for activities
        Column(modifier = Modifier.fillMaxWidth()) {
            val rows = activities.chunked(3)
            rows.forEach { rowActivities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowActivities.forEach { activity ->
                        val isSelected = selectedActivities.contains(activity)
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                if (isSelected) selectedActivities.remove(activity) 
                                else selectedActivities.add(activity) 
                            },
                            label = { Text(activity) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Bilješka") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                viewModel.addMoodEntry(moodValue.toInt(), note, selectedActivities.toList())
                navController.popBackStack() 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi")
        }
    }
}
