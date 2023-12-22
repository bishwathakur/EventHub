package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.AddprofileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Addprofile : AppCompatActivity() {

    private lateinit var binding: AddprofileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = AddprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        dataBaseReference = FirebaseAuth.getInstance().getReference("Users")



























    }
}