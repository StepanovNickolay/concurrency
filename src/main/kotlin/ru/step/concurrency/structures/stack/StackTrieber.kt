package ru.step.concurrency.structures.stack

import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * Lock-free Trieber stack based on Atomic reference
 */
class StackTrieber<T> : Stack<T> {
    private val top: AtomicReference<Node<T>?> = AtomicReference(null)
    private class Node<T>(val value: T, var next: Node<T>? = null)

    override fun push(value: T) {
        val newNode = Node(value, top.get())
        while (true) {
            if (top.compareAndSet(top.get(), newNode)) return
        }
    }

    override fun peek(): T = top.get()?.value
            ?: throw EmptyStackException()

    override fun pop(): T {
        while (true) {
            val currentTop = top.get() ?: throw EmptyStackException()
            val topNext = currentTop.next
            if (top.compareAndSet(currentTop, topNext)) return currentTop.value
        }
    }
}