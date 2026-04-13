package com.example.mypracticeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypracticeapplication.model.UserDetails
import com.example.mypracticeapplication.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    fun signUp(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val ok = dataStoreManager.registerAccount(
                UserDetails(
                    email = email,
                    password = password,
                    displayName = username
                )
            )
            if (ok) onSuccess() else onError("An account with this email already exists")
        }
    }
}
