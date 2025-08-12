/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.app.newsites.wear.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.newsites.wear.presentation.screen.map.MapScreen
import com.app.newsites.wear.presentation.screen.profile.ProfileScreen
import com.app.newsites.wear.presentation.screen.siteDetail.SiteDetailScreen
import com.app.newsites.wear.presentation.screen.sync.WelcomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        val intent = Intent(this, WearDataLifecycleService::class.java)
        startService(intent)

        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("welcome") }
    var selectedSiteId by remember { mutableStateOf("") }

    when (currentScreen) {
        "map" -> MapScreen(
            onNavigate = { currentScreen = "profile" },
            onSiteNavigate = { siteId ->
                selectedSiteId = siteId
                currentScreen = "siteDetail"
            }
        )
        "profile" -> ProfileScreen(onBack = { currentScreen = "map" })
        "welcome" -> WelcomeScreen(onNavigate = { currentScreen = "map" })
        "siteDetail" -> SiteDetailScreen(
            siteId = selectedSiteId,
            onBack = { currentScreen = "map" }
        )
    }
}



