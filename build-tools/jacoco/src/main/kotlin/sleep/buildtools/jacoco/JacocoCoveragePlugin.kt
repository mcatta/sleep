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

package sleep.buildtools.jacoco

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import sleep.buildtools.utils.*

internal class JacocoCoveragePlugin : Plugin<Project> {

    private companion object {
        const val JACOCO_TASK_NAME = "createDebugCoverage"

        const val APPLICATION_PLUGIN_NAME = "com.android.application"
        const val LIBRARY_PLUGIN_NAME = "com.android.library"
        const val JVM_PLUGIN_NAME = "org.jetbrains.kotlin.jvm"
    }

    private val Project.jacocoVersion: String
        get() = libsCatalog.findVersion("jacoco").get().toString()

    private fun Project.getTaskByNameOrNull(name: String): String? {
        return tasks.findByPath(name)?.let { name }
    }

    override fun apply(target: Project) {
        target.applyJacocoPlugin()

        target.pluginManager.withPlugins(LIBRARY_PLUGIN_NAME, APPLICATION_PLUGIN_NAME) {
            target.configureJacocoForAndroid()
        }
        target.pluginManager.withPlugins(JVM_PLUGIN_NAME) {
            target.configureJacocoForJvm()
        }
    }

    /**
     * Apply jacoco plugin on the [Project]
     */
    private fun Project.applyJacocoPlugin() {
        val jacocoVersion = jacocoVersion

        pluginManager.apply<JacocoPlugin>()
        configure<JacocoPluginExtension> {
            toolVersion = jacocoVersion
        }

        configurations.all { configuration ->
            configuration.resolutionStrategy { resolutionStrategy ->
                resolutionStrategy.eachDependency { details ->
                    if ("org.jacoco" == details.requested.group) {
                        details.useVersion(jacocoVersion)
                    }
                }
            }
        }
    }

    /**
     * Setup jacoco for Android
     */
    private fun Project.configureJacocoForAndroid() {
        // Setup Configuration
        val jacocoConfig: JacocoReport.() -> Unit = {
            group = "Reporting"
            description = "Generate Jacoco coverage reports on the ${project.name.capitalized()} build."

            listOf("testDebugUnitTest", "createDebugCoverageReport")
                .mapNotNull { name -> getTaskByNameOrNull(name) }
                .toSet()
                .let(::setDependsOn)

            reports { report ->
                report.xml.required.set(true)
            }

            val androidDebugTree =
                fileTree("$buildDir/tmp/kotlin-classes/debug") { it.exclude(JacocoOptions.EXCLUDED_FILES) }
            val jvmDebugTree = fileTree("$buildDir/classes/kotlin/main") { it.exclude(JacocoOptions.EXCLUDED_FILES) }
            val sourceDirs = "$projectDir/src/main/kotlin"

            sourceDirectories.setFrom(sourceDirs)
            classDirectories.setFrom(androidDebugTree, jvmDebugTree)
            executionData.setFrom(fileTree(buildDir) { it.include(listOf("**/*.exec", "**/*.ec")) })
        }

        // Workaround for Jacoco issues when run from the IDE
        tasks.withType<Test> { testTask ->
            testTask.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }

        // Register Jacoco Gradle Task
        afterEvaluate {
            tasks.register(JACOCO_TASK_NAME, jacocoConfig)
        }
    }

    /**
     * Setup Jacoco for JVM
     */
    private fun Project.configureJacocoForJvm() {
        // Setup Configuration
        val jacocoConfig: JacocoReport.() -> Unit = {
            group = "Reporting"
            description = "Generate Jacoco coverage reports on the ${project.name.capitalized()} build."

            setDependsOn(setOf(getTaskByNameOrNull("test")))

            reports { report ->
                report.xml.required.set(true)
            }

            classDirectories.setFrom(
                files(
                    classDirectories.files.map { file -> fileTree(file) { it.exclude(JacocoOptions.EXCLUDED_FILES) } }
                )
            )
        }

        // Workaround for Jacoco issues when run from the IDE
        tasks.withType<Test> { testTask ->
            testTask.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }

        // Register Jacoco Gradle Task
        afterEvaluate {
            tasks.register(JACOCO_TASK_NAME, jacocoConfig)
        }
    }

}