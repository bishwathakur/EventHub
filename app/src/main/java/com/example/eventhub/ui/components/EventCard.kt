package com.example.eventhub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventhub.data.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: Event,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onEventClick: () -> Unit,
    showDeleteOption: Boolean = false,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        onClick = onEventClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Event Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = event.userImage,
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = event.eventdate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                if (showDeleteOption && onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Event",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Event Title
            Text(
                text = event.eventname,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Event Venue
            Text(
                text = event.eventvenue,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            // Event Image
            if (event.eventpicUrl.isNotBlank()) {
                AsyncImage(
                    model = event.eventpicUrl,
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Event Description
            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${event.postLikes} likes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${event.postComments} comments",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${event.postRegistrations} registered",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Like")
                }
                
                TextButton(
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = "Comment",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comment")
                }
                
                TextButton(
                    onClick = { /* TODO: Share */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
            
            // Register Button
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Register",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register for Event")
            }
        }
    }
}