package com.example.mypracticeapplication.viewmodel

import android.content.Context
import com.example.mypracticeapplication.model.VideoItem
import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val context: Context
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

    private data class VideoDerivedState(
        val email: String,
        val likeCounts: Map<String, Int>,
        val userLikes: Set<String>,
        val favourites: Set<String>
    )

    private fun observeUserState() {
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
                VideoDerivedState(email, likeCounts, userLikes, favourites.toSet())
            }.distinctUntilChanged().collect { derived ->
                _uiState.update { state ->
                    state.copy(
                        isLoggedIn = derived.email.isNotBlank(),
                        currentEmail = derived.email,
                        videos = state.videos.map { video ->
                            video.copy(
                                likeCount = derived.likeCounts[video.filename] ?: 0,
                                isLikedByUser = video.filename in derived.userLikes,
                                isFavourite = video.filename in derived.favourites
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
}
