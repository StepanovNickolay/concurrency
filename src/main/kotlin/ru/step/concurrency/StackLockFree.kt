package ru.step.concurrency

import java.util.*
import java.util.concurrent.atomic.AtomicReference

interface Stack<T> {
    /**
     * Pushes an item onto the top of this stack.
     *
     * @param value item to push
     */
    fun push(value: T)
    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return object from top of the stack
     * @throws EmptyStackException if this stack is empty.
     */
    fun peek(): T

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return object from top of the stack
     * @throws EmptyStackException if this stack is empty.
     */
    fun pop(): T
}

class StackBasicImpl<T> : Stack<T> {
    private var top: Node? = null
    private inner class Node(val value: T, var next: Node? = null)

    override fun push(value: T) {
        val newTop = Node(value, top)
        top = newTop
    }

    override fun peek(): T = top?.value
                    ?: throw EmptyStackException()

    override fun pop(): T {
        return if (top == null) {
            throw EmptyStackException()
        } else {
            val value = top!!.value
            top = top!!.next
            value
        }
    }
}

class StackLockFreeImpl<T> : Stack<T> {
    private val top: AtomicReference<Node?> = AtomicReference(null)
    private inner class Node(val value: T, var next: Node? = null)

    override fun push(value: T) {
        var successful = false
        while (!successful) {
            val oldTop = top.get()
            val newTop = Node(value, oldTop)
            successful = top.compareAndSet(oldTop, newTop)
        }
    }

    override fun peek(): T = top.get()?.value
            ?: throw EmptyStackException()

    override fun pop(): T {
        var successful = false
        var newTop: Node?
        val oldTop: Node = top.get()
                ?: throw EmptyStackException()
        while (!successful) {
            newTop = oldTop.next
            successful = top.compareAndSet(oldTop, newTop)
        }
        return oldTop.value
    }
}
