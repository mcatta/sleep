@file:Suppress("ImplicitThis")

import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

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

plugins {
    alias(libs.plugins.sleep.android.library)
    alias(libs.plugins.sleep.jacoco)
    alias(libs.plugins.sleep.detekt)
}

android {
    namespace = "dev.marcocattaneo.sleep.data"

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://europe-west2-sleep-app-mobile.cloudfunctions.net/\"")
    }
}

dependencies {
    implementation(project(mapOf("path" to ":domain")))

    implementation(libs.firebase.auth)
    implementation(libs.arrow)
    implementation(libs.coroutine.core)

    implementation(libs.bundles.retrofit)

    implementation(libs.hilt.core)

    testImplementation(libs.mockk.core)
    testImplementation(libs.coroutine.test)
}