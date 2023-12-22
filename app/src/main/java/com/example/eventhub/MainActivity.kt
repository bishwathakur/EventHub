package com.example.eventhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.eventhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding.backButton.setOnClickListener {
            // Handle back button press
            onBackPressedDispatcher.onBackPressed()
        }

        // Create an onBackPressedCallback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom back button logic goes here
                // For example, you can finish the activity
                finish()
            }
        }

        // Add the callback to the onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }
}