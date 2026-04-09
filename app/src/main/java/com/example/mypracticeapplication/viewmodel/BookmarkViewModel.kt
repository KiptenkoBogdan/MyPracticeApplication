package com.example.mypracticeapplication.viewmodel

import com.example.mypracticeapplication.model.VideoItem
import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class BookmarkUiState(
    //val isLoggedIn: Boolean = false,
    val favouriteVideos: List<VideoItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModel(
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

            combine(
                emailFlow,
                emailFlow.flatMapLatest { email ->
                    if (email.isNotBlank()) dataStoreManager.getFavourites(email) else flowOf(emptyList())
                }
            ) { email, favourites ->
                BookmarkUiState(
                    //isLoggedIn = email.isNotBlank(),
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

    class Factory(
        private val dataStoreManager: DataStoreManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BookmarkViewModel(dataStoreManager) as T
        }
    }
}
