package com.example.mypracticeapplication.viewmodel

import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val profilePictureUri: String = "",
    val isLoggedIn: Boolean = false
)

class ProfileViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            dataStoreManager.getFromDataStore().collect { user ->
                _uiState.value = ProfileUiState(
                    email = user.email,
                    displayName = user.displayName,
                    profilePictureUri = user.profilePictureUri,
                    isLoggedIn = user.email.isNotBlank()
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            dataStoreManager.saveDisplayName(name)
        }
    }

    fun updateProfilePicture(uri: String) {
        viewModelScope.launch {
            dataStoreManager.saveProfilePictureUri(uri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearDataStore()
        }
    }

    class Factory(
        private val dataStoreManager: DataStoreManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(dataStoreManager) as T
        }
    }
}
