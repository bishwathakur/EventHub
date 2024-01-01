package com.example.eventhub.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.R
import com.example.eventhub.databinding.ActivityAddeventBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding : AddEventActivity
    private lateinit var storageRef : DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddeventBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_addevent)



    }
}