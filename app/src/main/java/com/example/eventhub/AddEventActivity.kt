package com.example.eventhub

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.ActivityAddeventBinding
import com.example.eventhub.models.EventDetails

import com.example.eventhub.adapter.AdapterEventPost
import com.example.eventhub.models.Event
import com.example.eventhub.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar

class AddEventActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{

    private lateinit var binding: ActivityAddeventBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var uri: Uri
    private lateinit var Eventpic: ImageView
    private lateinit var AddEvent: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var EventAdapter: AdapterEventPost
    private lateinit var repository: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAddeventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Eventpic = binding.insEventPhoto
        AddEvent = binding.addeventbtn
        EventAdapter = AdapterEventPost(this, repository, Glide.with(this), FirebaseAuth.getInstance(), ArrayList<EventDetails>())
        // Make sure to pass an empty list of EventDetails

        storageRef = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.insEventdate.setOnClickListener {
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
            uploadEvent()
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

        binding.insEventdate.setText(formattedDate)
    }

    private fun uploadEvent() {
        val userId = auth.currentUser?.uid

        storageRef.getReference("eventphoto").child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { imageUrl ->
                        val event = Event(
                            eventname = binding.insEventname.text.toString(),
                            eventdate = binding.insEventdate.text.toString(),
                            eventvenue = binding.insEventvenue.text.toString(),
                            eventbyuser = binding.insEventByuser.text.toString()
                            // ... Other properties
                        )

                        EventAdapter.addData(event)
                        userId?.let { uid ->
                            FirebaseDatabase.getInstance().getReference("Events").child(uid)
                                .push().setValue(event)
                                .addOnSuccessListener {
                                    EventAdapter.addData(event) // Update the adapter with the new event
                                    Toast.makeText(this, "Event Published", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to publish event", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
            }

    }

    fun openEventDetailsActivity(event: EventDetails) {
        // Implement code to open EventDetailsActivity
        // You can use Intent to start the activity
        val intent = Intent(this, EventDetailsActivity::class.java)
        // Pass event data to the EventDetailsActivity
        intent.putExtra("eventDetails", event)
        startActivity(intent)
    }


}
