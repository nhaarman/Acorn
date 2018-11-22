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

import kotlin.reflect.KClass

/**
 * A class representing the key for a Scene.
 */
inline class SceneKey(val value: String) {

    override fun toString(): String {
        return "SceneKey(value='$value')"
    }

    companion object {

        /**
         * Create a [SceneKey] for given [Scene] class, consisting of its fully
         * qualified name.
         */
        fun <T : Scene<*>> from(sceneClass: KClass<T>): SceneKey {
            return SceneKey.from(sceneClass.java)
        }

        /**
         * Create a [SceneKey] for given [Scene] class, consisting of its fully
         * qualified name.
         */
        fun <T : Scene<*>> from(sceneClass: Class<T>): SceneKey {
            return SceneKey(sceneClass.name)
        }

        /**
         * Returns the default [SceneKey] for [T], consisting of its fully
         * qualified class name.
         */
        inline fun <reified T : Scene<*>> T.defaultKey(): SceneKey {
            return SceneKey.from(T::class)
        }

        inline fun <reified T : Scene<*>> defaultKey(): SceneKey {
            return from(T::class)
        }
    }
}