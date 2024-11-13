package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.adapter.UserChatAdapter
import com.example.eventhub.models.Chat
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chats : Fragment(), UserChatAdapter.OnItemClickListener {

    private lateinit var allusersicon: View
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var messagesRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference
    private lateinit var chatList: ArrayList<User>
    private lateinit var mAdapter: UserChatAdapter
    private var currentUserUserID: String? = null
    private val uniqueUserIds = HashSet<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        chatRecyclerView = view.findViewById(R.id.chats_rv)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.setHasFixedSize(true)

        chatList = arrayListOf()

        // Initialize the adapter with the listener
        mAdapter = UserChatAdapter(requireContext(), chatList, this)
        chatRecyclerView.adapter = mAdapter

        messagesRef = FirebaseDatabase.getInstance().getReference("Messages")
        usersRef = FirebaseDatabase.getInstance().getReference("Users")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let { fetchUserID(it) }

        allusersicon = view.findViewById(R.id.allusersicon)
        allusersicon.setOnClickListener {
            val intent = Intent(context, NewMessageActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onItemClick(user: User) {
        // Handle the click event
        val intent = Intent(context, ChattingActivity::class.java).apply {
            putExtra("user", user) // Assuming 'user' is Parcelable
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // Use this flag
        }
        startActivity(intent)
    }


    private fun fetchUserID(uid: String) {
        loadingProgressBar.visibility = View.VISIBLE
        usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            currentUserUserID = snapshot.child("userid").value.toString()
            currentUserUserID?.let { getUniqueChatUsers(it) }
        }.addOnFailureListener {
            loadingProgressBar.visibility = View.GONE
        }
    }

    private fun getUniqueChatUsers(currentUserUserID: String) {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uniqueUserIds.clear()
                chatList.clear()

                if (snapshot.exists()) {
                    for (chatSnapshot in snapshot.children) {
                        val chat = chatSnapshot.getValue(Chat::class.java)
                        if (chat != null) {
                            if (chat.senderID == currentUserUserID) {
                                uniqueUserIds.add(chat.receiverID)
                            } else if (chat.receiverID == currentUserUserID) {
                                uniqueUserIds.add(chat.senderID)
                            }
                        }
                    }
                    fetchUserDetails()
                }
                loadingProgressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                loadingProgressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchUserDetails() {
        for (userId in uniqueUserIds) {
            usersRef.orderByChild("userid").equalTo(userId).limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            user?.let {
                                chatList.add(it)
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loadingProgressBar.visibility = View.GONE
                    }
                })
        }
    }
}
