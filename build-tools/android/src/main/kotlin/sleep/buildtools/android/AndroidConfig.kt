/*
 * Copyright 2023 Marco Cattaneo
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

package sleep.buildtools.android

/**
 * Defines the shared configuration for Android targets.
 */
internal object AndroidConfigs {
    const val COMPILE_SDK: Int = 33
    const val MIN_SDK: Int = 24
    const val TARGET_SDK: Int = 33

    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}

internal object AndroidPlugins {
    const val ANDROID_APPLICATION_PLUGIN = "com.android.application"
    const val ANDROID_LIBRARY_PLUGIN = "com.android.library"
    const val GOOGLE_SERVICES_PLUGIN = "com.google.gms.google-services"
    const val DAGGER_HILT_PLUGIN = "dagger.hilt.android.plugin"
    const val KOTLIN_KAPT_PLUGIN = "kotlin-kapt"
}