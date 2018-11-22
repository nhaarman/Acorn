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

package com.nhaarman.acorn.testing

import com.nhaarman.acorn.Logger

/**
 * A [Logger] implementation that outputs its logs to standard out.
 */
class TestLogger : Logger {

    override fun v(tag: String, message: Any?) {
        println("v: [$tag] $message")
    }

    override fun d(tag: String, message: Any?) {
        println("d: [$tag] $message")
    }

    override fun i(tag: String, message: Any?) {
        println("i: [$tag] $message")
    }

    override fun w(tag: String, message: Any?) {
        println("w: [$tag] $message")
    }

    override fun e(tag: String, message: Any?) {
        println("e: [$tag] $message")
    }
}