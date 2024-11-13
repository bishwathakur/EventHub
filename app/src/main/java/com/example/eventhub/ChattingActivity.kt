package com.example.eventhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.adapter.ChatAdapter
import com.example.eventhub.models.Chat
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChattingActivity : AppCompatActivity() {

    private lateinit var messagesRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference
    private lateinit var sendBtn: ImageButton
    private lateinit var messagebox: EditText
    private lateinit var auth: FirebaseAuth
    private var chatList = ArrayList<Chat>()
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var mAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        // Initialize Firebase references
        messagesRef = FirebaseDatabase.getInstance().getReference("Messages")
        usersRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        // Initialize views
        chatRecyclerView = findViewById(R.id.conversation_rv)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.setHasFixedSize(true)

        // Initialize adapter
        mAdapter = ChatAdapter(this, chatList)
        chatRecyclerView.adapter = mAdapter

        // Initialize UI elements
        sendBtn = findViewById(R.id.send_msg_btn)
        messagebox = findViewById(R.id.message_edittext)

        // Retrieve user data from intent
        val user = intent.getParcelableExtra<User?>("user")
        user?.let {
            findViewById<TextView>(R.id.convo_username).text = it.name
            findViewById<TextView>(R.id.convo_userid).text = it.userid
            Glide.with(this).load(it.pfp).into(findViewById(R.id.convo_userpfp))
        }

        // Get current user ID from Firebase
        auth.currentUser?.uid?.let { uid ->
            usersRef.child(uid).get().addOnSuccessListener { snapshot ->
                val senderID = snapshot.child("userid").value.toString()
                setupSendButton(senderID, user)
                user?.userid?.let { readMessage(senderID, it) }
            }
        } ?: run {
            Toast.makeText(this@ChattingActivity, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupSendButton(senderID: String, user: User?) {
        sendBtn.setOnClickListener {
            val message = messagebox.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this@ChattingActivity, "Enter the message", Toast.LENGTH_SHORT).show()
            } else {
                val receiverID = user?.userid
                if (receiverID != null) {
                    sendMessage(senderID, receiverID, message)
                    messagebox.setText("")
                    hideKeyboard(this)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, NewMessageActivity::class.java))
        finish()
    }

    private fun hideKeyboard(activity: Activity) {
        val view: View? = activity.currentFocus
        view?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun sendMessage(senderID: String, receiverID: String, message: String) {
        val messageRef = messagesRef.push()
        val messageData = mapOf(
            "senderID" to senderID,
            "receiverID" to receiverID,
            "message" to message
        )
        messageRef.setValue(messageData)
    }

    private fun readMessage(senderID: String, receiverID: String) {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat != null && ((chat.senderID == senderID && chat.receiverID == receiverID) ||
                                (chat.senderID == receiverID && chat.receiverID == senderID))) {
                        chatList.add(chat)
                    }
                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChattingActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
