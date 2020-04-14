package ru.step.concurrency.structures.linked_set

import java.util.concurrent.atomic.AtomicMarkableReference

class LinkedSetLockFree : Set<Int> {
    private open class Node(
            val value: Int,
            var next: AtomicMarkableReference<Node?> = AtomicMarkableReference(null, false)
    )
    private val head = Node(Int.MIN_VALUE, AtomicMarkableReference(Node(Int.MAX_VALUE), false))
    private fun Node.isRemoved(): Boolean = this.next.isMarked
    private class Window(var cur: Node, var next: Node? = null)

    private fun findWindow(x: Int): Window {
        while (true) {
            val window = Window(head, head.next.reference)
            while (window.next!!.value < x || window.next!!.isRemoved()) {
                if (window.cur.isRemoved()) break
                if (window.next!!.isRemoved()) {
                    // w.cur and w.next both deleted => have to move window
                    val newNext = window.next!!.next.reference!!.next.reference
                    if (window.cur.next.compareAndSet(window.next, newNext, window.next!!.isRemoved(), false)) {
                        window.next = newNext
                    } else {
                        window.next = window.cur.next.reference
                    }
                } else {
                    // move window
                    window.cur = window.next!!
                    window.next = window.cur.next.reference
                }
            }
            if (!window.cur.isRemoved()) return window
        }
    }

    override fun add(x: Int): Boolean {
        while (true) {
            val window = findWindow(x)
            if (window.next!!.value != x) {
                if (window.cur.next.compareAndSet(
                                window.next, Node(x, AtomicMarkableReference(window.next, false)),
                                window.next!!.isRemoved(), false)
                ) return true
            } else return false
        }
    }

    override fun remove(x: Int): Boolean {
        while (true) {
            val window = findWindow(x + 1)
            if (window.cur.value == x) {
                if (window.cur.next.attemptMark(window.next, true)) return true
            } else return false
        }
    }

    override fun contains(x: Int): Boolean {
        val window = findWindow(x)
        return window.next!!.value == x
    }
}