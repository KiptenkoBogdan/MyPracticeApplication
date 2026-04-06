package com.example.mypracticeapplication.model

data class UserDetails(
    val email: String,
    val password: String,
    val displayName: String = "",
    val profilePictureUri: String = ""
)
