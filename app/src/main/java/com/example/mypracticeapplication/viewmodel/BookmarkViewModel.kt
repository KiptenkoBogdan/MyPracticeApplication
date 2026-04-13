package com.example.mypracticeapplication.viewmodel

import com.example.mypracticeapplication.model.VideoItem
import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class BookmarkUiState(
    val displayName: String = "",
    val favouriteVideos: List<VideoItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        observeState()
    }

    private fun observeState() {
        viewModelScope.launch {
            val emailFlow = dataStoreManager.getLoggedInEmail()
            val displayNameFlow = dataStoreManager.getFromDataStore()
                .map { it.displayName }
                .distinctUntilChanged()

            combine(
                emailFlow,
                emailFlow.flatMapLatest { email ->
                    if (email.isNotBlank()) dataStoreManager.getFavourites(email) else flowOf(emptyList())
                },
                displayNameFlow
            ) { _, favourites, displayName ->
                BookmarkUiState(
                    displayName = displayName,
                    favouriteVideos = favourites.map { filename ->
                        VideoItem(
                            filename = filename,
                            creatorName = filename.substringBefore("_vid")
                                .substringBefore("_")
                                .replaceFirstChar { it.uppercase() }
                        )
                    }
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun removeFavourite(filename: String) {
        viewModelScope.launch {
            val email = dataStoreManager.getLoggedInEmail().first()
            if (email.isNotBlank()) {
                dataStoreManager.toggleFavourite(email, filename)
            }
        }
    }
}
