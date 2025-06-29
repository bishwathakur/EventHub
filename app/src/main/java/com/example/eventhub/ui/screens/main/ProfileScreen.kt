package com.example.eventhub.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.eventhub.ui.components.EventCard
import com.example.eventhub.ui.viewmodel.AuthViewModel
import com.example.eventhub.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onNavigateToEventDetails: (String) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val eventUiState by eventViewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(currentUser) {
        currentUser?.email?.let { email ->
            eventViewModel.loadUserEvents(email)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.signOut()
                            onSignOut()
                        }
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Profile Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        AsyncImage(
                            model = currentUser?.pfp,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Name
                        Text(
                            text = currentUser?.name ?: "Unknown User",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // User ID
                        Text(
                            text = "@${currentUser?.userid ?: "unknown"}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // User Details
                        currentUser?.let { user ->
                            ProfileDetailRow(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = user.email
                            )
                            
                            if (user.userplace.isNotBlank()) {
                                ProfileDetailRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = user.userplace
                                )
                            }
                            
                            if (user.userphone.isNotBlank()) {
                                ProfileDetailRow(
                                    icon = Icons.Default.Phone,
                                    label = "Phone",
                                    value = user.userphone
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                // My Events Section
                Text(
                    text = "My Events",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            when {
                eventUiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                eventUiState.events.isEmpty() -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No events created yet",
                                modifier = Modifier.padding(32.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                else -> {
                    items(eventUiState.events) { event ->
                        EventCard(
                            event = event,
                            onLikeClick = { eventViewModel.likeEvent(event.eventKey) },
                            onCommentClick = { onNavigateToEventDetails(event.eventKey) },
                            onRegisterClick = { eventViewModel.registerForEvent(event.eventKey) },
                            onEventClick = { onNavigateToEventDetails(event.eventKey) },
                            showDeleteOption = true,
                            onDeleteClick = { eventViewModel.deleteEvent(event.eventKey) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 14.sp
            )
        }
    }
}