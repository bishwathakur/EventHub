package com.example.eventhub


import android.os.Bundle
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChattingActivity : AppCompatActivity() {


 private lateinit var ref: DatabaseReference
 private lateinit var dbRef : DatabaseReference
 private lateinit var sendBtn: ImageButton
 private lateinit var messagebox: EditText
 private lateinit var auth: FirebaseAuth
 var chatList = ArrayList<Chat>()
 private lateinit var chatRecyclerView: RecyclerView
 private lateinit var mAdapter: ChatAdapter


 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_chatting)

  chatRecyclerView = findViewById(R.id.conversation_rv)
  chatRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
  chatRecyclerView.setHasFixedSize(true)

  chatList = arrayListOf<Chat>()

  mAdapter =  ChatAdapter(applicationContext,chatList)
  chatRecyclerView.adapter = mAdapter


  dbRef = FirebaseDatabase.getInstance().getReference("Messages")



  auth = FirebaseAuth.getInstance()

  sendBtn = findViewById(R.id.send_msg_btn)
  messagebox = findViewById(R.id.message_edittext)

  ref = FirebaseDatabase.getInstance().getReference("Users")

  val user = intent.getParcelableExtra<User?>("user")
  if (user != null) {
   val username: TextView = findViewById(R.id.convo_username)
   val userpfp: ImageView = findViewById(R.id.convo_userpfp)
   val userid: TextView = findViewById(R.id.convo_userid)

   username.text = user.name
   userid.text = user.userid

   Glide.with(this)
    .load(user.pfp)
    .into(userpfp)
  }

  val uid = auth.currentUser!!.uid

  ref.child(uid).get().addOnSuccessListener { snapshot ->
    if (snapshot != null) {
     val senderID = snapshot.child("userid").value.toString()


    sendBtn.setOnClickListener {
     val message: String = messagebox.text.toString()

     if (message.isEmpty()) {
      Toast.makeText(applicationContext, "Enter the message", Toast.LENGTH_SHORT).show()
     } else {
      val receiverID = user?.userid
      if (receiverID != null) {
       sendMessage(senderID, receiverID, message)

      }
      readMessage(senderID , receiverID!!)
     }
    }
   }
  }
 }

 private fun sendMessage(senderID: String, receiverID: String, message: String){

  var ref = FirebaseDatabase.getInstance().getReference("Messages")

  var hashMap:HashMap<String,String> = HashMap()

  hashMap.put("senderID",senderID)
  hashMap.put("receiverID",receiverID)
  hashMap.put("message",message)

  ref.push().setValue(hashMap)


 }
 fun readMessage(senderID: String, receiverID: String){

  val reference = FirebaseDatabase.getInstance().getReference("Messages")

  reference.addValueEventListener(object : ValueEventListener {
   override fun onDataChange(snapshot: DataSnapshot) {

    for (dataSnapShot: DataSnapshot in snapshot.children) {
     val chat = dataSnapShot.getValue(Chat::class.java)

     if (chat!!.senderId.equals(senderID) && chat!!.receiverId.equals(receiverID) ||
      (chat!!.senderId.equals(receiverID) && chat!!.receiverId.equals(senderID))
     )
      chatList.add(chat)

    }

    val chatAdapter = ChatAdapter(this@ChattingActivity, chatList)
   }

   override fun onCancelled(error: DatabaseError) {
    TODO("Not yet implemented")
   }
  })
 }
}