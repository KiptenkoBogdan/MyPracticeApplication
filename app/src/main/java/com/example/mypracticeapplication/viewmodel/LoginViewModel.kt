package com.example.mypracticeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypracticeapplication.model.UserDetails
import com.example.mypracticeapplication.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    fun login(email: String, password: String, onDone: () -> Unit) {
        viewModelScope.launch {
            dataStoreManager.saveToDataStore(
                UserDetails(email = email, password = password)
            )
            onDone()
        }
    }
}
