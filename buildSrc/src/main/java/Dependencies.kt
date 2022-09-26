/*
 * Copyright 2022 Marco Cattaneo
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

object Versions {
    const val COMPOSE = "1.2.0"
    const val COMPOSE_NAVIGATION = "2.5.1"
    const val COMPOSE_HILT_NAVIGATION = "1.0.0"
    const val ACTIVITY = "1.5.0"
    const val KOTLIN = "1.7.0"
    const val HILT = "2.42"
    const val COROUTINE = "1.6.4"
    const val FLOW_REDUX = "1.0.0"
    const val TURBINE = "0.11.0"
}

object PluginVersions {
    const val DETEKT = "1.21.0"
}

object Sdk {
    const val COMPILE_SDK_VERSION = 32
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 32
}

object ComposeLibs {
    const val UI = "androidx.compose.ui:ui:${Versions.COMPOSE}"
    const val MATERIAL = "androidx.compose.material:material:${Versions.COMPOSE}"
    const val TOOLING = "androidx.compose.ui:ui-tooling:${Versions.COMPOSE}"
    const val TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE}"
    const val ACTIVITY = "androidx.activity:activity-compose:${Versions.ACTIVITY}"
    const val UI_TEST = "androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE}"
    const val NAVIGATION = "androidx.navigation:navigation-compose:${Versions.COMPOSE_NAVIGATION}"
    const val HILT_NAVIGATION = "androidx.hilt:hilt-navigation-compose:${Versions.COMPOSE_HILT_NAVIGATION}"
}

object ComposeAccompanistLibs {
    const val PLACEHOLDER = "com.google.accompanist:accompanist-placeholder:0.25.1"
    const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:0.25.1"
}

object KotlinLibs {
    const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}"
}

object HiltLibs {
    const val ANDROID = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val CORE = "com.google.dagger:hilt-core:${Versions.HILT}"
    const val COMPILER = "com.google.dagger:hilt-compiler:${Versions.HILT}"
    const val ANDROID_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"
}

object FlowReduxLibs {
    const val CORE = "com.freeletics.flowredux:flowredux-jvm:${Versions.FLOW_REDUX}"
    const val COMPOSE = "com.freeletics.flowredux:compose:${Versions.FLOW_REDUX}"
}

object ThirdPartyLibs {
    const val FIREBASE_STORAGE = "com.google.firebase:firebase-storage-ktx:20.0.1"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx:24.1.0"
    const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    const val ARROW_CORE = "io.arrow-kt:arrow-core:1.0.1"
}

object AndroidXLibs {
    const val APP_COMPACT= "androidx.appcompat:appcompat:${Versions.ACTIVITY}"
    const val CORE = "androidx.core:core-ktx:1.7.0"
    const val MEDIA = "androidx.media:media:1.5.0"
}

object AndroidXTestLibs {
    const val JUNIT = "androidx.test.ext:junit:1.1.3"
}

object TestLibs {
    const val JUNIT = "junit:junit:4.13.2"
    const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
    const val MOCKK = "io.mockk:mockk:1.12.5"
    const val MOCKK_ANDROID = "io.mockk:mockk-android:1.12.5"
    const val TURBINE = "app.cash.turbine:turbine:${Versions.TURBINE}"
}

object AndroidLibs {
    const val MATERIAL = "com.google.android.material:material:1.6.1"
}
