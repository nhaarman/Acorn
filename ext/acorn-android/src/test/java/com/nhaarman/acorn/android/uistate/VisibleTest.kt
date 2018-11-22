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

package com.nhaarman.acorn.android.uistate

import com.nhaarman.acorn.android.util.RootViewGroup
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestTransitionFactory
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class VisibleTest {

    val root = spy(RootViewGroup())
    val scene = spy(TestScene())
    val sceneView = TestView()
    val viewController = TestViewController(sceneView)
    val viewControllerFactory = TestViewControllerFactory()

    val state = Visible(
        root,
        TestTransitionFactory()
    )

    @BeforeEach
    fun setup() {
        viewControllerFactory.register(scene.key, viewController)
    }

    @Test
    fun `'uiVisible' makes no transition`() {
        expect(state.uiVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' makes no transition`() {
        expect(state.withoutScene()).toBe(state)
    }

    @Test
    fun `'uiNotVisible' results in NotVisible state`() {
        expect(state.uiNotVisible()).toBeInstanceOf<NotVisible>()
    }

    @Test
    fun `'withScene' results in VisibleWithDestination state`() {
        expect(state.withScene(scene, viewControllerFactory, null)).toBeInstanceOf<VisibleWithDestination>()
    }

    @Test
    fun `'withScene' replaces root children with new Scene view`() {
        /* When */
        state.withScene(scene, viewControllerFactory, null)

        /* Then */
        expect(root.views).toBe(listOf(sceneView))
    }

    @Test
    fun `'withScene' attaches container to scene`() {
        /* When */
        state.withScene(scene, viewControllerFactory, null)

        /* Then */
        verify(scene).attach(any())
    }
}