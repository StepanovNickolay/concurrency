package ru.step.concurrency.structures.linked_set

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.collections.LinkedHashSet

class FunctionalTests {
    @Test
    fun `functional test SetBasicImpl`() {
        setTest(SetBasicImpl())
    }

    /**
     * TODO: fix it
     */
    @Test
    fun `functional test LinkedSetLockFree`() {
        setTest(LinkedSetLockFree())
    }

    private fun setTest(set: Set<Int>) {
        val random = Random(0)
        val javaSet: MutableSet<Int> = LinkedHashSet()
        for (i in 0..999999) {
            val op = random.nextInt(3)
            val x = random.nextInt(30)
            when (op) {
                0 -> {
                    println("push")
                    assertEquals(javaSet.add(x), set.add(x))
                }
                1 ->  {
                    println("contains")
                    assertEquals(javaSet.contains(x), set.contains(x))
                }
                2 -> {
                    println("contains")
                    assertEquals(javaSet.remove(x), set.remove(x))
                }
            }
        }
    }
}