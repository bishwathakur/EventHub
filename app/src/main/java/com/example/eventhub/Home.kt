package com.example.eventhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.adapter.AdapterEventPost
import com.example.eventhub.databinding.FragmentHomeBinding
import com.example.eventhub.models.EventViewModel
import com.example.eventhub.models.Post
import com.example.eventhub.utils.Status
import com.google.firebase.auth.FirebaseAuth


class Home : Fragment() {


    private lateinit var auth : FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: AdapterEventPost
    private val viewModel by viewModels<EventViewModel>()
    private var postList: ArrayList<Post> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeAdapter = AdapterEventPost(requireActivity(),  postList)
        recyclerViewSetUp()
        viewModel.getPosts()


        viewModel.postsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.homeProgressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.homeProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    homeAdapter.setList(postList)
                }
                Status.ERROR -> {
                    binding.homeProgressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_postDetailsFragment,
                bundle
            )
        }
        homeAdapter.setOnItemClickListenerForGoingtoOwner { post ->
            val bundle = Bundle().apply {
                putSerializable("post", post)
            }

            val action =
                if (post.userId == auth.currentUser?.uid) {
                    R.id.action_homeFragment_to_profileFragment
                } else {
                    R.id.action_homeFragment_to_postOwnerFragment
                }

            findNavController().navigate(action, bundle)
        }

//        homeAdapter.setonItemClickListenerForLike {
//            viewModel.setLike(it)
//        }




    }

    private fun recyclerViewSetUp() {
        val linearLayout = LinearLayoutManager(activity)
//        linearLayout.stackFromEnd = true
//        linearLayout.reverseLayout = true
        binding.homeRec.layoutManager = linearLayout
        homeAdapter.setList(postList)
        binding.homeRec.adapter = homeAdapter
    }

}
