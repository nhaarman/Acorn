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

package com.nhaarman.acorn.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.annotation.CallSuper
import com.nhaarman.acorn.state.SceneState

/**
 * An abstract [Scene] implementation which implements [LifecycleOwner].
 *
 * @see SaveableScene
 */
abstract class LifecycleScene<V : RestorableContainer>(
    savedState: SceneState?
) : BaseSaveableScene<V>(savedState), LifecycleOwner {

    private val lifecycle by lazy {
        LifecycleRegistry(this)
            .also { it.markState(Lifecycle.State.CREATED) }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    @CallSuper
    override fun onStart() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    @CallSuper
    override fun onStop() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @CallSuper
    override fun onDestroy() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}