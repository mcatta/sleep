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
    alias(libs.plugins.sleep.android.application)
    alias(libs.plugins.sleep.jacoco)
    alias(libs.plugins.sleep.detekt)
}

android {
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
        applicationId = "dev.marcocattaneo.sleep"
        versionCode = 17
        versionName = "1.3.2"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }

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

    testImplementation(libs.mockk.core)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.turbine)
    testImplementation(libs.roboeletric)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.compose.test.uiJunit4)

    debugImplementation(libs.bundles.compose.debug)
}