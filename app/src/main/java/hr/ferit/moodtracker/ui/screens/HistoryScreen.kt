package hr.ferit.moodtracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.moodtracker.data.MoodEntry
import hr.ferit.moodtracker.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodChart(entries: List<MoodEntry>) {
    val lastSevenEntries = entries.takeLast(7)
    
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Trend raspoloženja",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (lastSevenEntries.size < 2) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Unesi još podataka za grafikon", color = Color.Gray)
                }
            } else {
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spaceBetween = width / (lastSevenEntries.size - 1)
                    
                    val points = lastSevenEntries.mapIndexed { index, entry ->
                        val x = index * spaceBetween
                        val y = height - ((entry.moodValue - 1) / 4f * height)
                        androidx.compose.ui.geometry.Offset(x, y)
                    }

                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.forEach { point -> lineTo(point.x, point.y) }
                    }

                    drawPath(path = path, color = primaryColor, style = Stroke(width = 3.dp.toPx()))
                    points.forEach { point ->
                        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = point)
                        drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = point)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MoodViewModel = viewModel()) {
    val entries by viewModel.moodEntries.collectAsState()
    val analytics by viewModel.analyticsData.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Povijest i Analitika", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MoodChart(entries) }
            
            item {
                PersonalizedAdviceCard(analytics)
            }
            
            if (entries.isNotEmpty()) {
                item {
                    AnalyticsSummary(entries)
                }
            }

            item {
                Text(
                    "Zadnji unosi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(entries) { entry ->
                MoodItem(entry)
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun PersonalizedAdviceCard(analytics: hr.ferit.moodtracker.data.AnalyticsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Personalizirani savjet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = analytics.personalizedTip,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            if (analytics.bestActivity != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tvoja 'Happy' aktivnost: ${analytics.bestActivity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AnalyticsSummary(entries: List<MoodEntry>) {
    val avgMood = entries.map { it.moodValue }.average()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Mood, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Prosječno raspoloženje", style = MaterialTheme.typography.labelMedium)
                Text(
                    "${String.format("%.1f", avgMood)} / 5.0",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MoodItem(entry: MoodEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sdf = SimpleDateFormat("EEEE, dd. MMMM", Locale("hr"))
                Text(
                    text = sdf.format(entry.timestamp).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${entry.moodValue}/5",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (entry.startTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${entry.startTime} - ${entry.endTime}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            if (entry.activities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    entry.activities.forEach { activity ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(activity, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            if (entry.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
