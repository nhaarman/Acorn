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

package com.nhaarman.acorn.notesapp.android.ui.createitem

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.nhaarman.acorn.android.tests.AcornViewTestRule
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ViewFactoryProvider
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.expect.expect
import com.nhaarman.expect.lastValue
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Rule
import org.junit.Test

class CreateItemViewTest {

    @Rule @JvmField val rule = AcornViewTestRule<CreateItemContainer>(
        ViewFactoryProvider.viewFactory,
        CreateItemScene.key
    )

    @Test
    fun settingInitialText() {
        /* When */
        rule.onUiThread { container.setInitialText("Hello!") }

        /* Then */
        onView(withText("Hello!")).check(matches(isDisplayed()))
    }

    @Test
    fun inputText() {
        /* Given */
        val observer = rule.container.textChanges.test()

        /* When */
        onView(withHint("Take a note")).perform(typeText("Hello, world!"))

        /* Then */
        expect(observer.lastValue.trim()).toBe("Hello, world!")
    }

    @Test
    fun clickCreate() {
        /* Given */
        val observer = rule.container.createClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        /* When */
        onView(withId(R.id.save)).perform(click())

        /* Then */
        expect(observer.valueCount()).toBe(1)
    }
}
