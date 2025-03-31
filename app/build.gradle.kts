plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization")

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.bsvillarraga.spaceflightnews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bsvillarraga.spaceflightnews"
        minSdk = 24
        targetSdk = 34
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.xconverter.gson)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)

    //Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //glide
    implementation(libs.glide)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.byte.buddy)
    testImplementation(libs.byte.buddy.agent)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.androidx.espresso.core)
    ksp(libs.hilt.android.compiler)

    //Instrumented tests
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    //Unit test (JVM)
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.hilt.android.testing)

    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.androidx.room.testing)

    testImplementation(libs.turbine)

    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)

    kspTest(libs.hilt.android.compiler)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
