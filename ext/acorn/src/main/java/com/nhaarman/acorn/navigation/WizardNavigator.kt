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

package com.nhaarman.acorn.navigation

import androidx.annotation.CallSuper
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.internal.v
import com.nhaarman.acorn.internal.w
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.SaveableScene
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.util.lazyVar
import kotlin.reflect.KClass

/**
 * An abstract [Navigator] class that is able to go back and forth through a
 * list of [Scene]s.
 *
 * This Navigator has two methods [next] and [previous] to navigate through the
 * Scenes. Calling [previous] when the first Scene is being shown will have no
 * effect, calling [next] when the last Scene is being shown will finish this
 * Navigator.
 *
 * Implementers must implement [createScene] to provide the proper Scenes.
 *
 * This Navigator implements [SaveableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by this class's saveInstanceState() method.
 */
abstract class WizardNavigator(
    private val savedState: NavigatorState?
) : Navigator, SaveableNavigator, OnBackPressListener {

    /**
     * Creates the Scene for given [index], starting at `0`.
     *
     * This method will be called up to once for each index, results will be
     * reused when navigating through the wizard.
     *
     * @return the created [Scene], or `null` if the end of the wizard is reached.
     */
    protected abstract fun createScene(index: Int): Scene<out Container>?

    /**
     * Instantiates a [Scene] instance for given [sceneClass] and [state].
     *
     * This function is called when restoring the StackNavigator from a saved state.
     *
     * @param sceneClass The class of the [Scene] to instantiate.
     * @param state The saved state of the [Scene] if applicable. This will be
     * the instance as returned from [SaveableScene.saveInstanceState] if its
     * state was saved.
     */
    protected abstract fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container>

    private var state by lazyVar {
        val size: Int? = savedState?.get("size")
        val activeIndex: Int? = savedState?.get("active_index")
        if (size == null || activeIndex == null || savedState == null) {
            val scene = createScene(0) ?: error("Initial Scene may not be null.")
            return@lazyVar State.create(listOf(scene), 0) { index -> createScene(index) }
        }

        @Suppress("UNCHECKED_CAST")
        val scenes = (0 until size)
            .map { index ->
                instantiateScene(
                    sceneClass = Class.forName(savedState["${index}_class"]).kotlin as KClass<out Scene<*>>,
                    state = savedState["${index}_state"]
                )
            }

        State.create(scenes, activeIndex) { index -> createScene(index) }
    }

    @CallSuper
    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        state.addListener(listener)

        (state as? State.Active)
            ?.let { state -> state.scenes[state.activeIndex] }
            ?.let { listener.scene(it, null) }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
            }
        }
    }

    /**
     * Navigates to the next [Scene] in this wizard if possible.
     *
     * If there is no next Scene, this Navigator will finish and all Scenes will
     * be destroyed.
     *
     * If there is a next Scene and this Navigator is currently active, the
     * current [Scene] will be stopped, and the next Scene will be started.
     *
     * If there is a next Scene and this Navigator is currently inactive, no Scene
     * lifecycle events will be called at all. Starting this Navigator will trigger
     * a call to the [Scene.onStart] of the next Scene in the wizard.
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     */
    fun next() {
        v("WizardNavigator", "next")

        state = state.next()
    }

    /**
     * Navigates to the next [Scene] in this wizard if possible.
     *
     * If there is no previous Scene, nothing will happen.
     *
     * If there is a previous Scene and this Navigator is currently active, the
     * current [Scene] will be stopped, and the previous Scene will be started.
     *
     * If there is a previous Scene and this Navigator is currently inactive, no
     * Scene lifecycle events will be called at all. Starting this Navigator will
     * trigger a call to the [Scene.onStart] of the previous Scene in the wizard.
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     */
    fun previous() {
        v("WizardNavigator", "previous")

        state = state.previous()
    }

    /**
     * Finishes this Navigator.
     *
     * If this Navigator is currently active, the current Scene will go through
     * its destroying lifecycle calling [Scene.onStop] and [Scene.onDestroy].
     *
     * If this Navigator is currently not active, the current Scene will only
     * have its [Scene.onDestroy] method called.
     *
     * Calling this method when the Navigator has been destroyed will have no
     * effect.
     */
    fun finish() {
        state = state.finish()
    }

    @CallSuper
    override fun onStart() {
        v("WizardNavigator", "onStart")

        state = state.start()
    }

    @CallSuper
    override fun onStop() {
        v("WizardNavigator", "onStop")

        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v("WizardNavigator", "onDestroy")

        state = state.destroy()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is State.Destroyed) return false

        v("WizardNavigator", "onBackPressed")
        if (state.activeIndex == 0) {
            state = state.finish()
        } else {
            state = state.previous()
        }

        return true
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return state.scenes
            .foldIndexed(NavigatorState()) { index, bundle, scene ->
                bundle.also {
                    it["${index}_class"] = scene::class.java.name
                    it["${index}_state"] = (scene as? SaveableScene)?.saveInstanceState()
                }
            }
            .also {
                it["size"] = state.scenes.size
                it["active_index"] = state.activeIndex
            }
    }

    override fun isDestroyed(): Boolean {
        return state is State.Destroyed
    }

    private sealed class State {

        abstract val scenes: List<Scene<out Container>>
        abstract val activeIndex: Int

        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): State
        abstract fun stop(): State
        abstract fun destroy(): State

        abstract fun next(): State
        abstract fun previous(): State

        abstract fun finish(): State

        companion object {

            fun create(
                scenes: List<Scene<out Container>>,
                initialIndex: Int,
                factory: (Int) -> Scene<out Container>?
            ): State {
                return Inactive(scenes, initialIndex, emptyList(), factory)
            }
        }

        class Inactive(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            override var listeners: List<Navigator.Events>,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
                if (activeIndex >= scenes.size) throw ArrayIndexOutOfBoundsException("Scene index out of range: $activeIndex.")
            }

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): State {
                scenes[activeIndex].onStart()
                listeners.forEach { it.scene(scenes[activeIndex], null) }
                return Active(scenes, activeIndex, listeners, factory)
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                scenes.asReversed().forEach { it.onDestroy() }
                return Destroyed()
            }

            override fun next(): State {
                val newScenes = scenes.filledUpTo(activeIndex + 1, factory)

                if (newScenes == null) {
                    scenes.asReversed().forEach { it.onDestroy() }
                    return Destroyed()
                }

                return Inactive(newScenes, activeIndex + 1, listeners, factory)
            }

            override fun previous(): State {
                val newIndex = Math.max(0, activeIndex - 1)
                return Inactive(scenes, newIndex, listeners, factory)
            }

            override fun finish(): State {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Active(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            override var listeners: List<Navigator.Events>,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
            }

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): State {
                return this
            }

            override fun stop(): State {
                scenes[activeIndex].onStop()
                return Inactive(scenes, activeIndex, listeners, factory)
            }

            override fun destroy(): State {
                return stop().destroy()
            }

            override fun next(): State {
                scenes[activeIndex].onStop()
                val newIndex = activeIndex + 1
                val newScenes = scenes.filledUpTo(newIndex, factory)

                if (newScenes == null) {
                    scenes.asReversed().forEach { it.onDestroy() }
                    listeners.forEach { it.finished() }
                    return Destroyed()
                }

                newScenes[newIndex].onStart()
                listeners.forEach { it.scene(newScenes[newIndex], TransitionData.forwards) }
                return Active(newScenes, newIndex, listeners, factory)
            }

            override fun previous(): State {
                if (activeIndex == 0) return this

                scenes[activeIndex].onStop()
                scenes[activeIndex - 1].onStart()
                listeners.forEach { it.scene(scenes[activeIndex - 1], TransitionData.backwards) }
                return Active(scenes, activeIndex - 1, listeners, factory)
            }

            override fun finish(): State {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Destroyed : State() {

            override val scenes: List<Scene<out Container>> = emptyList()
            override val activeIndex: Int = -1

            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): State {
                w("WizardNavigator.State", "Warning: Cannot start state after navigator is destroyed.")
                return this
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                return this
            }

            override fun next(): State {
                w("WizardNavigator.State", "Warning: Cannot go to next scene after navigator is destroyed.")
                return this
            }

            override fun previous(): State {
                w("WizardNavigator.State", "Warning: Cannot go to previous scene after navigator is destroyed.")
                return this
            }

            override fun finish(): State {
                w("WizardNavigator.State", "Warning: Cannot finish navigator after navigator is destroyed.")
                return this
            }
        }

        protected fun <T> List<T>.filledUpTo(index: Int, f: (Int) -> T?): List<T>? {
            if (size > index) return this

            return (size..index)
                .fold<Int, List<T>?>(this) { list, i ->
                    f(i)?.let { t -> list?.let { it + t } }
                }
        }
    }
}
