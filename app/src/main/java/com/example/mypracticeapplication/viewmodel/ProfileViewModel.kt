package com.example.mypracticeapplication.viewmodel

import com.example.mypracticeapplication.utils.DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val profilePictureUri: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
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
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            dataStoreManager.saveDisplayName(name)
            val email = _uiState.value.email
            if (email.isNotBlank()) {
                dataStoreManager.updateAccount(email) { it.copy(displayName = name) }
            }
        }
    }

    fun updateProfilePicture(uri: String) {
        viewModelScope.launch {
            dataStoreManager.saveProfilePictureUri(uri)
            val email = _uiState.value.email
            if (email.isNotBlank()) {
                dataStoreManager.updateAccount(email) { it.copy(profilePictureUri = uri) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearDataStore()
        }
    }
}
