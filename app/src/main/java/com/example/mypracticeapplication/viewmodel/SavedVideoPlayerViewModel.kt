package com.example.mypracticeapplication.viewmodel

import com.example.mypracticeapplication.model.VideoItem
import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class SavedVideoPlayerUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoggedIn: Boolean = false,
    val currentEmail: String = "",
    val isLoaded: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SavedVideoPlayerViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedVideoPlayerUiState())
    val uiState: StateFlow<SavedVideoPlayerUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        observeState()
    }

    private fun observeState() {
        viewModelScope.launch {
            val emailFlow = dataStoreManager.getLoggedInEmail()

            combine(
                emailFlow,
                dataStoreManager.getLikeCounts(),
                emailFlow.flatMapLatest { email ->
                    if (email.isNotBlank()) dataStoreManager.getUserLikes(email) else flowOf(emptySet())
                },
                emailFlow.flatMapLatest { email ->
                    if (email.isNotBlank()) dataStoreManager.getFavourites(email) else flowOf(emptyList())
                }
            ) { email, likeCounts, userLikes, favourites ->
                SavedVideoPlayerUiState(
                    videos = favourites.map { filename ->
                        VideoItem(
                            filename = filename,
                            creatorName = extractCreatorName(filename),
                            likeCount = likeCounts[filename] ?: 0,
                            isLikedByUser = filename in userLikes,
                            isFavourite = true
                        )
                    },
                    isLoggedIn = email.isNotBlank(),
                    currentEmail = email,
                    isLoaded = true
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleLike(filename: String) {
        viewModelScope.launch {
            val state = _uiState.value
            if (!state.isLoggedIn) {
                _toastMessage.emit("Log in to like videos")
                return@launch
            }
            dataStoreManager.toggleLike(state.currentEmail, filename)
        }
    }

    fun toggleFavourite(filename: String) {
        viewModelScope.launch {
            val state = _uiState.value
            if (!state.isLoggedIn) {
                _toastMessage.emit("Log in to save videos")
                return@launch
            }
            dataStoreManager.toggleFavourite(state.currentEmail, filename)
        }
    }

    fun shareVideo(filename: String) {
        viewModelScope.launch {
            _toastMessage.emit("Link copied!")
        }
    }

    private fun extractCreatorName(filename: String): String {
        val name = filename.substringBefore("_vid").substringBefore("_")
        return name.replaceFirstChar { it.uppercase() }
    }
}
