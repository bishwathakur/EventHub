package com.example.eventhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityAddeventBinding  // Import the correct binding class
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddeventBinding  // Correct type for binding
    private lateinit var storageRef: DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddeventBinding.inflate(layoutInflater)  // Use the correct inflate method
        setContentView(binding.root)  // Set content view to the root of the binding

        // Your other initialization code goes here
    }
}
