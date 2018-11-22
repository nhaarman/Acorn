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

package com.nhaarman.acorn.android.presentation

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

/**
 * A factory interface that can create [ViewController] instances for [Scene]s.
 *
 * This interface uses the [ViewController] interface to be able to provide both
 * the attachable [Container] and the [View] to be displayed to the user.
 */
interface ViewControllerFactory {

    /**
     * Returns `true` when this ViewControllerFactory can create a [ViewController]
     * when [viewControllerFor] is called.
     * If this method returns false for a specific [SceneKey], no calls to
     * [viewControllerFor] with the same SceneKey must be made.
     */
    fun supports(sceneKey: SceneKey): Boolean

    /**
     * Creates a [ViewController] for given Scene key.
     *
     * @param sceneKey The key of the [Scene] instance for which the
     * corresponding view should be created.
     * @param parent This is the parent [View] that the resulting View should
     * be attached to. The implementation must not add the View to the parent
     * itself, but it can use the parent to generate the LayoutParams of the
     * view.
     *
     * @return The resulting [ViewController]. `null` if no result could be created
     * for given [sceneKey].
     */
    fun viewControllerFor(sceneKey: SceneKey, parent: ViewGroup): ViewController
}