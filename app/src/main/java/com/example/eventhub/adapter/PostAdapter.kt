package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.google.firebase.database.core.Context

class PostAdapter(
    private val eveList: ArrayList<Post>,
    private val refDatabase: DatabaseReference,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(
        val binding: ItemEventsBinding,

    ) :
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

            Glide.with(root.context)
                .load(currentEvent.userImage)
                .into(postUserPicture)

            Glide.with(root.context)
                .load(currentEvent.eventpicUrl)
                .into(postImage)


            postLikesTV.text = "${currentEvent.postLikes} likes"
            postCommentTV.text = "${currentEvent.postComments} comments"

            updateLikeButton(currentEvent, eventLikeBtn)
            eventLikeBtn.setOnClickListener {
                handleLikeButtonClick(currentEvent)
            }
        }
    }

    private fun updateLikeButton(post: Post, likeButton: TextView) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        refDatabase.child(Constants.LIKES).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(myUid!!)) {

                    likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
                    likeButton.text = "Liked"
                } else {

                    likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_not, 0, 0, 0)
                    likeButton.text = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun handleLikeButtonClick(post: Post) {
        var postLikes = post.postLikes
        val postKey = post.eventKey
        val myId = auth.currentUser?.uid

        if (postKey.isNotEmpty()) {
            refDatabase.child(Constants.LIKES).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(myId!!)) {
                        // Already liked, so remove like
                        refDatabase.child(Constants.POSTS).child(postKey).child(Constants.POSTLIKES)
                            .setValue(--postLikes)
                        refDatabase.child(Constants.LIKES).child(postKey).child(myId).removeValue()
                    } else {
                        // Not liked, like it
                        refDatabase.child(Constants.POSTS).child(postKey).child(Constants.POSTLIKES)
                            .setValue(++postLikes)
                        refDatabase.child(Constants.LIKES).child(postKey).child(myId).setValue(true)
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
}
