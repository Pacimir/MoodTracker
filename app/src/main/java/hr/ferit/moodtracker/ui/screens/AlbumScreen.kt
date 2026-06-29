package hr.ferit.moodtracker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import hr.ferit.moodtracker.viewmodel.MoodViewModel

@Composable
fun AlbumScreen(viewModel: MoodViewModel = viewModel()) {
    val photos by viewModel.happyPhotos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            showDialog = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Moj Album Sreće", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { launcher.launch("image/*") }, 
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dodaj sliku koja te veseli")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photo ->
                Card(elevation = CardDefaults.cardElevation(4.dp)) {
                    Column {
                        AsyncImage(
                            model = photo.imageUrl,
                            contentDescription = photo.description,
                            modifier = Modifier.aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                        if (photo.description.isNotEmpty()) {
                            Text(
                                text = photo.description,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedImageUri != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Dodaj opis") },
            text = {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Što te usrećilo na ovoj slici?") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.uploadHappyPhoto(selectedImageUri!!, description)
                    showDialog = false
                    description = ""
                }) {
                    Text("Spremi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Odustani")
                }
            }
        )
    }
}
