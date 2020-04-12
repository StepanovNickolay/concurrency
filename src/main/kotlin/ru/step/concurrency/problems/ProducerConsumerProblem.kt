package ru.step.concurrency.problems

import java.util.concurrent.Semaphore

/**
 * Whenever an event occurs, a producer thread creates an event object and
 * adds it to the event buffer. Concurrently, consumer threads take events out
 * of the buffer and process them.
 *
 * While an item is being added to or removed from the buffer, the buffer is
 * in an inconsistent state. Therefore, threads must have exclusive access to
 * the buffer.
 *
 * If a consumer thread arrives while the buffer is empty, it blocks until a
 * producer adds a new item.
 *
 * Producer get event and consumer process event must run concurrently
 */
class ProducerConsumerInfiniteBuffer {
    val mutex = Semaphore(1)
    val items = Semaphore(0)
    lateinit var buffer: Buffer<Event>

    fun producer() {
        val event = waitForEvent()
        mutex.acquire()
        buffer.add(event)
        mutex.release()
        items.release()
    }

    fun consumer() {
        items.acquire()
        mutex.acquire()
        val event = buffer.get()
        mutex.release()
        event.process()
    }
}

class ProducerConsumerFiniteBuffer {
    val mutex = Semaphore(1)
    val items = Semaphore(0)
    val bufferSize = 10
    lateinit var buffer: Buffer<Event>
    val bufferLimit = Semaphore(bufferSize)

    fun producer() {
        val event = waitForEvent()

        bufferLimit.acquire()
        mutex.acquire()
        buffer.add(event)
        mutex.release()
        items.release()
    }

    fun consumer() {
        items.acquire()
        mutex.acquire()
        val event = buffer.get()
        mutex.release()
        bufferLimit.release()

        event.process()
    }
}

fun waitForEvent() = Event()
class Event {
    fun process() {}
}

interface Buffer<T> {
    fun add(event: T)
    fun get(): T
}

