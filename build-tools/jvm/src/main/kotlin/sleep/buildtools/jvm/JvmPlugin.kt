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

package sleep.buildtools.jvm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import sleep.buildtools.utils.*


internal class JvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply<KotlinPluginWrapper>()

        target.tasks.withType<KotlinCompile> { task ->
            task.kotlinOptions.allWarningsAsErrors = target.getBooleanProperty("sleep.jvm.warning-as-error")
        }

        target.extensions.configure<KotlinTopLevelExtension> { ext ->
            ext.jvmToolchain {
                it.languageVersion.set(JavaLanguageVersion.of(target.getIntProperty("sleep.jvm.version")))
            }
        }
    }
}