package ru.step.concurrency

import java.util.concurrent.Semaphore

/**
 *   Any number of readers can be in the critical section simultaneously.
 *
 *   Writers must have exclusive access to the critical section.
 */
class ReadersWritersWriteStarvation {
    val readers = 0
    val mutex = Semaphore(1)
    val roomEmpty = Semaphore(1)

    fun writer() {
        roomEmpty.acquire()
        // critical section
        roomEmpty.release()
    }

    fun reader() {
        mutex.acquire()
        readers.inc()
        if (readers == 1) roomEmpty.acquire()  // first in locks
        mutex.release()

        // critical section

        mutex.acquire()
        readers.dec()
        if (readers == 0) roomEmpty.release() // last out unlocks
    }

    val readLightswitch = Lightswitch()
    fun readerWithLightswitch() {
        readLightswitch.lock(roomEmpty)
        // critical section
        readLightswitch.unlock(roomEmpty)
    }
}

class Lightswitch {
    val counter = 0
    val mutex = Semaphore(1)

    fun lock(semaphore: Semaphore) {
        mutex.acquire()
        counter.inc()
        if (counter == 1) semaphore.acquire()
        mutex.release()
    }

    fun unlock(semaphore: Semaphore) {
        mutex.acquire()
        counter.dec()
        if (counter == 0) semaphore.release()
        mutex.release()
    }
}

class ReadersWritersNoWriteStarvation {
    val readLightswitch = Lightswitch()
    val roomEmpty = Semaphore(1)
    val turnstile = Semaphore(1)

    fun writer() {
        turnstile.acquire()
        roomEmpty.acquire()
        // critical section
        turnstile.release()
        roomEmpty.release()
    }

    fun reader() {
        turnstile.acquire()
        turnstile.release()
        readLightswitch.lock(roomEmpty)
        // critical section
        readLightswitch.unlock(roomEmpty)
    }
}

class ReadersWritersWriterPriority {
    val readSwitch = Lightswitch()
    val writeSwitch = Lightswitch()
    val noReaders = Semaphore(1)
    val noWriters = Semaphore(1)

    fun writer() {
        writeSwitch.lock(noReaders)
        noWriters.acquire()
        // critical section
        noWriters.release()
        writeSwitch.unlock(noReaders)
    }

    fun reader() {
        noReaders.acquire()
        readSwitch.lock(noWriters)
        noReaders.release()
        // critical section
        readSwitch.unlock(noWriters)
    }
}


