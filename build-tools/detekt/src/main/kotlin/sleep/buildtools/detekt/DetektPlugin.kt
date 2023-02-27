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

package sleep.buildtools.detekt

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import sleep.buildtools.utils.apply
import sleep.buildtools.utils.withType

class DetektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply<DetektPlugin>()

        target.tasks.withType<Detekt> { task ->
            task.buildUponDefaultConfig = true
            task.allRules = false

            task.reports { reports ->
                reports.html.required.set(true)         // observe findings in your browser with structure and code snippets
                reports.sarif.required.set(true)        // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
                reports.md.required.set(true)           // simple Markdown format
            }
        }
    }
}