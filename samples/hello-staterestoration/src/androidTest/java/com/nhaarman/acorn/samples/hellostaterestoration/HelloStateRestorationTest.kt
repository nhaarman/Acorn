/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.samples.hellostaterestoration

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class HelloStateRestorationTest {

    @Rule @JvmField val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun navigatingThroughScenes() {
        onView(withText("0")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("1")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("2")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("3")).check(matches(isDisplayed()))

        pressBack()
        pressBack()
        pressBack()

        onView(withText("0")).check(matches(isDisplayed()))
    }

    @Test
    fun viewStateWhenRotating() {
        setPortrait()

        onView(withHint("Type some text")).perform(typeText("Hello!"), closeSoftKeyboard())

        rotate()

        onView(withText(containsString("Hello!"))).check((matches(isDisplayed())))
    }

    @Test
    fun viewStateWhenNavigating() {
        onView(withHint("Type some text")).perform(typeText("Hello!"), closeSoftKeyboard())

        onView(withText("Next")).perform(click())
        pressBack()

        onView(withText(containsString("Hello!"))).check((matches(isDisplayed())))
    }

    private fun setPortrait() {
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun rotate() {
        rule.activity.requestedOrientation = when (rule.activity.requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> error("Unknown state: ${rule.activity.requestedOrientation}")
        }
        sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}