package ru.step.concurrency.structures.linked_set

/**
 * Stack interface.
 */
interface Set<T> {
    /**
     * Adds the specified element to this set if it is not already present
     * (optional operation).
     *
     * @param x element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    fun add(x: T): Boolean

    /**
     * Removes the specified element from this set if it is present
     * (optional operation).
     *
     * @param x object to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     */
    fun remove(x: T): Boolean

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     *
     * @param x element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    operator fun contains(x: T): Boolean
}

class SetBasicImpl : Set<Int> {
    private class Node(val value: Int, var next: Node? = null)
    private val head = Node(Int.MIN_VALUE, Node(Int.MAX_VALUE))
    private class Window(var cur: Node, var next: Node? = null)

    private fun findWindow(x: Int): Window {
        val window = Window(head, head.next!!)
        while (window.next!!.value < x) {
            window.cur = window.next!!
            window.next = window.cur.next
        }
        return window
    }

    override fun add(x: Int): Boolean {
        val window = findWindow(x)
        return if (window.next!!.value != x) {
            window.cur.next = Node(x, window.next)
            true
        } else false
    }

    override fun remove(x: Int): Boolean {
        val window = findWindow(x)
        return if (window.next!!.value == x) {
            window.cur.next = window.next!!.next
            true
        } else false
    }

    override fun contains(x: Int): Boolean {
        val window = findWindow(x)
        return window.next!!.value == x
    }
}