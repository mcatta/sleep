@file:Suppress("UnstableApiUsage")

/*
* Copyright 2021 Marco Cattaneo
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("com.google.gms.google-services")
    alias(libs.plugins.versions)
    alias(libs.plugins.sleep.jacoco)
}

android {
    compileSdk = Sdk.COMPILE_SDK_VERSION

    namespace = "dev.marcocattaneo.sleep"

    signingConfigs {
        create("release") {
            keyAlias = "sleep"
            storeFile = file("release.keystore")
            keyPassword = "sleepapp"
            storePassword = "sleepapp"
        }
    }

    defaultConfig {
        applicationId = AppConfiguration.APPLICATION_ID
        minSdk = Sdk.MIN_SDK_VERSION
        targetSdk = Sdk.TARGET_SDK_VERSION
        versionCode = AppConfiguration.VERSION_CODE
        versionName = AppConfiguration.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(project(mapOf("path" to ":domain")))
    implementation(project(mapOf("path" to ":data")))

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.flowredux)

    implementation(libs.compose.accompanist.placeholder)
    implementation(libs.compose.accompanist.uiController)

    implementation(libs.arrow)
    implementation(libs.timber)

    implementation(libs.hilt.android)
    kapt(libs.hilt.androidCompiler)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk.core)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.turbine)
    testImplementation(libs.roboeletric)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.compose.test.uiJunit4)

    debugImplementation(libs.bundles.compose.debug)
}