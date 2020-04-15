package ru.step.concurrency.structures.queue

import java.util.concurrent.atomic.AtomicReference

class MSQueue : Queue {
    private class Node(val x: Int, var next: AtomicReference<Node?> = AtomicReference(null))

    private val dummy = Node(0)
    private var head = AtomicReference(dummy)
    private var tail = AtomicReference(dummy)

    override fun enqueue(x: Int) {
        val newNode = Node(x)
        while (true) {
            val currentTail = tail.get()
            if (currentTail.next.compareAndSet(null, newNode)) {
                // try to move tail, if not success - other thread has already done it
                tail.compareAndSet(currentTail, newNode)
                break
            } else {
                // tail is not the last element
                // try to move tail reference, if not success - other thread has already done it.
                // Anyway should do another CAS iteration to update tail value
                tail.compareAndSet(currentTail, currentTail.next.get())
            }
        }
    }

    override fun dequeue(): Int {
        while (true) {
            val first = head.get()
            val last = tail.get()
            val firstNext = first.next.get()
            if (last === first) {
                if (firstNext == null) {
                    // queue is empty
                    return Int.MIN_VALUE
                } else {
                    // enqueue thread failed on move tail reference, try to help it
                    tail.compareAndSet(last, firstNext)
                }
            } else {
                // if CAS not succeed will try again
                if (head.compareAndSet(first, firstNext)) return firstNext!!.x
            }
        }
    }

    override fun peek(): Int {
        while (true) {
            val first = head.get()
            val last = tail.get()
            val firstNext = first.next.get()
            if (last === first) {
                if (firstNext == null) {
                    // queue is empty
                    return Int.MIN_VALUE
                } else {
                    // enqueue thread failed on move tail reference, try to help it
                    tail.compareAndSet(last, firstNext)
                }
            } else {
                // if CAS not succeed will try again
                if (head.compareAndSet(first, first)) return firstNext!!.x
            }
        }
    }
}