plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Google Play Services dependencies
    implementation(libs.play.services.tasks)
    implementation(libs.play.services.wearable.v1800)

    // OkHttp (which may pull in Kotlin dependencies)
    implementation(libs.okhttp)

    // Other dependencies
    implementation(libs.appcompat.v161)
    implementation(libs.material.v190)
    implementation(libs.activity.v172)
    implementation(libs.constraintlayout.v214)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)

    // Additional dependencies
    implementation(libs.gson)


}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    }
}
