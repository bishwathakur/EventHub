package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.R
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.models.Post
import com.example.eventhub.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class PostAdapter(
    private val eveList: ArrayList<Post>,
    private val refDatabase: DatabaseReference, // Pass DatabaseReference
    private val auth: FirebaseAuth // Pass FirebaseAuth

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemEventsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            ItemEventsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentEvent = eveList[position]

        holder.binding.apply {


            postEventName.text = currentEvent.eventname
            postEventVenue.text = currentEvent.eventvenue
            postEventDate.text = currentEvent.eventdate
            postEventByuser.text = currentEvent.userId

            // Initialize Glide and load the image into the ImageView
            Glide.with(root.context)
                .load(currentEvent.userImage)
                .into(postUserPicture)

            Glide.with(root.context)
                .load(currentEvent.eventpicUrl)
                .into(postImage)

            postLikesTV.text = currentEvent.postLikes.toString()
            postCommentTV.text = currentEvent.postComments.toString()




            eventLikeBtn.setOnClickListener {
                setLikes(post = Post())
                notifyDataSetChanged()
            }
        }
    }

    private fun setLikes(post: Post) {
        var postLikes: Int = post.postLikes
        var mProcessLike = true
        //get id of the post clicked
        val postKey: String = post.eventKey

        if (postKey.isNotEmpty()) {
            val myId = auth.currentUser?.uid
            refDatabase.child(Constants.LIKES).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (mProcessLike) { //already liked, so remove like
                        mProcessLike = if (dataSnapshot.child(postKey).hasChild(myId!!)) {
                            refDatabase.child(Constants.POSTS).child(postKey).child(Constants.POSTLIKES).setValue(--postLikes)
                            refDatabase.child(Constants.LIKES).child(postKey).child(myId).removeValue()
                            false
                        } else { //not liked, liked it
                            refDatabase.child(Constants.POSTS).child(postKey).child(Constants.POSTLIKES).setValue(++postLikes)
                            refDatabase.child(Constants.LIKES).child(postKey).child(myId).setValue("Liked")
                            false
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }


    override fun getItemCount(): Int {
        return eveList.size
    }
    private fun setLikes(holder1: PostViewHolder, post: Post) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        refDatabase.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                holder1.binding.apply {
                    if (dataSnapshot.child(postKey).hasChild(myUid!!)) {
                        //user has liked for this post
                        eventLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like, 0, 0, 0)
                        eventLikeBtn.text = "Liked"
                    } else {
                        //user has not liked for this post
                        eventLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_not, 0, 0, 0)
                        eventLikeBtn.text = "Like"
                    }

                    eventLikeBtn.setOnClickListener {
                        // Call your suspend function to handle like/unlike logic
                        setLikes(post)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Your suspend function for like/unlike logic




}