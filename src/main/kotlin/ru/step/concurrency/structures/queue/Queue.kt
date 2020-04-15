package ru.step.concurrency.structures.queue

/**
 * Queue interface.
 *
 * @author Nikita Koval
 */
interface Queue {
    /**
     * Inserts the specified element into this queue
     *
     * @param x the element to add
     */
    fun enqueue(x: Int)

    /**
     * Retrieves and removes the head of this queue,
     * or returns [Int.MIN_VALUE] if this queue is empty
     */
    fun dequeue(): Int

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns [Int.MIN_VALUE] if this queue is empty
     */
    fun peek(): Int
}

class QueueBasicImpl : Queue {
    private class Node(val x: Int, var next: Node? = null)

    private val dummy = Node(0)
    private var head = dummy
    private var tail = dummy

    override fun enqueue(x: Int) {
        val newNode = Node(x)
        tail.next = newNode
        tail = newNode
    }

    override fun dequeue(): Int = if (head === tail) {
        Int.MIN_VALUE
    } else {
        val next = head.next
        if (tail == next) {
            tail = head
            head.next = null
        } else {
            head.next = next!!.next
        }
        next.x
    }

    override fun peek(): Int = if (head === tail) {
        Int.MIN_VALUE
    } else head.next!!.x

}
