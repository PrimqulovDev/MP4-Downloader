plugins {
    id("com.android.library")
//    id("androidx.navigation.safeargs")
    kotlin("android")
    id("dagger.hilt.android.plugin")

    kotlin("kapt")
    kotlin("plugin.serialization")

}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    api(project(mapOf("path" to ":data")))

    api("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")

    api("com.akexorcist:localization:1.2.10")

    api("androidx.fragment:fragment-ktx:1.4.0")

    /**
     * lifecycle
     */
    //noinspection LifecycleAnnotationProcessorWithJava8
    //@SuppressLint("LifecycleAnnotationProcessorWithJava8")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.4.0")
    api("androidx.lifecycle:lifecycle-extensions:2.2.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.0")

    api("com.karumi:dexter:6.2.2")


    /**
     * Okhttp3
     */
    api("com.squareup.okhttp3:okhttp:4.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.9.0")

    api("androidx.recyclerview:recyclerview:1.2.1")
    api("com.github.bumptech.glide:glide:4.11.0")

    api("com.karumi:dexter:6.2.2")

    /**
     * Cicerone
     */
    api("com.github.terrakok:cicerone:7.0")


    /**
     *  ExoPlayer
     */
    api("com.google.android.exoplayer:exoplayer:2.16.1")
    api("com.google.android.exoplayer:exoplayer-core:2.16.1")
    api("com.google.android.exoplayer:exoplayer-dash:2.16.1")
    api("com.google.android.exoplayer:exoplayer-ui:2.16.1")

    /**
     * Hilt
    */
    kapt("com.google.dagger:dagger-compiler:2.38")
    kapt("com.google.dagger:hilt-android-compiler:2.38")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("com.google.dagger:hilt-android:2.38")

    //PRDownloader - https://github.com/MindorksOpenSource/PRDownloader
    api("com.mindorks.android:prdownloader:0.6.0")


    /**
     *  Gson
     */
    api("com.google.code.gson:gson:2.8.6")

}