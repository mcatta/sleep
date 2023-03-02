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

package sleep.buildtools.android.plugin

import org.gradle.api.Project
import sleep.buildtools.android.AndroidPlugins
import sleep.buildtools.android.common.BaseAndroidPlugin
import sleep.buildtools.android.convention.AndroidLibraryConvention
import sleep.buildtools.utils.apply

internal class AndroidLibraryPlugin : BaseAndroidPlugin() {
    override fun onApplyPlugins(target: Project) {
        target.pluginManager.apply(AndroidPlugins.ANDROID_LIBRARY_PLUGIN)
        target.pluginManager.apply<AndroidPlugin>()
    }

    override fun onPluginsApplied(target: Project) = AndroidLibraryConvention().apply(target)
}