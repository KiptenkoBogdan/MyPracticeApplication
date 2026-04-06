package com.example.mypracticeapplication.viewmodel

import android.content.Context
import com.example.mypracticeapplication.model.VideoItem
import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoggedIn: Boolean = false,
    val currentEmail: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        loadVideos()
        observeUserState()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            val filenames = context.assets.list("videos")?.toList() ?: emptyList()
            val videos = filenames.shuffled().map { filename ->
                VideoItem(
                    filename = filename,
                    creatorName = extractCreatorName(filename)
                )
            }
            _uiState.update { it.copy(videos = videos) }
            dataStoreManager.initLikeCounts(filenames)
        }
    }

    private fun observeUserState() {
        viewModelScope.launch {
            dataStoreManager.getLoggedInEmail().collect { email ->
                _uiState.update { it.copy(isLoggedIn = email.isNotBlank(), currentEmail = email) }
            }
        }

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
            ) { _, likeCounts, userLikes, favourites ->
                Triple(likeCounts, userLikes, favourites.toSet())
            }.collect { (likeCounts, userLikes, favourites) ->
                _uiState.update { state ->
                    state.copy(
                        videos = state.videos.map { video ->
                            video.copy(
                                likeCount = likeCounts[video.filename] ?: 0,
                                isLikedByUser = video.filename in userLikes,
                                isFavourite = video.filename in favourites
                            )
                        }
                    )
                }
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

    class Factory(
        private val dataStoreManager: DataStoreManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(dataStoreManager, context) as T
        }
    }
}
