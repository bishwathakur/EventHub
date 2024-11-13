package com.example.eventhub

import android.app.DatePickerDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.eventhub.databinding.ActivityAddeventBinding
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEventActivity : AppCompatActivity() {

    var imageUri: Uri? = null


    private lateinit var dialog: Dialog


    private lateinit var binding: ActivityAddeventBinding

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>


    private lateinit var storageRef: FirebaseStorage
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddeventBinding.inflate(layoutInflater)
        setContentView(binding.root)


        storageRef = FirebaseStorage.getInstance()


        setupActivityResultLaunchers()

        binding.insEventPhoto.setOnClickListener {
            imagePickDialog()
        }

        binding.eventDatebox.setOnClickListener {
            showDatePickerDialog()
        }

        binding.insEventdate.setOnClickListener {
            showDatePickerDialog()
        }





        dbRef = FirebaseDatabase.getInstance().getReference("Events")

        binding.addeventbtn.setOnClickListener {

//            val eventname = binding.insEventname.text.toString()
//            val eventdate = binding.insEventdate.text.toString()
//            val eventvenue = binding.insEventvenue.text.toString()
//
//
//            if (eventname.isEmpty()) binding.insEventname.error= "Required Field!!"
//            if (eventdate.isEmpty()) binding.insEventdate.error= "Required Field!!"
//            if (eventvenue.isEmpty()) binding.insEventvenue.error= "Required Field!!"

            if (!isFinishing && !isDestroyed) {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_wait)
                dialog.setCanceledOnTouchOutside(false)

                if (!isFinishing && !isDestroyed) {
                    dialog.show()
                }
            }
            saveEventData()
        }
    }


    private fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        binding.insEventdate.setText(formattedDate)
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)

        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            onDateSet(null, year, month, dayOfMonth)
        }, year, month, date)

        datePickerDialog.show()
    }

    private fun setupActivityResultLaunchers() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    imageUri = it
                    binding.insEventPhoto.setImageURI(imageUri)
                }
            }
    }

    private fun imagePickDialog() {
        val options = arrayOf("Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick image From")
            .setCancelable(false)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> imagePickGallery()
                }
            }
            .create().show()
    }

    private fun imagePickGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveEventData() {


        // Getting values
        val eventname = binding.insEventname.text.toString()
        val eventdate = binding.insEventdate.text.toString()
        val eventvenue = binding.insEventvenue.text.toString()


        if (eventname.isEmpty()) binding.insEventname.error= "Required Field!!"
        if (eventdate.isEmpty()) binding.insEventdate.error= "Required Field!!"
        if (eventvenue.isEmpty()) binding.insEventvenue.error= "Required Field!!"

        // Checking if an image is selected
        if (imageUri != null) {
            // Uploading image to Firebase Storage
            uploadImageToStorage(eventname, eventdate, eventvenue)
        } else {
            // If no image is selected, you can handle this case accordingly
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToStorage(eventname: String, eventdate: String, eventvenue: String) {
        // Ensure that imageUri is not null before proceeding
        if (imageUri != null) {
            // Creating a reference to the storage location
            val storageReference =
                storageRef.reference.child("Eventpic/${eventname}_image.jpg")

            // Uploading the image to Firebase Storage
            val uploadTask = storageReference.putFile(imageUri!!)

            // Adding a listener to track the upload task
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image upload successful, get the download URL
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    // Save event data along with the image URL to the Realtime Database
                    saveEventDataToDatabase(eventname, eventdate, eventvenue, uri.toString())
                }.addOnFailureListener { e ->
                    // Handle failure to get download URL
                    Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                // Handle failure to upload image
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case when imageUri is null
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveEventDataToDatabase(
        eventname: String,
        eventdate: String,
        eventvenue: String,
        eventpicUrl: String
    ) {
        // Creating a reference to the Events node in the Realtime Database
        val eventsReference = FirebaseDatabase.getInstance().getReference("Events")

        // Creating a unique key for the event
        val eventKey = eventsReference.push().key

        // Assuming you have Firebase Authentication set up
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the user is logged in
        currentUser?.let { user ->
            // Assuming you have the necessary user details stored in Firebase Realtime Database
            // Fetch the user details from the database using user.uid
            val userReference = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)

                    // Check if user data is not null
                    userData?.let { user ->
                        // Now you can use the fetched user details to create a new post
                        val newPost = Post(
                            eventKey ?: "",
                            eventname,
                            eventdate,
                            eventvenue,
                            eventpicUrl,
                            user.userid.toString(),
                            user.name.toString(),
                            user.email.toString(),
                            user.pfp.toString()
                        )

                        // Save newPost to the database or perform any other operation
                        eventsReference.child(eventKey ?: "").setValue(newPost)
                            .addOnSuccessListener {
                                // Event data saved successfully
                                Toast.makeText(
                                    this@AddEventActivity,
                                    "Event added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                                finish()
                            }.addOnFailureListener {
                                // Handle failure to save event data
                                Toast.makeText(
                                    this@AddEventActivity,
                                    "Failed to save event data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}

