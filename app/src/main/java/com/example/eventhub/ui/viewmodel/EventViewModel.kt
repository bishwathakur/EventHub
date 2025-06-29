package com.example.eventhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventhub.data.model.Event
import com.example.eventhub.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()
    
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            eventRepository.getAllEvents().collect { events ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    events = events,
                    error = null
                )
            }
        }
    }
    
    fun loadUserEvents(userEmail: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            eventRepository.getUserEvents(userEmail).collect { events ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    events = events,
                    error = null
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
    
    fun deleteEvent(eventKey: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventKey)
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete event"
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EventUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null
)