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

package sleep.buildtools.android.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import sleep.buildtools.android.AndroidConfigs
import sleep.buildtools.android.common.BuildConvention
import sleep.buildtools.utils.configure
import sleep.buildtools.utils.kapt
import sleep.buildtools.utils.libsCatalog

internal class AndroidAppConvention : BuildConvention {
    override fun apply(target: Project) {
        target.dependencies.kapt(target.libsCatalog.findLibrary("hilt.androidCompiler"))

        target.extensions.configure<ApplicationExtension> { ext ->
            ext.testOptions {
                animationsDisabled = true
                unitTests {
                    isIncludeAndroidResources = true
                    isReturnDefaultValues = true
                }
            }
            ext.defaultConfig {
                targetSdk = AndroidConfigs.TARGET_SDK
                testInstrumentationRunner = AndroidConfigs.TEST_INSTRUMENTATION_RUNNER
                vectorDrawables.useSupportLibrary = true
            }

            ext.compileSdk = AndroidConfigs.COMPILE_SDK

            ext.buildFeatures {
                compose = true
            }

            ext.composeOptions {
                kotlinCompilerExtensionVersion = target.libsCatalog.findVersion("compose").get().toString()
            }

            ext.packagingOptions {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    }
}