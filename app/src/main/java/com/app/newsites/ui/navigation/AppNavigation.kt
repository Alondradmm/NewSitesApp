package com.app.newsites.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.newsites.ui.screen.Perfil.EncuestaScreen
import com.app.newsites.ui.screen.Perfil.PerfilScreen
import com.app.newsites.ui.screen.home.HomeScreen
import com.app.newsites.ui.screen.login.LoginScreen
import com.app.newsites.ui.screen.login.LoginViewModel
import com.app.newsites.ui.screen.map.MapScreen
import com.app.newsites.ui.screen.register.RegisterScreen
import com.app.newsites.ui.screen.sites.AgregarSiteScreen
import com.app.newsites.ui.screen.sites.EditSite
import com.app.newsites.ui.screen.sites.SitesScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController, userViewModel: LoginViewModel = viewModel()) {
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
            HomeScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = viewModel())
        }
        composable("sites") {
            SitesScreen(navController = navController, viewModel = viewModel())
        }
        composable("agregar_site") {
            AgregarSiteScreen(navController)
        }
        composable("editSite/{siteId}") { backStackEntry ->
            val siteId = backStackEntry.arguments?.getString("siteId") ?: return@composable
            EditSite(siteId, navController)
        }
        composable("perfil") {
            PerfilScreen(navController = navController, viewModel = viewModel())
        }
        composable("encuesta") {
            EncuestaScreen(navController)
        }

        composable(
            route = "map/{email}",
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MapScreen(
                navController = navController,
                viewModel = viewModel(),
                emailUsuario = email
            )
        }

    }
}