package com.example.eventhub.data.repository

import android.net.Uri
import com.example.eventhub.data.model.Comment
import com.example.eventhub.data.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    
    fun getAllEvents(): Flow<List<Event>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    eventSnapshot.getValue(Event::class.java)?.let { event ->
                        events.add(event)
                    }
                }
                trySend(events)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference.child("Events").addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("Events").removeEventListener(listener)
        }
    }
    
    fun getUserEvents(userEmail: String): Flow<List<Event>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    eventSnapshot.getValue(Event::class.java)?.let { event ->
                        if (event.userEmail == userEmail) {
                            events.add(event)
                        }
                    }
                }
                trySend(events)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference.child("Events").addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("Events").removeEventListener(listener)
        }
    }
    
    suspend fun createEvent(event: Event, imageUri: Uri?): Result<String> {
        return try {
            val eventKey = database.reference.child("Events").push().key
                ?: throw Exception("Failed to generate event key")
            
            val imageUrl = if (imageUri != null) {
                uploadEventImage(eventKey, imageUri)
            } else ""
            
            val eventWithImage = event.copy(
                eventKey = eventKey,
                eventpicUrl = imageUrl
            )
            
            database.reference.child("Events").child(eventKey).setValue(eventWithImage).await()
            Result.success(eventKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteEvent(eventKey: String): Result<Unit> {
        return try {
            database.reference.child("Events").child(eventKey).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likeEvent(eventKey: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val likesRef = database.reference.child("EventDetails").child("Likes").child(eventKey)
            val eventRef = database.reference.child("Events").child(eventKey)
            
            val snapshot = likesRef.child(uid).get().await()
            if (snapshot.exists()) {
                // Unlike
                likesRef.child(uid).removeValue().await()
                val eventSnapshot = eventRef.get().await()
                val currentLikes = eventSnapshot.child("postLikes").getValue(Int::class.java) ?: 0
                eventRef.child("postLikes").setValue(maxOf(0, currentLikes - 1)).await()
            } else {
                // Like
                likesRef.child(uid).setValue(true).await()
                val eventSnapshot = eventRef.get().await()
                val currentLikes = eventSnapshot.child("postLikes").getValue(Int::class.java) ?: 0
                eventRef.child("postLikes").setValue(currentLikes + 1).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerForEvent(eventKey: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val registrationsRef = database.reference.child("EventDetails").child("Registrations").child(eventKey)
            val eventRef = database.reference.child("Events").child(eventKey)
            
            val snapshot = registrationsRef.child(uid).get().await()
            if (snapshot.exists()) {
                // Unregister
                registrationsRef.child(uid).removeValue().await()
                val eventSnapshot = eventRef.get().await()
                val currentRegistrations = eventSnapshot.child("postRegistrations").getValue(Int::class.java) ?: 0
                eventRef.child("postRegistrations").setValue(maxOf(0, currentRegistrations - 1)).await()
            } else {
                // Register
                registrationsRef.child(uid).setValue(true).await()
                val eventSnapshot = eventRef.get().await()
                val currentRegistrations = eventSnapshot.child("postRegistrations").getValue(Int::class.java) ?: 0
                eventRef.child("postRegistrations").setValue(currentRegistrations + 1).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getEventComments(eventKey: String): Flow<List<Comment>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<Comment>()
                for (commentSnapshot in snapshot.children) {
                    commentSnapshot.getValue(Comment::class.java)?.let { comment ->
                        comments.add(comment)
                    }
                }
                comments.sortBy { it.timestamp }
                trySend(comments)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference.child("Events").child(eventKey).child("Comments")
            .addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("Events").child(eventKey).child("Comments")
                .removeEventListener(listener)
        }
    }
    
    suspend fun addComment(eventKey: String, comment: Comment): Result<Unit> {
        return try {
            val commentId = database.reference.child("Events").child(eventKey)
                .child("Comments").push().key ?: throw Exception("Failed to generate comment ID")
            
            val commentWithId = comment.copy(commentId = commentId)
            database.reference.child("Events").child(eventKey)
                .child("Comments").child(commentId).setValue(commentWithId).await()
            
            // Update comment count
            val eventRef = database.reference.child("Events").child(eventKey)
            val eventSnapshot = eventRef.get().await()
            val currentComments = eventSnapshot.child("postComments").getValue(Int::class.java) ?: 0
            eventRef.child("postComments").setValue(currentComments + 1).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteComment(eventKey: String, commentId: String): Result<Unit> {
        return try {
            database.reference.child("Events").child(eventKey)
                .child("Comments").child(commentId).removeValue().await()
            
            // Update comment count
            val eventRef = database.reference.child("Events").child(eventKey)
            val eventSnapshot = eventRef.get().await()
            val currentComments = eventSnapshot.child("postComments").getValue(Int::class.java) ?: 0
            eventRef.child("postComments").setValue(maxOf(0, currentComments - 1)).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun uploadEventImage(eventKey: String, imageUri: Uri): String {
        val imageRef = storage.reference.child("Eventpic/${eventKey}_image.jpg")
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }
}