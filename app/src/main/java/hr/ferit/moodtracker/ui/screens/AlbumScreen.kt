package hr.ferit.moodtracker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import hr.ferit.moodtracker.viewmodel.MoodViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(viewModel: MoodViewModel = viewModel()) {
    val photos by viewModel.happyPhotos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    
    var fullScreenImageUri by remember { mutableStateOf<String?>(null) }
    val sdf = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
    
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            showDialog = true
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            selectedImageUri = tempPhotoUri
            showDialog = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Moj Album Sreće", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") }, 
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galerija")
                }
                
                Button(
                    onClick = { 
                        val file = File(context.cacheDir, "temp_photo_${UUID.randomUUID()}.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        tempPhotoUri = uri
                        
                        // Eksplicitno davanje dozvole za kameru
                        val intent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
                        intent.addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.AddAPhoto, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kamera")
                }
            }
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(photos) { photo ->
                    PhotoCard(photo, sdf) { fullScreenImageUri = photo.imageUrl }
                }
            }
        }
    }

    if (fullScreenImageUri != null) {
        FullScreenImageDialog(fullScreenImageUri) { fullScreenImageUri = null }
    }

    if (showDialog && selectedImageUri != null) {
        AddPhotoDialog(
            description = description,
            onDescriptionChange = { description = it },
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.uploadHappyPhoto(selectedImageUri!!, description)
                showDialog = false
                description = ""
            }
        )
    }
}

@Composable
fun PhotoCard(photo: hr.ferit.moodtracker.data.HappyPhoto, sdf: SimpleDateFormat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = null,
                modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                if (photo.description.isNotEmpty()) {
                    Text(text = photo.description, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                Text(
                    text = sdf.format(photo.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(uri: String?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxSize(),
        text = {
            Box(modifier = Modifier.fillMaxSize().clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        },
        containerColor = Color.Black.copy(alpha = 0.9f)
    )
}

@Composable
fun AddPhotoDialog(description: String, onDescriptionChange: (String) -> Unit, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova uspomena") },
        text = {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Što te usrećilo?") },
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Spremi") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Odustani") } }
    )
}
