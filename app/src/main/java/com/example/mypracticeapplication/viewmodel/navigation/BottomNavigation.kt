package com.example.mypracticeapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.net.Uri
import com.example.mypracticeapplication.utils.DataStoreManager
import com.example.mypracticeapplication.view.BookmarkScreen
import com.example.mypracticeapplication.view.HomeScreen
import com.example.mypracticeapplication.view.LoginScreen
import com.example.mypracticeapplication.view.ProfileScreen
import com.example.mypracticeapplication.view.SavedVideoPlayerScreen
import com.example.mypracticeapplication.model.Route
import com.example.mypracticeapplication.view.BottomNavBar
import com.example.mypracticeapplication.view.BottomNavItem
import com.example.mypracticeapplication.viewmodel.BookmarkViewModel
import com.example.mypracticeapplication.viewmodel.HomeViewModel
import com.example.mypracticeapplication.viewmodel.ProfileViewModel
import com.example.mypracticeapplication.viewmodel.SavedVideoPlayerViewModel

@Composable
fun BottomNavigator(dataStoreManager: DataStoreManager) {
    val context = LocalContext.current.applicationContext
    val bottomNavigationItems = remember {
        listOf(
            BottomNavItem(icon = Icons.Outlined.Home, text = "Home"),
            BottomNavItem(icon = Icons.Outlined.Bookmark, text = "Saved"),
            BottomNavItem(icon = Icons.Outlined.AccountCircle, text = "Profile")
        )
    }
    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value
    val isLoggedIn by dataStoreManager.isLoggedIn().collectAsState(initial = false)

    var selectedItem by rememberSaveable { mutableStateOf(0) }
    selectedItem = when (backStackState?.destination?.route) {
        Route.HomeScreen.route -> 0
        Route.BookmarkScreen.route -> 1
        Route.ProfileScreen.route -> 2
        Route.LoginScreen.route -> 2
        else -> 0
    }

    val isBottomBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Route.HomeScreen.route ||
                backStackState?.destination?.route == Route.BookmarkScreen.route ||
                backStackState?.destination?.route == Route.ProfileScreen.route ||
                backStackState?.destination?.route == Route.LoginScreen.route
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (isBottomBarVisible) {
            BottomNavBar(
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
                            route = if (isLoggedIn) Route.BookmarkScreen.route else Route.LoginScreen.route
                        )

                        2 -> navigateToTab(
                            navController = navController,
                            route = if (isLoggedIn) Route.ProfileScreen.route else Route.LoginScreen.route
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
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(dataStoreManager, context)
                )
                HomeScreen(viewModel = homeViewModel)
            }
            composable(route = Route.BookmarkScreen.route) {
                val bookmarkViewModel: BookmarkViewModel = viewModel(
                    factory = BookmarkViewModel.Factory(dataStoreManager)
                )
                BookmarkScreen(
                    viewModel = bookmarkViewModel,
                    onVideoClick = { filename ->
                        navController.navigate(Route.SavedVideoPlayerScreen.createRoute(filename))
                    }
                )
            }
            composable(
                route = Route.SavedVideoPlayerScreen.route,
                arguments = Route.SavedVideoPlayerScreen.arguments
            ) { backStackEntry ->
                val rawFilename = backStackEntry.arguments?.getString("filename")
                val filename = rawFilename?.let { Uri.decode(it) }
                if (filename == null) {
                    navController.popBackStack()
                    return@composable
                }
                val savedVideoPlayerViewModel: SavedVideoPlayerViewModel = viewModel(
                    factory = SavedVideoPlayerViewModel.Factory(dataStoreManager)
                )
                SavedVideoPlayerScreen(
                    startingFilename = filename,
                    viewModel = savedVideoPlayerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(route = Route.LoginScreen.route) {
                LoginScreen(
                    onLoginClicked = { _, _ ->
                        navController.navigate(Route.HomeScreen.route) {
                            popUpTo(Route.LoginScreen.route) { inclusive = true }
                        }
                    },
                    dataStoreManager = dataStoreManager
                )
            }
            composable(route = Route.ProfileScreen.route) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(dataStoreManager)
                )
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Route.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    viewModel = profileViewModel
                )
            }
        }
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
