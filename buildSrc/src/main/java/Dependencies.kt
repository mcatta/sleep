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
    const val COMPOSE = "1.1.0"
    const val COMPOSE_NAVIGATION = "2.4.1"
    const val COMPOSE_HILT_NAVIGATION = "1.0.0"
    const val ACTIVITY = "1.4.0"
    const val KOTLIN = "1.6.10"
    const val HILT = "2.41"
    const val COROUTINE = "1.5.2"
}

object Sdk {
    const val COMPILE_SDK_VERSION = 31
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 31
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
    const val PLACEHOLDER = "com.google.accompanist:accompanist-placeholder:0.23.1"
    const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:0.23.1"
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

object ThirdPartyLibs {
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:29.3.0"
    const val FIREBASE_STORAGE = "com.google.firebase:firebase-storage"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx"
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
    const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0"
    const val MOCKK = "io.mockk:mockk:1.12.3"
    const val MOCKK_ANDROID = "io.mockk:mockk-android:1.12.3"
}

object AndroidLibs {
    const val MATERIAL = "com.google.android.material:material:1.5.0"
}