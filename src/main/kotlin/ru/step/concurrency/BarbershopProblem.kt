package ru.step.concurrency

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Exchanger
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 *  A barbershop consists of a waiting room with n chairs and barber
 *  room containing the barber chair. If there is no customers to be served,
 *  the barber goes to sleep. If a customer enters the barbershop and
 *  all chairs are occupied, then the customer leaves the shop. If the
 *  barber is busy, but chairs are available, then the customer sits
 *  on the free chairs. If the barber is asleep, the customer wakes up
 *  the barber.
 *
 */
class BarbershopProblem {
    val maxChairs = 4
    val cutomers = AtomicInteger(0)
    val customerReady = Semaphore(0)
    val barberReady = Semaphore(0)
    val customerDone = Semaphore(0)
    val barberDone = Semaphore(0)

    fun run() {
        CompletableFuture.runAsync(barber)
        CompletableFuture.allOf(
                *List(30) {
                    CompletableFuture.runAsync(customer())
                            .orTimeout(30, TimeUnit.SECONDS)
                            .exceptionally { null }
                }.toTypedArray()
        ).join()

        println("done")
    }

    val barber = thread(start = false, name = "barber") {
        while (true) {
            customerReady.acquire()
            barberReady.release()

            cutHair()

            customerDone.release()
            barberDone.acquire()
        }
    }

    fun customer() = thread(start = false, name = UUID.randomUUID().toString()) {
        while (true) {
            if (cutomers.get() == maxChairs) {
                leaveBarbershop()
            } else {
                cutomers.incrementAndGet()
            }

            customerReady.release()
            barberReady.acquire()

            getHairCut()

            customerDone.acquire()
            barberDone.release()

            cutomers.decrementAndGet()
        }
    }

    fun getHairCut() {}
    fun leaveBarbershop() {}
    fun cutHair() {}
}

fun main(){
    BarbershopProblem().run()
}