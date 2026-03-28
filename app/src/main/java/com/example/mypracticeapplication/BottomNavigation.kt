package com.example.mypracticeapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mypracticeapplication.utils.DataStoreManager
import com.example.mypracticeapplication.view.BookmarkScreen
import com.example.mypracticeapplication.view.LoginScreen
import com.example.mypracticeapplication.view.ProfileScreen
import com.example.mypracticeapplication.view.Route


@Composable
fun BottomNavigator(dataStoreManager: DataStoreManager) {
    val bottomNavigationItems = remember {
        listOf(
            _root_ide_package_.com.example.kotlinpracticeapp.BottomNavItem(
                icon = Icons.Outlined.Home,
                text = "Home"
            ),
            _root_ide_package_.com.example.kotlinpracticeapp.BottomNavItem(
                icon = Icons.Outlined.Bookmark,
                text = "Bookmark"
            ),
            _root_ide_package_.com.example.kotlinpracticeapp.BottomNavItem(
                icon = Icons.Outlined.AccountCircle,
                text = "Profile"
            )
        )
    }
    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable{
        mutableStateOf(0)
    }
    selectedItem = when(backStackState?.destination?.route){
        Route.HomeScreen.route -> 0
        Route.BookmarkScreen.route -> 1
        Route.LoginScreen.route -> 2
        else -> 0
    }

    val isBottomBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Route.HomeScreen.route ||
                backStackState?.destination?.route == Route.BookmarkScreen.route ||
                backStackState?.destination?.route == Route.LoginScreen.route
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (isBottomBarVisible) {
            _root_ide_package_.com.example.kotlinpracticeapp.BottomNavBar(
                items = bottomNavigationItems,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    when (index) {
                        0 -> navigateToTab(
                            navController = navController,
                            route = Route.HomeScreen.route
                        )

                        1 -> navigateToTab(
                            navController = navController,
                            route = Route.BookmarkScreen.route
                        )

                        2 -> navigateToTab(
                            navController = navController,

                            route = Route.LoginScreen.route
                        )
                    }
                }
            )
        }
    }) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(route = Route.HomeScreen.route) {
                HomeScreen()
            }
            composable(route = Route.BookmarkScreen.route) {
                BookmarkScreen()
            }
            composable(route = Route.LoginScreen.route) {
                LoginScreen({ _, _ ->
                    navController.navigate(
                        route = Route.HomeScreen.route
                    )
                }, dataStoreManager)
            }
            composable(route = Route.ProfileScreen.route) {
                ProfileScreen({navController.navigate(route = Route.LoginScreen.route)},dataStoreManager)
            }
        }
    }
}

@Composable
fun OnBackClickStateSaver(navController: NavController) {
    BackHandler(true) {
        navigateToTab(
            navController = navController,
            route = Route.HomeScreen.route
        )
    }
}
private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screen_route ->
            popUpTo(screen_route) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}