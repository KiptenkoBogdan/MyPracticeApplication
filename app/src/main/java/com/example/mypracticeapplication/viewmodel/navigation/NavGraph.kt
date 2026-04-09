package com.example.mypracticeapplication.view

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