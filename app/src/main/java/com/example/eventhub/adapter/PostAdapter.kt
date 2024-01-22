package com.example.eventhub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.PostDetailsActivity
import com.example.eventhub.R
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.models.Post
import com.example.eventhub.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class PostAdapter(
    private val eveList: ArrayList<Post>,
    private val auth: FirebaseAuth,
    private val evedetRef: DatabaseReference,
    private val eveRef: DatabaseReference
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    inner class PostViewHolder(val binding: ItemEventsBinding) : RecyclerView.ViewHolder(binding.root)

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

            // Like button binders
            updateLikeButton(currentEvent, postLikeBtn)

            postLikeBtn.setOnClickListener {
                handleLikeButtonClick(currentEvent)
            }

            // Register button binders
            updateRegisterButton(currentEvent, postRegisterBtn)

            postRegisterBtn.setOnClickListener {
                handleRegisterButtonClick(currentEvent)
            }

            // Comment inflater
            postCommentBtn.setOnClickListener {
                val context = root.context
                val intent = Intent(context, PostDetailsActivity::class.java)
                intent.putExtra("postLikes", currentEvent.postLikes)
                intent.putExtra("postRegistrations", currentEvent.postRegistrations)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            // Set click listener on the root view
            holder.itemView.setOnClickListener {
                // Check if mListener is not null before calling its method
                mListener?.onItemClick(position)
            }
        }
    }

    private fun updateLikeButton(post: Post, likeButton: TextView) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_not, 0, 0, 0)

        evedetRef.child(Constants.LIKES).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
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
                // Handle onCancelled if needed
            }
        })
    }

    private fun updateRegisterButton(post: Post, registerButton: TextView) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        registerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_add, 0, 0, 0)

        evedetRef.child(Constants.REGISTER).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(myUid!!)) {
                    registerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0)
                    registerButton.text = "REGISTERED"
                } else {
                    registerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_add, 0, 0, 0)
                    registerButton.text = "REGISTER"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun handleLikeButtonClick(post: Post) {
        var postLikes = post.postLikes
        val postKey = post.eventKey
        val myId = auth.currentUser?.uid

        if (postKey.isNotEmpty()) {
            evedetRef.child(Constants.LIKES).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(myId!!)) {
                        // Already liked, so remove like
                        val databaseReference = eveRef.child(postKey)
                        val LikesCounter = "postLikes"
                        var updatedLikes = --postLikes
                        updatedLikes = updatedLikes.coerceAtLeast(0)
                        databaseReference.child(LikesCounter).setValue(updatedLikes)
                        evedetRef.child(Constants.LIKES).child(postKey).child(myId).removeValue()
                    } else {
                        // Not liked, like it
                        val databaseReference = eveRef.child(postKey)
                        val LikesCounter = "postLikes"
                        val updatedLikes = ++postLikes
                        databaseReference.child(LikesCounter).setValue(updatedLikes)
                        evedetRef.child(Constants.LIKES).child(postKey).child(myId).setValue(true)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
        }
    }

    private fun handleRegisterButtonClick(post: Post) {
        var postRegistrations = post.postRegistrations
        val postKey = post.eventKey
        val myId = auth.currentUser?.uid

        if (postKey.isNotEmpty()) {
            evedetRef.child(Constants.REGISTER).child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(myId!!)) {
                        // Already registered, so remove registration
                        val databaseReference = eveRef.child(postKey)
                        val RegistrationsCounter = "postRegistrations"
                        var updatedRegistrations = --postRegistrations
                        updatedRegistrations = updatedRegistrations.coerceAtLeast(0)
                        databaseReference.child(RegistrationsCounter).setValue(updatedRegistrations)
                        evedetRef.child(Constants.REGISTER).child(postKey).child(myId).removeValue()
                    } else {
                        // Not registered, register in it
                        val databaseReference = eveRef.child(postKey)
                        val RegistrationsCounter = "postRegistrations"
                        val updatedRegistrations = ++postRegistrations
                        databaseReference.child(RegistrationsCounter).setValue(updatedRegistrations)
                        evedetRef.child(Constants.REGISTER).child(postKey).child(myId).setValue(true)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return eveList.size
    }
}
