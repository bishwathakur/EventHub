package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.eventhub.databinding.FragmentHomeBinding


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for addButton
        binding.addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddEventActivity::class.java)
            intent.putExtra("eventName", "Event Name")
            intent.putExtra("eventDate", "Event Date")
            intent.putExtra("eventVenue", "Event Venue")
            intent.putExtra("eventByUser", "Event By User")
            startActivity(intent)
        }

    }

}
