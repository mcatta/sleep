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
}

android {
    compileSdk = Sdk.COMPILE_SDK_VERSION

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
        release {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(mapOf("path" to ":mvi")))
    implementation(project(mapOf("path" to ":domain")))
    implementation(project(mapOf("path" to ":data")))

    implementation(AndroidXLibs.CORE)
    implementation(AndroidXLibs.APP_COMPACT)
    implementation(AndroidLibs.MATERIAL)
    implementation(AndroidXLibs.MEDIA)

    implementation(ComposeLibs.UI)
    implementation(ComposeLibs.MATERIAL)
    implementation(ComposeLibs.TOOLING_PREVIEW)
    implementation(ComposeLibs.NAVIGATION)
    implementation(ComposeLibs.HILT_NAVIGATION)
    implementation(ComposeLibs.ACTIVITY)

    implementation(ComposeAccompanistLibs.PLACEHOLDER)
    implementation(ComposeAccompanistLibs.SYSTEM_UI_CONTROLLER)

    implementation(ThirdPartyLibs.FIREBASE_STORAGE)
    implementation(ThirdPartyLibs.FIREBASE_FIRESTORE)
    implementation(ThirdPartyLibs.ARROW_CORE)
    implementation(ThirdPartyLibs.TIMBER)

    testImplementation(kotlin("test"))
    testImplementation(TestLibs.MOCKK)
    testImplementation(TestLibs.COROUTINE_TEST)

    androidTestImplementation(AndroidXTestLibs.JUNIT)
    androidTestImplementation(ComposeLibs.UI_TEST)

    implementation(HiltLibs.ANDROID)
    kapt(HiltLibs.ANDROID_COMPILER)

    debugImplementation(ComposeLibs.TOOLING)
    debugImplementation(ComposeLibs.TOOLING_PREVIEW)
}