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
import org.gradle.api.Project
import sleep.buildtools.android.common.BuildConvention
import sleep.buildtools.utils.configure
import sleep.buildtools.utils.libsCatalog

internal class AndroidComposeConvention : BuildConvention {
    override fun apply(target: Project) {
        target.extensions.configure<CommonExtension<*, *, *, *>> { ext ->
            ext.buildFeatures.compose = true
            ext.composeOptions.kotlinCompilerExtensionVersion =
                target.libsCatalog.findVersion("compose").get().toString()
        }
    }
}