package com.example.eventhub

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.ActivityAddeventBinding
import com.example.eventhub.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar

class AddEventActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddeventBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var uri: Uri
    private lateinit var Eventpic: ImageView
    private lateinit var AddEvent: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddeventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Eventpic = binding.insEventPhoto
        AddEvent = binding.addeventbtn

        storageRef = FirebaseStorage.getInstance()

        auth = FirebaseAuth.getInstance()

        binding.insEventDate.setOnClickListener {
            showDatePickerDialog()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                Eventpic.setImageURI(it)
                uri = it!!
            })

        Eventpic.setOnClickListener {
            galleryImage.launch("image/*")
        }

        binding.addeventbtn.setOnClickListener {
            uploadEventDetails()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = dateFormat.format(selectedDate.time)

        binding.insEventDate.setText(formattedDate)
    }

    private fun uploadEventDetails() {
        val userId = auth.currentUser?.uid

        storageRef.getReference("eventphoto").child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { imageUrl ->
                        val mapImage = mapOf("eventphoto" to imageUrl.toString())
                        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        userId?.let { uid ->
                            databaseReference.child(uid).setValue(mapImage)
                        }

                        // Load the image using Glide
                        Glide.with(this)
                            .load(imageUrl)
                            .into(Eventpic)
                    }
            }

        val eventimage = uri.toString()
        val eventname = binding.insEventName.text.toString()
        val eventdate = binding.insEventDate.text.toString()
        val eventvenue = binding.insEventVenue.text.toString()
        val eventbyuser = binding.insEventByuser.text.toString()

        // Continue with processing the event details as needed...
    }
}

