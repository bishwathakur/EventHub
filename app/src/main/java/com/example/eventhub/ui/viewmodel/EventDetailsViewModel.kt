package com.example.eventhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventhub.data.model.Comment
import com.example.eventhub.data.model.Event
import com.example.eventhub.data.repository.AuthRepository
import com.example.eventhub.data.repository.EventRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()
    
    fun loadEventDetails(eventKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // TODO: Implement getEventById in EventRepository
            // For now, we'll just set loading to false
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun loadComments(eventKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingComments = true)
            
            eventRepository.getEventComments(eventKey).collect { comments ->
                _uiState.value = _uiState.value.copy(
                    isLoadingComments = false,
                    comments = comments
                )
            }
        }
    }
    
    fun addComment(eventKey: String, commentText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingComment = true)
            
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                // Get user profile to get display name and image
                authRepository.getCurrentUserProfile().collect { userProfile ->
                    if (userProfile != null) {
                        val comment = Comment(
                            comment = commentText,
                            userId = userProfile.userid,
                            userImage = userProfile.pfp,
                            userName = userProfile.name,
                            uid = currentUser.uid
                        )
                        
                        eventRepository.addComment(eventKey, comment)
                            .onSuccess {
                                _uiState.value = _uiState.value.copy(isAddingComment = false)
                            }
                            .onFailure { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isAddingComment = false,
                                    error = exception.message ?: "Failed to add comment"
                                )
                            }
                    }
                }
            }
        }
    }
    
    fun deleteComment(eventKey: String, commentId: String) {
        viewModelScope.launch {
            eventRepository.deleteComment(eventKey, commentId)
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete comment"
                    )
                }
        }
    }
    
    fun likeEvent(eventKey: String) {
        viewModelScope.launch {
            eventRepository.likeEvent(eventKey)
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to like event"
                    )
                }
        }
    }
    
    fun registerForEvent(eventKey: String) {
        viewModelScope.launch {
            eventRepository.registerForEvent(eventKey)
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to register for event"
                    )
                }
        }
    }
    
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EventDetailsUiState(
    val isLoading: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isAddingComment: Boolean = false,
    val event: Event? = null,
    val comments: List<Comment> = emptyList(),
    val error: String? = null
)