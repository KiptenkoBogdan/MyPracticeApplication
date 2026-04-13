package com.example.mypracticeapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val email: String,
    val password: String,
    val displayName: String = "",
    val profilePictureUri: String = ""
)
