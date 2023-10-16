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

package dev.marcocattaneo.sleep.di

import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.marcocattaneo.sleep.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


internal class SleepifyApplicationTest {

    @get:Rule
    var rule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testOnCreate() {
        // Given
        val scenario = rule.scenario

        // When
        scenario.onActivity { activity ->
            // then
            assertEquals(SleepifyApplication::class.java, activity.application::class.java)
        }
    }

}