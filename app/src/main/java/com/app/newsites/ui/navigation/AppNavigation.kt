package com.app.newsites.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.newsites.ui.screen.home.HomeScreen
import com.app.newsites.ui.screen.login.LoginScreen
import com.app.newsites.ui.screen.register.RegisterScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally { 5000 } },
        exitTransition = { slideOutHorizontally { -5000 }},
        popEnterTransition = { slideInHorizontally { -5000 }},
        popExitTransition = { slideOutHorizontally { 5000 }}) {
        composable("login") {
            LoginScreen(navController = navController, viewModel = viewModel())
        }
        composable("home") {
            HomeScreen()
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = viewModel())
        }
    }
}