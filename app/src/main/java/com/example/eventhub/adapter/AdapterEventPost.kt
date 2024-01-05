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
) : RecyclerView.Adapter<AdapterPost.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemEventsBinding) : RecyclerView.ViewHolder(binding.root)

    var posts: List<EventDetails> = ArrayList()
    var myUid: String? = null

    init {
        myUid = auth.currentUser?.uid
    }

    fun setList(posts: List<EventDetails>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(ItemEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.apply {
            //user data
            postUserName.text = post.userName
            glide.load(post.userImage).into(postUserPicture)
            //post data
            //get time from timestamp
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = if (post.postTime.isNotEmpty()) post.postTime.toLong() else 0

            val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
            postTimeIv.text = time
            postCaption.text = post.caption
            postLikesTV.text = post.postLikes.toString()
            postCommentTV.text = post.postComments.toString()
            postTextAnyone.text = post.postFans

            // ... (continue adapting the code based on AdapterEventPost)
            // ...

            postLikeBtn.setOnClickListener {
                onItemClickListenerForLike?.let { it(post) }
                notifyDataSetChanged()
            }

            setLikes(holder, post.postId)

            postUserPicture.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }
            postUserName.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }

            postCommentBtn.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }
        }
    }

    // ... (continue adapting the code based on AdapterEventPost)
    // ...

    // Add the missing functions for AdapterPost
    // ...

    // You can copy the functions setLikes and translate from AdapterEventPost
    // ...

}
