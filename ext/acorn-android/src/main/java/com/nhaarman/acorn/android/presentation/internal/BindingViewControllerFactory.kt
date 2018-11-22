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

package com.nhaarman.acorn.android.presentation.internal

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.presentation.SceneKey

/**
 * A [ViewControllerFactory] implementation that binds [SceneKey]s to
 * [ViewControllerFactory] instances to create views.
 */
internal class BindingViewControllerFactory(
    private val bindings: Map<SceneKey, ViewControllerFactory>
) : ViewControllerFactory {

    override fun supports(sceneKey: SceneKey): Boolean {
        return bindings.containsKey(sceneKey)
    }

    override fun viewControllerFor(sceneKey: SceneKey, parent: ViewGroup): ViewController {
        val viewControllerFactory = bindings[sceneKey]
            ?: throw IllegalStateException("Could not create ViewController for Scene with key $sceneKey.")

        return viewControllerFactory.viewControllerFor(sceneKey, parent)
    }
}
