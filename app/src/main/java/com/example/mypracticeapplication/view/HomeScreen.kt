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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mypracticeapplication.view.components.VideoOverlay
import com.example.mypracticeapplication.view.components.VideoPlayerComposable
import com.example.mypracticeapplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (uiState.videos.isEmpty()) {
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

    val pagerState = rememberPagerState(pageCount = { uiState.videos.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val video = uiState.videos[page]
        val isCurrentPage = page == pagerState.currentPage

        val onLikeClick = remember(video.filename) { { viewModel.toggleLike(video.filename) } }
        val onFavouriteClick = remember(video.filename) { { viewModel.toggleFavourite(video.filename) } }
        val onShareClick = remember(video.filename) { { viewModel.shareVideo(video.filename) } }

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
                onLikeClick = onLikeClick,
                onFavouriteClick = onFavouriteClick,
                onShareClick = onShareClick
            )
        }
    }
}
