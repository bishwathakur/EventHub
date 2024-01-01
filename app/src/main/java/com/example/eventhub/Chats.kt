package com.example.eventhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventhub.databinding.FragmentChatsBinding
import com.example.eventhub.databinding.FragmentProfileBinding

class Chats : Fragment() {

    private lateinit var binding : FragmentChatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

}