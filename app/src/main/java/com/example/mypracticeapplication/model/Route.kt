package com.example.mypracticeapplication.model

import androidx.navigation.NamedNavArgument

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
}