package com.example.socialmediaapp.adapter

import android.content.Context
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.R
import com.example.eventhub.models.Event
import com.example.eventhub.models.EventDetails
import com.example.eventhub.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.*

class AdapterPost(
    private val context: Context,
    private val repository: Repository,
    private val glide: RequestManager,
    private val auth: FirebaseAuth,
    private val  binding: ItemEventsBinding
) : RecyclerView.Adapter<AdapterPost.EventViewHolder>() {

    inner class EventViewHolder(val binding: ItemEventsBinding) : RecyclerView.ViewHolder(binding.root)

    var posts: List<EventDetails> = ArrayList()
    var myUid: String? = null

    init {
        myUid = auth.currentUser?.uid
    }

    fun setList(posts: List<EventDetails>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(ItemEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.apply {
            //user data
            eventName.text = post.userName
            glide.load(post.userImage).into(postUserPicture)
            //post data
            //get time from timestamp
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = if (post.postTime.isNotEmpty()) post.postTime.toLong() else 0

            val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
            eventTime.text = time
            postLikesTV.text = post.postLikes.toString()
            postCommentTV.text = post.postComments.toString()

            // ... (continue adapting the code based on AdapterEventPost)
            // ...

            val onItemClickListenerForLike: ((EventDetails) -> Unit)? = null
            eventLikeBtn.setOnClickListener {
                onItemClickListenerForLike?.let { it(post) }
                notifyDataSetChanged()
            }

            setLikes(holder, post.postId)

            val onItemClickListenerForGoingtoOwner: ((EventDetails) -> Unit)? = null
            postUserPicture.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }


            eventName.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }
            val onItemClickListener: ((EventDetails) -> Unit)? = null
            eventCommentBtn.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }
        }
    }

    private fun setLikes(holder1: EventViewHolder, postKey: String) {
        repository.refDatabase.child("Likes").addValueEventListener(object : ValueEventListener {
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
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}
