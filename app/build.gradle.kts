plugins {
    id("com.android.application")

}

android {
    namespace = "com.example.finalproject"
    compileSdk = 33 // Use the latest stable version

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 21 // Lower the minSdk to support more devices
        targetSdk = 33 // Use a stable target SDK version
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


}
dependencies {
    // Explicitly define dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Wearable dependencies for Data Layer API communication
    implementation("com.google.android.gms:play-services-wearable:18.0.0")

    // Additional dependencies
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10")
}
