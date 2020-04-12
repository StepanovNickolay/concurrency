package ru.step.concurrency.problems

import java.util.*
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

/**
 * There are two kinds of threads, oxygen and hydrogen. In order to assemble
 * these threads into water molecules, we have to create a barrier that makes each
 * thread wait until a complete molecule is ready to proceed.
 * As each thread passes the barrier, it should invoke bond. You must guarantee
 * that all the threads from one molecule invoke bond before any of the threads
 * from the next molecule do.
 */
class H2OProblem {
    val mutex = Semaphore(1)
    var oxygen = 0
    var hydrogen = 0
    val barrier = CyclicBarrier(3)
    val oxygenQueue = Semaphore(0)
    val hydrogenQueue = Semaphore(0)

    fun run(){
        List(20) { oxygen() }
        List(20) { hydrogen() }
    }

    fun oxygen() = thread(/*start = false,*/ name = "h2o-oxy-${UUID.randomUUID()}") {
        while (true) {
            mutex.acquire()
            oxygen.inc()
            if (hydrogen >= 2 && oxygen >=1) {
                hydrogenQueue.release(2)
                hydrogen -= 2
                oxygenQueue.release()
                oxygen.dec()
            } else {
                mutex.release()
            }

            oxygenQueue.acquire()
            bond()

            barrier.await()
            mutex.release()
        }
    }

    fun hydrogen() = thread(/*start = false,*/ name = "h2o-hydro-${UUID.randomUUID()}") {
        while (true) {
            mutex.acquire()
            hydrogen.inc()
            if (hydrogen >= 2 && oxygen >= 1) {
                hydrogenQueue.release(2)
                hydrogen -= 2
                oxygenQueue.release()
                oxygen.dec()
            } else {
                mutex.release()
            }

            hydrogenQueue.acquire()
            bond()

            barrier.await()
        }
    }

    fun bond(){}
}

fun main(){
    H2OProblem().run()
}