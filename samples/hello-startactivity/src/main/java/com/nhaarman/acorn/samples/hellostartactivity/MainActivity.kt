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

package com.nhaarman.acorn.samples.hellostartactivity

import android.content.Context
import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class MainActivity : AcornAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloStartActivityNavigatorProvider
    }

    override fun provideActivityControllerFactory(): ActivityControllerFactory {
        return object : ActivityControllerFactory {

            override fun supports(sceneKey: SceneKey): Boolean {
                return sceneKey == SceneKey.from(MapsScene::class)
            }

            override fun activityControllerFor(scene: Scene<*>, context: Context): ActivityController {
                return MapsActivityController()
            }
        }
    }
}
