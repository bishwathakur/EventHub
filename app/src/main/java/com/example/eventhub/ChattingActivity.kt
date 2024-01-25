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
       messagebox.setText("")
       hideKeyboard(this)


      }
      readMessage(senderID , receiverID!!)
     }
    }
   }
  }
 }
 override fun onBackPressed() {
  super.onBackPressed()

  // Start the NewMessage activity when the back button is pressed
  startActivity(Intent(this, NewMessageActivity::class.java))
  finish() // Optional: Finish the current activity to remove it from the stack
 }
 private fun hideKeyboard(activity: Activity) {
  val view: View? = activity.currentFocus
  if (view != null) {
   val imm: InputMethodManager =
    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
   imm.hideSoftInputFromWindow(view.windowToken, 0)
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
 fun readMessage(senderID: String, receiverID: String) {
  val ref = FirebaseDatabase.getInstance().getReference("Messages")

  ref.addValueEventListener(object : ValueEventListener {
   override fun onDataChange(snapshot: DataSnapshot) {
    chatList.clear()

    for (dataSnapShot: DataSnapshot in snapshot.children) {
     val chat = dataSnapShot.getValue(Chat::class.java)

     if (chat!!.senderID.equals(senderID) && chat!!.receiverID.equals(receiverID) ||
      (chat!!.senderID.equals(receiverID) && chat!!.receiverID.equals(senderID))
     ) {
      chatList
      println(chatList)
      chatList.add(chat!!)
     }
    }

    mAdapter.notifyDataSetChanged()

    // Set up the adapter only once after loading all messages
    if (chatRecyclerView.adapter == null) {
     val chatAdapter = ChatAdapter(this@ChattingActivity, chatList)
     chatRecyclerView.adapter = chatAdapter
    }
   }

   override fun onCancelled(error: DatabaseError) {
    // Handle cancellation if needed
   }
  })
 }

}