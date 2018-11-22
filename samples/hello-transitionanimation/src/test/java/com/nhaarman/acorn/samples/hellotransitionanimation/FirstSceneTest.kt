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

package com.nhaarman.acorn.samples.hellotransitionanimation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class FirstSceneTest {

    private val listener = mock<FirstScene.Events>()
    private val scene = FirstScene(listener)
    private val container = TestContainer()

    @Test
    fun `clicking button notifies listener`() {
        /* Given */
        scene.attach(container)

        /* When */
        container.clickSecondScene()

        /* Then */
        verify(listener).secondSceneRequested()
    }

    private class TestContainer : FirstSceneContainer {

        fun clickSecondScene() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onSecondSceneClicked(f: () -> Unit) {
            listener = f
        }
    }
}