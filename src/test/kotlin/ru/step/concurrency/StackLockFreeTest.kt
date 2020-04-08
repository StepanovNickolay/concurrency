package ru.step.concurrency

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.Stack

class StackLockFreeTest {
    private val R = Random(0)

    @Test
    fun `functional test StackBasicImpl`() {
        val myStack = StackBasicImpl<Int>()
        val javaStack = Stack<Int>()
        for (i in 0..999999) {
            when (R.nextInt(2)) {
                0 -> { // push
                    val x: Int = R.nextInt()
                    javaStack.push(x)
                    myStack.push(x)
                }
                1 -> { //pop
                    if (!javaStack.isEmpty()) {
                        assertEquals(javaStack.pop() as Int, myStack.pop())
                    }
                }
            }
        }
    }

    @Test
    fun `functional test StackLockFreeImpl`() {
        val myStack = StackLockFreeImpl<Int>()
        val javaStack = Stack<Int>()
        for (i in 0..999999) {
            when (R.nextInt(2)) {
                0 -> { // push
                    val x: Int = R.nextInt()
                    javaStack.push(x)
                    myStack.push(x)
                }
                1 -> { //pop
                    if (!javaStack.isEmpty()) {
                        assertEquals(javaStack.pop() as Int, myStack.pop())
                    }
                }
            }
        }
    }
}