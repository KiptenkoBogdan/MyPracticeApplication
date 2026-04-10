package com.example.mypracticeapplication.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mypracticeapplication.view.components.VideoOverlay
import com.example.mypracticeapplication.view.components.VideoPlayerComposable
import com.example.mypracticeapplication.viewmodel.SavedVideoPlayerViewModel

@Composable
fun SavedVideoPlayerScreen(
    startingFilename: String,
    viewModel: SavedVideoPlayerViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Pop back if the favourites list becomes empty after loading (e.g. last video unsaved).
    LaunchedEffect(uiState.isLoaded, uiState.videos.isEmpty()) {
        if (uiState.isLoaded && uiState.videos.isEmpty()) {
            onBack()
        }
    }

    if (!uiState.isLoaded || uiState.videos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading videos...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val initialPage = uiState.videos
        .indexOfFirst { it.filename == startingFilename }
        .coerceAtLeast(0)

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { uiState.videos.size }
    )

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1
    ) { page ->
        // Guard against transient out-of-bounds when the list shrinks.
        val video = uiState.videos.getOrNull(page) ?: return@VerticalPager
        val isCurrentPage = page == pagerState.currentPage

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoPlayerComposable(
                filename = video.filename,
                isPlaying = isCurrentPage
            )

            VideoOverlay(
                creatorName = video.creatorName,
                likeCount = video.likeCount,
                isLiked = video.isLikedByUser,
                isFavourite = video.isFavourite,
                onLikeClick = { viewModel.toggleLike(video.filename) },
                onFavouriteClick = { viewModel.toggleFavourite(video.filename) },
                onShareClick = { viewModel.shareVideo(video.filename) }
            )
        }
    }
}
