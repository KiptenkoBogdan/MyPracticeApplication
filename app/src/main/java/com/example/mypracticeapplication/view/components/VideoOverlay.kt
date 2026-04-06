package com.example.mypracticeapplication.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideoOverlay(
    creatorName: String,
    likeCount: Int,
    isLiked: Boolean,
    isFavourite: Boolean,
    onLikeClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        // Creator name at bottom-left
        Text(
            text = "@$creatorName",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
        )

        // Right side action buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onLikeClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = formatCount(likeCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Favourite/Save button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onFavouriteClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isFavourite) Color.Yellow else Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Share button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onShareClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
