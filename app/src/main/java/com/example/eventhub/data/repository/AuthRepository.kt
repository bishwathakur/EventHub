package com.example.eventhub.data.repository

import com.example.eventhub.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {
    
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        firebaseAuth.signOut()
    }
    
    suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
            database.reference.child("Users").child(uid).setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getUserProfile(uid: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference.child("Users").child(uid).addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("Users").child(uid).removeEventListener(listener)
        }
    }
    
    fun getCurrentUserProfile(): Flow<User?> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.reference.child("Users").child(uid).addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("Users").child(uid).removeEventListener(listener)
        }
    }
}