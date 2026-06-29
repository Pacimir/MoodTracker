package hr.ferit.moodtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.moodtracker.viewmodel.MoodViewModel
import kotlinx.coroutines.delay

@Composable
fun ActivityTrackerScreen(navController: NavController, viewModel: MoodViewModel = viewModel()) {
    var activityType by remember { mutableStateOf("Šetnja") }
    val activities = listOf("Šetnja", "Trčanje", "Vježbanje", "Joga", "Meditacija")
    
    var isRunning by remember { mutableStateOf(false) }
    var timeSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            timeSeconds++
        }
    }

    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Prati Aktivnost", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Text("Odaberi aktivnost:")
        ScrollableTabRow(
            selectedTabIndex = activities.indexOf(activityType),
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            activities.forEach { activity ->
                Tab(
                    selected = activityType == activity,
                    onClick = { activityType = activity },
                    text = { Text(activity) }
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRunning) "Pauziraj" else "Započni")
            }

            Button(
                onClick = {
                    isRunning = false
                    if (timeSeconds > 0) {
                        viewModel.addActivity(activityType, timeSeconds / 60)
                    }
                    timeSeconds = 0
                },
                enabled = timeSeconds > 0
            ) {
                Text("Završi i Spremi")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Povratak")
        }
    }
}
