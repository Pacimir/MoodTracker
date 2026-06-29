package hr.ferit.moodtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.moodtracker.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val tips = listOf(
        "Prošeći barem 15 minuta na svježem zraku.",
        "Zapiši tri stvari na kojima si danas zahvalan/na.",
        "Nazovi dragu osobu i popričaj s njom.",
        "Slušaj omiljenu glazbu koja te opušta.",
        "Popij čašu vode i duboko udahni 5 puta."
    )
    val randomTip = remember { tips.random() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "MoodTracker", style = MaterialTheme.typography.headlineLarge)
            IconButton(onClick = { 
                auth.signOut()
                // The MainActivity will automatically switch to LoginScreen due to the observer
            }) {
                Text("Odjava", style = MaterialTheme.typography.labelSmall)
            }
        }
        
        user?.email?.let {
            Text(text = "Dobrodošao, $it", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Savjet dana:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = randomTip, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { navController.navigate(Screen.ActivityTracker.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Prati novu aktivnost")
        }
    }
}
