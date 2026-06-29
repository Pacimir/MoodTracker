package hr.ferit.moodtracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CustomColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = OffWhite,
    primaryContainer = Mint,
    secondary = Coral,
    onSecondary = OffWhite,
    background = SoftGreen,
    surface = OffWhite,
    surfaceVariant = SkyBlue
)

@Composable
fun MoodTrackerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = CustomColorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    MaterialTheme(
        colorScheme = CustomColorScheme,
        typography = Typography,
        content = content
    )
}
