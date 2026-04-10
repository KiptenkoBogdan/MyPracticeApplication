package com.example.mypracticeapplication.model

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object HomeScreen : Route(route = "homeScreen")

    object BookmarkScreen : Route(route = "bookMarkScreen")

    object ProfileScreen : Route(route = "profileScreen")

    object LoginScreen : Route(route = "loginScreen")

    object NewsNavigation : Route(route = "newsNavigation")

    object NewsNavigatorScreen : Route(route = "newsNavigator")

    object SavedVideoPlayerScreen : Route(
        route = "savedVideoPlayer/{filename}",
        arguments = listOf(navArgument("filename") { type = NavType.StringType })
    ) {
        fun createRoute(filename: String): String =
            "savedVideoPlayer/${Uri.encode(filename)}"
    }
}
