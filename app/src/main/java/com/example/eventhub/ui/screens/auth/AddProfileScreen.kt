package com.example.eventhub.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.eventhub.R
import com.example.eventhub.data.model.User
import com.example.eventhub.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var name by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }
    
    // Handle navigation when profile is saved
    LaunchedEffect(uiState.isAuthenticated, uiState.needsProfile) {
        if (uiState.isAuthenticated && !uiState.needsProfile) {
            onNavigateToMain()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Complete Your Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Tell us about yourself",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Add Photo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User ID Field
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("Username") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Location Field
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Phone Field
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Save Profile Button
        Button(
            onClick = {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val user = User(
                        uid = currentUser.uid,
                        name = name,
                        email = currentUser.email ?: "",
                        userid = userId,
                        userplace = location,
                        userphone = phone,
                        pfp = "" // TODO: Upload image and get URL
                    )
                    viewModel.saveUserProfile(user)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && name.isNotBlank() && userId.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Complete Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Error Message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}