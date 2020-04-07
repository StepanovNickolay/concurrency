package ru.step.concurrency

import java.util.concurrent.atomic.AtomicReference

interface Stack<T> {
    /**
     * Pushes an item onto the top of this stack.
     */
    fun push(value: T)
    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     */
    fun peek(): T

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     */
    fun pop(): T
}

class StackImpl<T>(value: T) : Stack<T> {
    private var top: Node = Node(value)
    private inner class Node(val value: T, var next: Node? = null)

    override fun push(value: T) {
        val newTop = Node(value, top)
        top = newTop
    }

    override fun peek(): T =
            top.value

    override fun pop(): T {
        val value = top.value
        top = top.next!!
        return value
    }
}

class StackLockFreeImpl<T>(value: T) : Stack<T> {
    private val top: AtomicReference<Node> = AtomicReference(Node(value))
    private inner class Node(val value: T, var next: Node? = null)

    override fun push(value: T) {
        var successful = false
        while (!successful) {
            val oldTop = top.get()
            val newTop = Node(value, oldTop)
            successful = top.compareAndSet(oldTop, newTop)
        }
    }

    override fun peek(): T =
            top.get().value

    override fun pop(): T {
        var successful = false
        var newTop: Node?
        var oldTop: Node? = null
        while (!successful) {
            oldTop = top.get()
            newTop = oldTop.next
            successful = top.compareAndSet(oldTop, newTop)
        }
        return oldTop!!.value
    }
}
