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

package com.nhaarman.acorn.notesapp

import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.ImmediateThinScheduler
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

class ImmediateMainThreadExtension :
    Extension,
    BeforeAllCallback,
    AfterAllCallback {

    private val ExtensionContext.store get() = getStore(ExtensionContext.Namespace.create(this))

    override fun beforeAll(context: ExtensionContext) {
        context.store.put("original", mainThread)
        mainThread = ImmediateThinScheduler.INSTANCE
    }

    override fun afterAll(context: ExtensionContext) {
        mainThread = context.store.get("original", Scheduler::class.java)
    }
}
