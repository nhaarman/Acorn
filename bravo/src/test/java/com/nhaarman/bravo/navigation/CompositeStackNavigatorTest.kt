package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CompositeStackNavigatorTest {

    private val navigator1Scene1 = spy(TestScene(11))
    private val navigator2Scene1 = spy(TestScene(21))
    private val navigator2Scene2 = spy(TestScene(22))

    private val navigator1 = spy(TestSingleSceneNavigator(navigator1Scene1))
    private val navigator2 = spy(TestStackNavigator(listOf(navigator2Scene1)))

    private val navigator = TestCompositeStackNavigator(listOf(navigator1))
    private val listener = mock<Navigator.Events>()

    @Nested
    inner class NavigatorState {

        @Nested
        inner class InactiveNavigator {

            @Test
            fun `navigator is not finished`() {
                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `added listener does not get notified of scene`() {
                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).scene(any())
            }

            @Test
            fun `pushing a navigator does not notify listeners of scene`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.push(navigator2)

                /* Then */
                verify(listener, never()).scene(any())
            }

            @Test
            fun `popping the last scene and navigator from the stack notifies listeners of finished`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.pop()

                /* Then */
                verify(listener).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack does not notify listeners of finished`() {
                /* Given */
                navigator.addListener(listener)
                navigator.push(navigator2)

                /* When */
                navigator.pop()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `popping the second to last navigator from the navigator does not notify listeners of scenes`() {
                /* Given */
                navigator.addListener(listener)
                navigator.push(navigator2)

                /* When */
                navigator.pop()

                /* Then */
                verify(listener, never()).scene(any())
            }

            @Test
            fun `onBackPressed for a single scene notifies listeners of finished`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed for a single scene does not notify screen`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener, never()).scene(any())
            }

            @Test
            fun `onBackPressed for multiple navigators does not notify screen`() {
                /* Given */
                navigator.addListener(listener)
                navigator.push(navigator2)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener, never()).scene(any())
            }
        }

        @Nested
        inner class ActiveNavigator {

            @Test
            fun `navigator is not finished`() {
                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `added listener gets notified of scene`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener).scene(navigator1Scene1)
            }

            @Test
            fun `starting navigator notifies listeners of scene`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener).scene(navigator1Scene1)
            }

            @Test
            fun `starting navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `pushing a navigator notifies listeners of scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onStart()

                /* When */
                navigator.push(navigator2)

                /* Then */
                verify(listener).scene(navigator2Scene1)
            }

            @Test
            fun `start navigator after navigator push notifies pushed scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.push(navigator2)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener).scene(navigator2Scene1)
            }

            @Test
            fun `popping the last scene and navigator from the navigator notifies listeners of finished`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onStart()

                /* When */
                navigator.pop()

                /* Then */
                verify(listener).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack does not notify listeners of finished`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onStart()
                navigator.push(navigator2)

                /* When */
                navigator.pop()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack notifies listeners of proper scenes`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onStart()
                navigator.push(navigator2)

                /* When */
                navigator.pop()

                /* Then */
                listener.inOrder {
                    verify().scene(navigator1Scene1)
                    verify().scene(navigator2Scene1)
                    verify().scene(navigator1Scene1)
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `onBackPressed for a single scene notifies finished`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed for a single scene does not notify screen`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(navigator1Scene1)
                    verify().finished()
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `onBackPressed for multiple navigators notifies proper scenes`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)
                navigator.push(navigator2)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(navigator1Scene1)
                    verify().scene(navigator2Scene1)
                    verify().scene(navigator1Scene1)
                    verifyNoMoreInteractions()
                }
            }
        }

        @Nested
        inner class StoppedNavigator {

            @Test
            fun `stopping navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStop()

                /* Then */
                verify(listener, never()).finished()
            }
        }

        @Nested
        inner class DestroyedNavigator {

            @Test
            fun `destroying navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onDestroy()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `pushing a scene for destroyed navigator does not notify listeners`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onDestroy()

                /* When */
                navigator.push(navigator2)

                /* Then */
                verify(listener, never()).scene(any())
            }

            @Test
            fun `popping from multi item stack for destroyed navigator does not notify scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onDestroy()
                navigator.push(navigator2)

                /* When */
                navigator.pop()

                /* Then */
                verify(listener, never()).scene(any())
            }
        }
    }

    @Nested
    inner class StateForSingleChildNavigatorStack {

        @Test
        fun `starting navigator starts child navigator`() {
            /* When */
            navigator.onStart()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts child navigator only once`() {
            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            verify(navigator1, times(1)).onStart()
        }

        @Test
        fun `stopping an inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `stopping an active navigator stops child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy child navigator`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start child navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStart()

            /* Then */
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start child navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys child navigator once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1, times(1)).onDestroy()
        }
    }

    @Nested
    inner class StateForMultiChildNavigatorStack {

        private val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

        @Test
        fun `starting navigator starts top child navigator`() {
            /* When */
            navigator.onStart()

            /* Then */
            navigator2.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator does not start bottom child navigators`() {
            /* When */
            navigator.onStart()

            /* Then */
            verify(navigator1, never()).onStart()
        }

        @Test
        fun `starting navigator multiple times starts child navigator only once`() {
            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            verify(navigator2, times(1)).onStart()
        }

        @Test
        fun `stopping an inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `stopping an active navigator stops child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            navigator2.inOrder {
                verify().onStart()
                verify().onStop()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop child navigators`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1, never()).onStop()
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy child navigators`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(navigator1, navigator2) {
                verify(navigator2).onDestroy()
                verify(navigator1).onDestroy()
            }
        }

        @Test
        fun `destroy an active navigator stops top child navigator and destroys all child navigators`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStart()
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start child navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStart()

            /* Then */
            verify(navigator2).onDestroy()
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start child scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator2).onDestroy()
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys child navigator once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator2, times(1)).onDestroy()
        }
    }

    @Nested
    inner class StatesWhenManipulatingStack {

        @Test
        fun `popping from a single item stack for inactive navigator destroys child navigator`() {
            /* When */
            navigator.pop()

            /* When */
            verify(navigator1).onDestroy()
        }

        @Test
        fun `popping from a single item stack for inactive navigator does not stop child navigator`() {
            /* When */
            navigator.pop()

            /* When */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `popping from a multi item stack for inactive navigator destroys latest child navigator`() {
            /* Given */
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            /* When */
            navigator.pop()

            /* When */
            verify(navigator2).onDestroy()
        }

        @Test
        fun `popping from a single item stack for active navigator stops and destroys child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            navigator1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `popping from a multi item stack for active navigator stops and destroys latest child navigator, and starts current child navigator`() {
            /* Given */
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onStart()
            }
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator destroys child navigator`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(navigator1).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `onBackPressed from a multi item stack for inactive navigator destroys latest child navigator`() {
            /* Given */
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            /* When */
            navigator.onBackPressed()

            /* When */
            verify(navigator2).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for active navigator stops and destroys child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            navigator1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `onBackPressed from a multi item stack for active navigator stops and destroys latest child navigator, and starts current child navigator`() {
            /* Given */
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onStart()
            }
        }

        @Test
        fun `onBackPressed from a multi item stack for active navigator with multiple scenes does not stop or destroy top child navigator`() {
            /* Given */
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            /* When */
            navigator2.push(navigator2Scene2)
            navigator.onBackPressed()

            /* When */
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not stop previous child navigator`() {
            /* When */
            navigator.push(navigator2)

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not start pushed navigator`() {
            /* When */
            navigator.push(navigator2)

            /* Then */
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `pushing for destroyed navigator does not start pushed navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.push(navigator2)

            /* Then */
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `pushing for started navigator stops previous child navigator and starts pushed child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.push(navigator2)

            /* Then */
            inOrder(navigator1, navigator2) {
                verify(navigator1).onStart()
                verify(navigator1).onStop()
                verify(navigator2).onStart()
                verifyNoMoreInteractions()
            }
        }
    }

    @Nested
    inner class SavingState {

        private val navigator1Scene1 = TestScene(11)
        private val navigator2Scene1 = TestScene(21)

        private val navigator1 = RestorableTestSingleSceneNavigator(navigator1Scene1)
        private val navigator2 = TestStackNavigator(listOf(navigator2Scene1))

        private val navigator = RestorableTestCompositeStackNavigator(listOf(navigator1), null)

        @Test
        fun `saving and restoring state for single navigator stack`() {
            /* Given */
            navigator.onStart()
            navigator1Scene1.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = RestorableTestCompositeStackNavigator(listOf(navigator1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture())
                expect(lastValue).toNotBeTheSameAs(navigator1Scene1)
                expect(lastValue).toBeInstanceOf<TestScene> {
                    expect(it.foo).toBe(3)
                }
            }
        }

        @Test
        fun `saving and restoring state for multiple child navigators on navigator stack`() {
            /* Given */
            navigator.onStart()
            navigator1Scene1.foo = 3
            navigator.push(navigator2)
            navigator2Scene1.foo = 4

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = RestorableTestCompositeStackNavigator(listOf(navigator1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture())
                expect(lastValue).toNotBeTheSameAs(navigator2Scene1)
                expect(lastValue).toBeInstanceOf<TestScene> {
                    expect(it.foo).toBe(4)
                }
            }

            /* When */
            restoredNavigator.pop()

            /* Then */
            argumentCaptor<Scene<out Container>> {
                verify(listener, times(2)).scene(capture())
                expect(lastValue).toNotBeTheSameAs(navigator1Scene1)
                expect(lastValue).toBeInstanceOf<TestScene> {
                    expect(it.foo).toBe(3)
                }
            }
        }
    }

    class TestCompositeStackNavigator(
        private val initialStack: List<Navigator<out Navigator.Events>>
    ) : CompositeStackNavigator<Navigator.Events>(null) {

        override fun initialStack(): List<Navigator<out Navigator.Events>> {
            return initialStack
        }

        override fun instantiateNavigator(
            navigatorClass: Class<Navigator<*>>,
            state: BravoBundle?
        ): Navigator<out Navigator.Events> {
            error("Not supported")
        }
    }

    open class TestSingleSceneNavigator(
        private val scene: Scene<out Container>,
        savedState: BravoBundle? = null
    ) : SingleSceneNavigator<Navigator.Events>(savedState) {

        override fun createScene(state: BravoBundle?): Scene<out Container> {
            return scene
        }
    }

    open class TestStackNavigator(
        private val initialStack: List<TestScene>,
        savedState: BravoBundle? = null
    ) : StackNavigator<Navigator.Events>(savedState) {

        override fun initialStack(): List<Scene<out Container>> {
            return initialStack
        }

        override fun instantiateScene(sceneClass: Class<Scene<*>>, state: BravoBundle?): Scene<*> {
            return when (sceneClass) {
                TestScene::class.java -> TestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    class RestorableTestCompositeStackNavigator(
        private val initialStack: List<Navigator<out Navigator.Events>>,
        savedState: BravoBundle?
    ) : CompositeStackNavigator<Navigator.Events>(savedState) {

        override fun initialStack(): List<Navigator<out Navigator.Events>> {
            return initialStack
        }

        override fun instantiateNavigator(
            navigatorClass: Class<Navigator<*>>,
            state: BravoBundle?
        ): Navigator<out Navigator.Events> {
            return when (navigatorClass) {
                RestorableTestSingleSceneNavigator::class.java -> RestorableTestSingleSceneNavigator(
                    TestScene(0),
                    state
                )
                TestStackNavigator::class.java -> TestStackNavigator(emptyList(), state)
                else -> error("Unknown navigator class: $navigatorClass.")
            }
        }
    }

    open class RestorableTestSingleSceneNavigator(
        private val scene: Scene<out Container>,
        savedState: BravoBundle? = null
    ) : SingleSceneNavigator<Navigator.Events>(savedState) {

        override fun createScene(state: BravoBundle?): Scene<out Container> {
            return state?.let { TestScene.create(it) } ?: scene
        }
    }
}