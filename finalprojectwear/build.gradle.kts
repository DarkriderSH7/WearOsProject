plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 30 // Wear OS 3 requires minSdk 30
        targetSdk = 33
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

    buildFeatures {
        viewBinding = true;
    }

    // Uncomment if using Kotlin
    // kotlinOptions {
    //     jvmTarget = "11"
    // }
}

dependencies {
    implementation(libs.play.services.wearable.v1800)
    implementation(libs.appcompat.v161)
    implementation(libs.material.v190)
    implementation(libs.activity.v172)
    implementation(libs.constraintlayout.v214)
    implementation(libs.wear)
    // Add any other dependencies required for your Wear OS app
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    }
}