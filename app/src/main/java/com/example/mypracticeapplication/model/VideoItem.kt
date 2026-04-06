package com.example.mypracticeapplication.model

data class VideoItem(
    val filename: String,
    val creatorName: String,
    val likeCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isFavourite: Boolean = false
)
