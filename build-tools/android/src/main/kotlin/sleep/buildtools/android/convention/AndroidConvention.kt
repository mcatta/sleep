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

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import sleep.buildtools.android.AndroidBuildTypes
import sleep.buildtools.android.AndroidConfigs
import sleep.buildtools.android.common.BuildConvention
import sleep.buildtools.utils.*

internal class AndroidConvention : BuildConvention {
    override fun apply(target: Project) {
        val javaIntVersion = target.getIntProperty("sleep.jvm.version")
        val javaVersion = JavaVersion.toVersion(javaIntVersion)

        target.tasks.withType<KotlinCompilationTask<KotlinJvmCompilerOptions>> { task ->
            task.compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(javaIntVersion.toString()))
                freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-Xinline-classes")
            }
        }

        target.extensions.configure<CommonExtension<*, *, *, *, *, *>> { ext ->
            ext.compileSdk = AndroidConfigs.COMPILE_SDK

            ext.defaultConfig {
                minSdk = AndroidConfigs.MIN_SDK
                testInstrumentationRunner = AndroidConfigs.TEST_INSTRUMENTATION_RUNNER
            }

            ext.compileOptions.apply {
                sourceCompatibility = javaVersion
                targetCompatibility = javaVersion
            }

            ext.buildTypes {
                getByName(AndroidBuildTypes.RELEASE.key).apply {
                    isMinifyEnabled = false
                    proguardFiles(
                        ext.getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
                getByName(AndroidBuildTypes.DEBUG.key).apply {
                    isMinifyEnabled = false
                    enableAndroidTestCoverage = true
                    enableUnitTestCoverage = true
                }
            }

            ext.testOptions {
                unitTests.isIncludeAndroidResources = true
            }
        }
    }

}
