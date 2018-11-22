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

import android.app.Activity
import android.view.ViewGroup
import com.nhaarman.acorn.android.internal.contentView
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.util.lazyVar

/**
 * A [UIHandler] that utilizes the [UIState] state machine to handle the UI.
 */
class UIStateUIHandler private constructor(
    private val root: ViewGroup,
    private val transitionFactory: TransitionFactory
) : UIHandler {

    private var state by lazyVar {
        UIState.create(root, transitionFactory)
    }

    override fun onUIVisible() {
        state = state.uiVisible()
    }

    override fun onUINotVisible() {
        state = state.uiNotVisible()
    }

    override fun withScene(
        scene: Scene<out Container>,
        viewControllerFactory: ViewControllerFactory,
        data: TransitionData?
    ) {
        state = state.withScene(scene, viewControllerFactory, data)
    }

    override fun withoutScene() {
        state = state.withoutScene()
    }

    companion object {

        fun create(
            root: ViewGroup,
            transitionFactory: TransitionFactory
        ): UIStateUIHandler {
            return UIStateUIHandler(
                root,
                transitionFactory
            )
        }

        fun create(
            activity: Activity,
            transitionFactory: TransitionFactory
        ): UIStateUIHandler {
            return UIStateUIHandler(
                activity.contentView,
                transitionFactory
            )
        }
    }
}