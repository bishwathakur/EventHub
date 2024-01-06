package com.example.eventhub.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.eventhub.AddEventActivity
import com.example.eventhub.R
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.models.Event
import com.example.eventhub.models.EventDetails
import com.example.eventhub.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class AdapterEventPost(
    private val context: AddEventActivity,
    private val repository: Repository,
    private val glide: RequestManager,
    private val auth: FirebaseAuth,
    private var events: MutableList<EventDetails>
) : RecyclerView.Adapter<AdapterEventPost.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventsBinding) : RecyclerView.ViewHolder(binding.root)

    var myUid: String? = null

    init {
        myUid = auth.currentUser?.uid
    }

    fun setList(posts: List<EventDetails>) {
        this.events = posts.toMutableList()
        notifyDataSetChanged()
    }

    fun addData(newEvent: Event) {
        events.add(newEvent)
        notifyItemInserted(events.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.binding.apply {
            //user data
            glide.load(event.userImage).into(postUserPicture)

//            postEventName.setOnClickListener {
//                onItemClickListenerForGoingtoOwner?.let { it(event) }
//            }

            //event data
//            postEventName.text = event.eventname
//            postEventDate.text = event.eventdate
//            postEventVenue.text = event.eventvenue
//            postEventByuser.text = event.eventbyuser

            //get time from timestamp
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = if (event.postTime.isNotEmpty()) event.postTime.toLong() else 0

            val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
            postLikesTV.text = event.postLikes.toString()
            postCommentTV.text = event.postComments.toString()

            // ... (continue adapting the code based on AdapterEventPost)
            // ...

            val onItemClickListenerForLike: ((EventDetails) -> Unit)? = null
            eventLikeBtn.setOnClickListener {
                onItemClickListenerForLike?.let { it(event) }
                notifyDataSetChanged()
            }

            setLikes(holder, event.postId)


            var onItemClickListenerForGoingtoOwner: ((EventDetails) -> Unit)? = null

            fun setOnItemClickListenerForGoingtoOwner(listener: (EventDetails) -> Unit) {
                onItemClickListenerForGoingtoOwner = listener
            }
            postUserPicture.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(event) }
            }

            postEventName.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(event) }
            }
            val onItemClickListener: ((EventDetails) -> Unit)? = null
            eventCommentBtn.setOnClickListener {
                onItemClickListener?.let { it(event) }
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(event) }
            }
        }
    }

    private fun setLikes(holder1: ViewHolder, postKey: String) {
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
