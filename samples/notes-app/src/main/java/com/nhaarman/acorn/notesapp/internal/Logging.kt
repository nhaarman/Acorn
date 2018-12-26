/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.notesapp.internal

import com.nhaarman.acorn.logger

internal fun v(tag: String, message: Any?) = logger?.v(tag, message)
internal fun d(tag: String, message: Any?) = logger?.d(tag, message)
internal fun i(tag: String, message: Any?) = logger?.i(tag, message)
internal fun w(tag: String, message: Any?) = logger?.w(tag, message)
