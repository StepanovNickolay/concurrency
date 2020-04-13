package ru.step.concurrency.structures.stack

import java.util.*

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
    private var top: Node<T>? = null
    private class Node<T>(val value: T, var next: Node<T>? = null)

    override fun push(value: T) {
        val newTop = Node(value, top)
        top = newTop
    }

    override fun peek(): T = top?.value
            ?: throw EmptyStackException()

    override fun pop(): T {
        val value = top?.value ?: throw EmptyStackException()
        top = top?.next ?: throw EmptyStackException()
        return value
    }
}