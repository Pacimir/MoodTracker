package hr.ferit.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.moodtracker.ui.navigation.MoodNavigation
import hr.ferit.moodtracker.ui.screens.LoginScreen
import hr.ferit.moodtracker.ui.theme.MoodTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodTrackerTheme {
                val auth = FirebaseAuth.getInstance()
                var currentUser by remember { mutableStateOf(auth.currentUser) }
                
                LaunchedEffect(Unit) {
                    auth.addAuthStateListener { 
                        currentUser = it.currentUser
                    }
                }

                if (currentUser == null) {
                    LoginScreen(onLoginSuccess = {
                        // Korisnik će se automatski osvježiti preko AuthStateListener-a
                    })
                } else {
                    MoodNavigation()
                }
            }
        }
    }
}
