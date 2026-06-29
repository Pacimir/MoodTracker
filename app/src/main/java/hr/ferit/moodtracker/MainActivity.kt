package hr.ferit.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.moodtracker.ui.navigation.MoodNavigation
import hr.ferit.moodtracker.ui.screens.LoginScreen
import hr.ferit.moodtracker.ui.theme.MoodTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MoodTrackerTheme {
                var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                
                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { auth ->
                        currentUser = auth.currentUser
                    }
                    FirebaseAuth.getInstance().addAuthStateListener(listener)
                    onDispose {
                        FirebaseAuth.getInstance().removeAuthStateListener(listener)
                    }
                }
                
                if (currentUser == null) {
                    LoginScreen(onLoginSuccess = {
                        currentUser = FirebaseAuth.getInstance().currentUser
                    })
                } else {
                    MoodNavigation()
                }
            }
        }
    }
}
