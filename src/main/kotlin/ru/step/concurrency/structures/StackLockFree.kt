package ru.step.concurrency.structures

import ru.step.concurrency.structures.StackElimination.Operation.POP
import ru.step.concurrency.structures.StackElimination.Operation.PUSH
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

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

    private inner class Node<T>(val value: T, var next: Node<T>? = null)

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

/**
 * Lock-free Trieber stack based on Atomic reference
 */
class StackTrieber<T> : Stack<T> {
    private val top: AtomicReference<Node<T>?> = AtomicReference(null)

    private inner class Node<T>(val value: T, var next: Node<T>? = null)

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

/**
 * Lock-free stack with elimination impl
 * https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf
 *
 * This impl limit the number of threads to be used with it
 */
class StackElimination<T> : Stack<T> {
    companion object {
        private val MAX_THREADS = Runtime.getRuntime().availableProcessors()
    }

    private val top: AtomicReference<Node<T>?> = AtomicReference(null)

    private inner class Node<T>(val value: T, var next: Node<T>? = null)

    private val eliminationArray: AtomicReferenceArray<Cell<T>> = AtomicReferenceArray(MAX_THREADS)

    override fun push(value: T) {
        val newNode = Node(value, top.get())
        var cell: Cell<T>? = null
        while (true) {
            if (top.compareAndSet(top.get(), newNode)) return
            if (cell == null) cell = Cell(PUSH, newNode)
            if (backoff(cell)) return
        }
    }

    override fun pop(): T {
        var cell: Cell<T>? = null
        while (true) {
            val currentTop = top.get() ?: throw EmptyStackException()
            val topNext = currentTop.next
            if (top.compareAndSet(currentTop, topNext)) return currentTop.value
            if (cell == null) cell = Cell(POP)
            if (backoff(cell)) return cell.node!!.value
        }
    }

    override fun peek(): T = top.get()?.value
            ?: throw EmptyStackException()

    /**
     * @return true if elimination happened
     */
    private fun backoff(cell: Cell<T>): Boolean {
        cell.stopSpin() // reset spin flag for previous iterations
        eliminationArray.set(cell.id, cell)
        val partner = findCellToEliminate(cell)
        return if (partner != null && cell.operation != partner.operation) {
            val currentCell = this.eliminationArray.compareAndSwap(cell.id, cell, null)!!
            return if (currentCell != cell) {
                if (eliminationArray.compareAndSet(partner.id, currentCell, null)) {
                    if (partner.operation == POP) {
                        partner.node = currentCell.node // exchange
                    }
                    true
                } else false
            } else {
                if (eliminationArray.compareAndSet(partner.id, partner, null)) {
                    if (currentCell.operation == POP) {
                        currentCell.node = partner.node // exchange
                        partner.stopSpin()
                    }
                    true
                } else false
            }
        } else {
            cell.startSpin() // if no partner when will spin for him
            val currentCell = eliminationArray.compareAndSwap(cell.id, cell, null)!!
            if (currentCell != cell) {
                // found cell to eliminate
                if (eliminationArray.compareAndSet(partner!!.id, currentCell, null)) {
                    if (partner.operation == POP) {
                        partner.node = currentCell.node // exchange
                    }
                    true
                } else false
            } else false
        }
    }

    /**
     * @return cell for current to eliminate or null if not found
     */
    private fun findCellToEliminate(cell: Cell<T>): Cell<T>? {
        while (true) {
            val index = cell.random.nextInt(eliminationArray.length())
            if (index != cell.id) {
                val cellToEliminate = eliminationArray.get(index)
                if (cellToEliminate.operation != cell.operation) return cellToEliminate
                return cellToEliminate
            }
        }
    }

    enum class Operation { PUSH, POP }
    private inner class Cell<T>(
            val operation: Operation,
            @Volatile var node: Node<T>? = null,
            @Volatile var wakeup: Boolean = false
    ) {
        val id: Int = ThreadCounter.id()
        val random: ThreadLocalRandom = ThreadLocalRandom.current()

        fun startSpin() {
            workEmulation()
        }

        fun stopSpin() {
            wakeup = true
        }

        private fun workEmulation(): Int {
            var res = 0
            for (i in 1..1_000_000) {
                if (wakeup) break
                res = i * 413423 % 1514
            }
            return res
        }
    }

    /**
     * @return current value
     */
    private fun AtomicReferenceArray<Cell<T>>.compareAndSwap(
            index: Int,
            expected: Cell<T>?,
            new: Cell<T>?
    ): Cell<T>? {
        while (true) {
            val current: Cell<T>? = this[index]
            if (current == expected) {
                if (this.compareAndSet(index, expected, new))
                    return current
            } else return current
        }
    }

    private class ThreadCounter {
        companion object {
            private val threadCounter = AtomicInteger()
            private val id: ThreadLocal<Int> = object : ThreadLocal<Int>() {
                override fun initialValue(): Int? {
                    val id = threadCounter.incrementAndGet()
                    check(id <= MAX_THREADS)
                    return id
                }
            }

            fun id(): Int = id.get()
        }
    }
}
