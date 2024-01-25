package com.example.eventhub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.NewMessageActivity
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
    private val eveRef: DatabaseReference,
    private val isProfileFragment: Boolean // Add a flag for identification
    //    private val listener: EventClickListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    var onItemClick: ((Post) -> Unit)? = null
    var onItemLongClick: ((Post) -> Unit)? = null
    var onShareClick: ((Post) -> Unit)? = null

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postUserPicture: ImageView = itemView.findViewById(R.id.post_userPicture)
        val postImage: ImageView = itemView.findViewById(R.id.post_Image)
        val postEventName: TextView = itemView.findViewById(R.id.post_event_name)
        val postEventVenue: TextView = itemView.findViewById(R.id.post_event_venue)
        val postEventDate: TextView = itemView.findViewById(R.id.post_event_date)
        val postEventByUser: TextView = itemView.findViewById(R.id.post_event_byuser)

        val postLikesTV: TextView = itemView.findViewById(R.id.post_LikesTV)
        val postCommentTV: TextView = itemView.findViewById(R.id.post_CommentTV)

        val postLikeBtn: Button = itemView.findViewById(R.id.post_like_btn)
        val postCommentBtn: Button = itemView.findViewById(R.id.post_comment_btn)
        val postShareBtn: Button = itemView.findViewById(R.id.post_share_btn)
        val postRegisterBtn: Button = itemView.findViewById(R.id.post_register_btn)

        val card : CardView = itemView.findViewById(R.id.cardevent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)

        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eveList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentEvent = eveList[position]

        holder.apply {
            postEventName.text = currentEvent.eventname
            postEventDate.text = currentEvent.eventdate
            postEventVenue.text = currentEvent.eventvenue
            postEventByUser.text = currentEvent.userId

            Glide.with(itemView)
                .load(currentEvent.userImage)
                .into(postUserPicture)

            Glide.with(itemView)
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

            //Comment count updater
            updateCommentCount(currentEvent)

            postRegisterBtn.setOnClickListener {
                handleRegisterButtonClick(currentEvent)
            }

            // Comment inflate
            postCommentBtn.setOnClickListener {
                onItemClick?.invoke(currentEvent)
            }

            postShareBtn.setOnClickListener{
                onShareClick?.invoke(currentEvent)
            }

            postImage.setOnClickListener {
                onItemClick?.invoke(currentEvent)
            }

            card.setOnClickListener{
                onItemClick?.invoke(currentEvent)
            }



            card.setOnLongClickListener {
                if (isProfileFragment) {
                    onItemLongClick?.invoke(currentEvent)
                    true
                } else {
                    false
                }
            }
            postImage.setOnLongClickListener {
                if (isProfileFragment) {
                    onItemLongClick?.invoke(currentEvent)
                    true
                } else {
                    false
                }
            }

        }
    }

    private fun updateLikeButton(post: Post, likeButton: TextView) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_not, 0, 0, 0)

        evedetRef.child(Constants.LIKES).child(postKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(myUid!!)) {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like,
                            0,
                            0,
                            0
                        )
                        likeButton.text = "Liked"
                    } else {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_not,
                            0,
                            0,
                            0
                        )
                        likeButton.text = "Like"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    private fun updateRegisterButton(post: Post, registerButton: TextView) {
        val postKey = post.eventKey
        val myUid = auth.currentUser?.uid

        registerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_add, 0, 0, 0)

        evedetRef.child(Constants.REGISTER).child(postKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(myUid!!)) {
                        registerButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.baseline_check_circle_24,
                            0,
                            0,
                            0
                        )
                        registerButton.text = "REGISTERED"
                    } else {
                        registerButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.baseline_add,
                            0,
                            0,
                            0
                        )
                        registerButton.text = "REGISTER"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
    }

    private fun updateCommentCount(post: Post) {
        val postKey = post.eventKey
        var postComments = post.postComments

        eveRef.child(postKey).child("Comments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Correctly fetch the comment count from the database
                    val nodeCount = snapshot.childrenCount

                    // Update the postComments variable with the correct count
                    postComments = nodeCount.toInt().coerceAtLeast(0)

                    // Update the database with the new comment count
                    eveRef.child(postKey).child("postComments").setValue(postComments)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun handleLikeButtonClick(post: Post) {
        var postLikes = post.postLikes
        val postKey = post.eventKey
        val myId = auth.currentUser?.uid

        if (postKey.isNotEmpty()) {
            evedetRef.child(Constants.LIKES).child(postKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(myId!!)) {
                            // Already liked, so remove like
                            val databaseReference = eveRef.child(postKey)
                            val LikesCounter = "postLikes"
                            var updatedLikes = --postLikes
                            updatedLikes = updatedLikes.coerceAtLeast(0)
                            databaseReference.child(LikesCounter).setValue(updatedLikes)
                            evedetRef.child(Constants.LIKES).child(postKey).child(myId)
                                .removeValue()
                        } else {
                            // Not liked, like it
                            val databaseReference = eveRef.child(postKey)
                            val LikesCounter = "postLikes"
                            val updatedLikes = ++postLikes
                            databaseReference.child(LikesCounter).setValue(updatedLikes)
                            evedetRef.child(Constants.LIKES).child(postKey).child(myId)
                                .setValue(true)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    private fun handleRegisterButtonClick(post: Post) {
        var postRegistrations = post.postRegistrations
        val postKey = post.eventKey
        val myId = auth.currentUser?.uid

        if (postKey.isNotEmpty()) {
            evedetRef.child(Constants.REGISTER).child(postKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(myId!!)) {
                            // Already registered, so remove registration
                            val databaseReference = eveRef.child(postKey)
                            val RegistrationsCounter = "postRegistrations"
                            var updatedRegistrations = --postRegistrations
                            updatedRegistrations = updatedRegistrations.coerceAtLeast(0)
                            databaseReference.child(RegistrationsCounter)
                                .setValue(updatedRegistrations)
                            evedetRef.child(Constants.REGISTER).child(postKey).child(myId)
                                .removeValue()
                        } else {
                            // Not registered, register in it
                            val databaseReference = eveRef.child(postKey)
                            val RegistrationsCounter = "postRegistrations"
                            val updatedRegistrations = ++postRegistrations
                            databaseReference.child(RegistrationsCounter)
                                .setValue(updatedRegistrations)
                            evedetRef.child(Constants.REGISTER).child(postKey).child(myId)
                                .setValue(true)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle onCancelled if needed
                    }
                })
        }
    }
}
