package com.example.eventhub.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.engine.Resource

import com.example.eventhub.models.User
import com.example.eventhub.models.Comment
import com.example.eventhub.models.EventDetails

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference


import com.example.eventhub.utils.Constants
import com.example.eventhub.utils.Resources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Repository(
    var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth,
    private var context: Context
) {

    private val postLiveData = MutableLiveData<com.example.eventhub.utils.Resources<Boolean>>()

    suspend fun uploadPost(post: EventDetails): MutableLiveData<Resources<Boolean>> {
        val timeStamp = System.currentTimeMillis().toString()
        val filePathAndName = "Posts/Post_$timeStamp"
        post.postTime = timeStamp
        post.postId = timeStamp

        val storageReference = refStorage.child(filePathAndName)
        storageReference.putFile(Uri.parse(post.postAttachment))
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result.toString()
                if (uriTask.isSuccessful) {
                    post.postAttachment = downloadUri
                    val ref = refDatabase.child(Constants.POSTS)
                    ref.child(timeStamp).setValue(post).addOnSuccessListener {
                        postLiveData.value = Resources.success(true)
                        Toast.makeText(context, "Post Published", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        postLiveData.value = Resources.error(e.message.toString(), false)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
            }

        return postLiveData
    }
    suspend fun setLike(post: EventDetails){


        var postLikes: Int = post.postLikes
        var mProcessLike = true
        //get id of the post clicked
        val postId: String = post.postId

        if (postId.isNotEmpty()){
            val myId= auth.currentUser?.uid
            refDatabase.child(Constants.LIKES).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (mProcessLike) { //already liked ,so remove  like
                        mProcessLike = if (dataSnapshot.child(postId).hasChild(myId!!)) {

                            refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( -- postLikes))
                            refDatabase.child(Constants.LIKES).child(postId).child(myId).removeValue()
                            false
                        } else { //not liked , liked it
                            refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( ++ postLikes ))
                            refDatabase.child(Constants.LIKES).child(postId).child(myId).setValue("Liked")
                            false
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error from likeRepo"+databaseError.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
    var mProcessComment = true
    suspend fun postComment(post:EventDetails,comment: Comment){
        //put this data in DB :
        refDatabase.child(Constants.POSTS).child(post.postId)
            .child(Constants.COMMENTS).child(comment.timeStamp)
            .setValue(comment).addOnSuccessListener { // added
                Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                mProcessComment = true
                updateCommentCount(postId = post.postId)
            }.addOnFailureListener { e -> //failed
                Toast.makeText(context, "" + e.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun updateCommentCount(postId:String) {
        //whenever user adds comments increase the comments counts as we did for like count
        val ref = refDatabase.child(Constants.POSTS).child(postId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mProcessComment) {
                    val comments = ( dataSnapshot.child("postComments").value ?: "0").toString()
                    val newCommentVal = comments.toInt() + 1
                    ref.child("postComments").setValue(newCommentVal)
                    mProcessComment = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private val uid = auth.currentUser?.uid.toString()

    private val currentUserLiveData=MutableLiveData<com.example.eventhub.utils.Resources<User>>()
    suspend fun getCurrentUserData(): MutableLiveData<com.example.eventhub.utils.Resources<User>> {
        refDatabase.child(Constants.USERS)
            .child(uid)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                currentUserLiveData.value= Resources.success(user)
            }.addOnFailureListener {
                currentUserLiveData.value= Resources.error(it.message.toString(),null)
            }
        return currentUserLiveData
    }






}
