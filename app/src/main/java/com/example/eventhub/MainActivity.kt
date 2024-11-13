package com.example.eventhub

import Home
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import com.example.eventhub.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // view binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigator.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navigateToFragment(Home())
                R.id.profile -> navigateToFragment(Profile())
                R.id.chats -> navigateToFragment(Chats())
            }
            true
        }
        // Opens Home fragment by default
        navigateToFragment(Home())
    }

    private fun navigateToFragment(fragment: Fragment) {
        // Check if the fragment is already displayed
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment)
        if (currentFragment?.javaClass == fragment.javaClass) {
            // Do nothing if it's already the current fragment
            return
        }
        // Replace fragment if it's not currently displayed
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.commit()
    }
    }