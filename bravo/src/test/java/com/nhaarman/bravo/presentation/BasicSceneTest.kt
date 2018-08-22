package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.ContainerState
import com.nhaarman.bravo.ContainerState.Companion.containerState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class BasicSceneTest {

    private val scene = TestBasicScene()
    private val testView = TestView()

    @Test
    fun `attaching a container stores the view`() {
        /* When */
        scene.attach(testView)

        /* Then */
        expect(scene.view).toBe(testView)
    }

    @Test
    fun `detaching the container releases the view`() {
        /* Given */
        scene.attach(testView)

        /* When */
        scene.detach(testView)

        /* Then */
        expect(scene.view).toBeNull()
    }

    @Test
    fun `view state is restored between views`() {
        /* Given */
        val view1 = TestView(1)
        val view2 = TestView(2)

        /* When */
        scene.attach(view1)
        view1.state = 3
        scene.detach(view1)
        scene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestBasicScene : BasicScene<TestView>() {

        val view get() = currentView
    }

    private class TestView(var state: Int? = null) : Container, RestorableContainer {

        override fun saveInstanceState(): ContainerState {
            return containerState {
                it["state"] = state
            }
        }

        override fun restoreInstanceState(bundle: ContainerState) {
            state = bundle["state"]
        }
    }
}
