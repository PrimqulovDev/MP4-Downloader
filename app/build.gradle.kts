plugins {
    id("com.android.application")
    kotlin("android")
    id("dagger.hilt.android.plugin")




    kotlin("kapt")
    kotlin("plugin.serialization")

//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")


}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "app.downloader"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "$project.rootDir/tools/proguard-rules-debug.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

/*
    kapt {
        javacOptions {
            option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
        }
    }
*/

    kapt {
        correctErrorTypes = true
    }

    /*hilt {
        enableAggregatingTask = true
        enableExperimentalClasspathAggregation = true
    }*/


    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation(project(mapOf("path" to ":domain")))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("io.coil-kt:coil:1.4.0")

    /**
     * Hilt
     */
    kapt("com.google.dagger:dagger-compiler:2.38")
    kapt("com.google.dagger:hilt-android-compiler:2.38")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("com.google.dagger:hilt-android:2.38")
    annotationProcessor("com.google.dagger:hilt-compiler:2.40.5")

}