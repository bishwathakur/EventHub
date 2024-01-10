package com.example.eventhub.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.example.eventhub.utils.Constants
import com.example.eventhub.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import java.io.File
import javax.inject.Inject

class Repository@Inject constructor(
    var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth,
    private var context: Context


//    @Volatile
//    private var INSTANCE: Repository? = null
//
//    fun getInstance(): Repository {
//        return INSTANCE ?: synchronized(this) {
//            val instance = Repository()
//            INSTANCE = instance
//            instance
//        }
//    }

//    fun loadEvents(eventList: MutableLiveData<List<Event>>) {
//        refDatabase.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                try {
//                    val _eventList: List<Event> = snapshot.children.map { dataSnapshot ->
//                        dataSnapshot.getValue(Event::class.java)!!
//                    }
//                    eventList.postValue(_eventList)
//                } catch (e: Exception) {
//                    // Handle exception
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle onCancelled
//            }
//        })
//    }
//}

){
    private val postLiveData = MutableLiveData<Resource<Boolean>>()

    suspend fun uploadPost(post: Post): MutableLiveData<Resource<Boolean>> {
        val timeStamp = System.currentTimeMillis().toString()
        val filePathAndName = "Posts/Post_$timeStamp"
        post.postTime = timeStamp
        post.postId = timeStamp

        val storageReference = refStorage.child(filePathAndName)

// Assuming postAttachment is a valid file path
        val file = Uri.fromFile(File(post.postAttachment))

        storageReference.putFile(file)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result.toString()
                if (uriTask.isSuccessful) {
                    post.postAttachment = downloadUri
                    val ref = refDatabase.child(Constants.POSTS)
                    ref.child(timeStamp).setValue(post).addOnSuccessListener {
                        postLiveData.value = Resource.success(true)
                        Toast.makeText(context, "Post Published", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        postLiveData.value = Resource.error(e.message.toString(), false)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
                postLiveData.value = Resource.error(it.message.toString(), false)
            }

        return postLiveData

    }



    private val uid = auth.currentUser?.uid.toString()


    private val currentUserLiveData=MutableLiveData<Resource<User>>()
    suspend fun getCurrentUserData(): MutableLiveData<Resource<User>> {
        refDatabase.child(Constants.USERS)
            .child(uid)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                currentUserLiveData.value= Resource.success(user)
            }.addOnFailureListener {
                currentUserLiveData.value= Resource.error(it.message.toString(),null)
            }
        return currentUserLiveData
    }




    //Likes repo


//    suspend fun setLike(post: EventDetails){
//
//
//        var postLikes: Int = post.postLikes
//        var mProcessLike = true
//        //get id of the post clicked
//        val postId: String = post.postId
//
//        if (postId.isNotEmpty()){
//            val myId= auth.currentUser?.uid
//            refDatabase.child(Constants.LIKES).addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (mProcessLike) { //already liked ,so remove  like
//                        mProcessLike = if (dataSnapshot.child(postId).hasChild(myId!!)) {
//
//                            refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( -- postLikes))
//                            refDatabase.child(Constants.LIKES).child(postId).child(myId).removeValue()
//                            false
//                        } else { //not liked , liked it
//                            refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( ++ postLikes ))
//                            refDatabase.child(Constants.LIKES).child(postId).child(myId).setValue("Liked")
//                            false
//                        }
//                    }
//                }
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(context, "Error from likeRepo"+databaseError.message, Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//
//    }




    //Comment repo


//    var mProcessComment = true
//    suspend fun postComment(post:EventDetails,comment: Comment){
//        //put this data in DB :
//        refDatabase.child(Constants.POSTS).child(post.postId)
//            .child(Constants.COMMENTS).child(comment.timeStamp)
//            .setValue(comment).addOnSuccessListener { // added
//                Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
//                mProcessComment = true
//                updateCommentCount(postId = post.postId)
//            }.addOnFailureListener { e -> //failed
//                Toast.makeText(context, "" + e.message, Toast.LENGTH_LONG).show()
//            }
//    }


    //Comment count update repo


//
//    private fun updateCommentCount(postId:String) {
//        //whenever user adds comments increase the comments counts as we did for like count
//        val ref = refDatabase.child(Constants.POSTS).child(postId)
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (mProcessComment) {
//                    val comments = ( dataSnapshot.child("postComments").value ?: "0").toString()
//                    val newCommentVal = comments.toInt() + 1
//                    ref.child("postComments").setValue(newCommentVal)
//                    mProcessComment = false
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })
//    }





//    private val uid = auth.currentUser?.uid.toString()
//
//    private val currentUserLiveData=MutableLiveData<Resources<User>>()
//    suspend fun getCurrentUserData(): MutableLiveData<Resources<User>> {
//        refDatabase.child(Constants.USERS)
//            .child(uid)
//            .get().addOnSuccessListener { snapShot->
//                val user=snapShot.getValue(User::class.java)
//                currentUserLiveData.value= Resources.success(user)
//            }.addOnFailureListener {
//                currentUserLiveData.value= Resources.error(it.message.toString(),null)
//            }
//        return currentUserLiveData
//    }


    //show Posts in home
    private val postsLiveData=MutableLiveData<Resource<List<Post>>>()
    suspend   fun getPosts(): MutableLiveData<Resource<List<Post>>> {
        postsLiveData.value = Resource.loading(null)

        try {
            var postList: ArrayList<Post> = ArrayList()
            refDatabase.child(Constants.POSTS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList.clear()
                        snapshot.children.forEach { child ->
                            val post = child.getValue<Post>()
                            postList.add(post!!)
                        }
                        postsLiveData.value = Resource.success(postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        postsLiveData.value = Resource.error(error.message, null)

                    }
                })
        }catch (e:Exception) {
            postsLiveData.value = Resource.error(e.message.toString(), null)
        }


        return postsLiveData

    }






}
