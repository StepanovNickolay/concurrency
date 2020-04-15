package ru.step.concurrency.structures.queue

class MSQueue : Queue {
    private class Node(val x: Int, var next: Node? = null)

    private val dummy = Node(0)
    private var head = dummy
    private var tail = dummy

    override fun enqueue(x: Int) {
        val newNode = Node(x)
        tail.next = newNode
        tail = newNode
    }

    override fun dequeue(): Int = if (head == tail) {
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

    override fun peek(): Int = if (head == tail) {
        Integer.MIN_VALUE
    } else {
        try {
            head.next!!.x
        } catch (ex: Exception) {
            throw ex
        }
    }
}