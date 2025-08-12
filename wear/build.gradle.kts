plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.app.newsites.wear"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.newsites"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom.v33150))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    
    implementation(libs.play.services.maps)
    implementation (libs.play.services.location)
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation(libs.androidx.material3)

    implementation (libs.androidx.material.icons.extended)

    implementation (libs.androidx.lifecycle.viewmodel.compose)

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.service)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // RELOJ
    implementation ("com.google.android.gms:play-services-wearable:19.0.0")
}

apply(plugin = "com.google.gms.google-services")
