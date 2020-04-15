package ru.step.concurrency.structures.queye

import junit.framework.Assert.assertEquals
import org.junit.Test
import ru.step.concurrency.structures.queue.MSQueue
import ru.step.concurrency.structures.queue.Queue
import ru.step.concurrency.structures.queue.QueueBasicImpl
import java.util.*

class QueueFunctionalTests {
    @Test
    fun `functional test QueueBasicImpl`() {
        queueTest(QueueBasicImpl())
    }

    @Test
    fun `functional test MSQueue`() {
        queueTest(MSQueue())
    }

    private fun queueTest(queue: Queue) {
        val random = Random(0)
        val javaQueue = ArrayDeque<Int>()
        for (i in 0..999999) {
            when (random.nextInt(3)) {
                0 -> {
                    println("enqueue")
                    val x = random.nextInt(50)
                    javaQueue.add(x)
                    queue.enqueue(x)
                }
                1 -> {
                    println("dequeue")
                    if (javaQueue.isEmpty()) {
                        assertEquals(Int.MIN_VALUE, queue.dequeue())
                    } else {
                        assertEquals(javaQueue.poll(), queue.dequeue())
                    }
                }
                2 -> {
                    println("peek")
                    if (javaQueue.isEmpty()) {
                        assertEquals(Int.MIN_VALUE, queue.peek())
                    } else {
                        assertEquals(javaQueue.peek(), queue.peek())
                    }
                }
            }
        }
    }
}