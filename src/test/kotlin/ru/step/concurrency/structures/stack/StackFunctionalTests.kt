package ru.step.concurrency.structures.stack

import org.junit.Assert.*
import org.junit.Test
import java.util.*
import java.util.Stack

class StackFunctionalTests {
    @Test
    fun `functional test StackBasicImpl`() {
        stackTest(StackBasicImpl())
    }

    @Test
    fun `functional test StackTrieber`() {
        stackTest(StackTrieber())
    }

    @Test
    fun `functional test StackElimination`() {
        stackTest(StackElimination())
    }

    private fun stackTest(stackImpl: ru.step.concurrency.structures.stack.Stack<Int>) {
        val random = Random(0)
        val javaStack = Stack<Int>()
        for (i in 0..999999) {
            when (random.nextInt(2)) {
                0 -> { // push
                    val x: Int = random.nextInt()
                    javaStack.push(x)
                    stackImpl.push(x)
                }
                1 -> { //pop
                    if (!javaStack.isEmpty()) {
                        assertEquals(javaStack.pop() as Int, stackImpl.pop())
                    } else {
                        var javaStackEx: Exception? = null
                        try { javaStack.pop() } catch (ex: Exception) { javaStackEx = ex }
                        assertThrows(javaStackEx!!::class.java) { stackImpl.pop() }
                    }
                }
            }
        }
    }
}