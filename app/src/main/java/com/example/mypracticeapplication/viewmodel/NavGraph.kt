package com.example.mypracticeapplication.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.mypracticeapplication.BottomNavigator

//@Composable
//fun NavGraph(
//    startDestination: Unit
//) {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = startDestination) {
//        navigation(
//            route = Route.HomeScreen.route,
//            startDestination = Route.HomeScreen.route
//        ) {
//            composable(route = Route.HomeScreen.route) {
//                //val viewModel: OnBoardingViewModel = hiltViewModel()
//                //HomeScreen(onEvent = viewModel::onEvent)
//            }
//        }
//
//        navigation(
//            route = Route.NewsNavigation.route,
//            startDestination = Route.NewsNavigatorScreen.route
//        ) {
//            composable(route = Route.NewsNavigatorScreen.route){
//                BottomNavigator()
//            }
//        }
//    }
//}