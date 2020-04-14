package ru.step.concurrency.structures.linked_set

import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.collections.LinkedHashSet

class FunctionalTests {
    @Test
    fun `functional test SetBasicImpl`() {
        setTest(SetBasicImpl())
    }

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
                0 ->                 // add
                    Assert.assertEquals(javaSet.add(x), set.add(x))
                1 ->                 // contains
                    Assert.assertEquals(javaSet.contains(x), set.contains(x))
                2 ->                 // remove
                    Assert.assertEquals(javaSet.remove(x), set.remove(x))
            }
        }
    }
}