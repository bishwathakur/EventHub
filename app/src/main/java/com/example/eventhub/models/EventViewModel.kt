package com.example.eventhub.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventhub.utils.Resource
import com.example.eventhub.models.User
import com.example.eventhub.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventViewModel @Inject constructor(
    private val repository: Repository,
    val auth: FirebaseAuth
) : ViewModel() {

    //Seein which user posted and getting details

    var currentUserLiveData=MutableLiveData<Resource<User>>()
    fun getDataForCurrentUser() {
        viewModelScope.launch {
            currentUserLiveData= repository.getCurrentUserData()
        }
    }

    var postLiveData = MutableLiveData<Resource<Boolean>>()
    fun uploadPost(post: Post){
        viewModelScope.launch {
            postLiveData=repository.uploadPost(post)
        }
    }


//    var postLiveData = MutableLiveData<Resource<Boolean>>()
//    fun uploadPost(post: Post){
//        viewModelScope.launch {
//            postLiveData=repository.uploadPost(post)
//        }
//    }


    //Update getting posts

    var postsLiveData=MutableLiveData<Resource<List<Post>>>()
    fun getPosts(){
        viewModelScope.launch {
            postsLiveData = repository.getPosts()
        }
    }


}