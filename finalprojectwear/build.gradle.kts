plugins {
    id("com.android.application")
    // id("kotlin-android") // Uncomment if you're using Kotlin
}

android {
    namespace = "com.example.finalprojectwear"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.finalprojectwear"
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

    // Uncomment if using Kotlin
    // kotlinOptions {
    //     jvmTarget = "11"
    // }
}

dependencies {
    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Add any other dependencies required for your Wear OS app
}

