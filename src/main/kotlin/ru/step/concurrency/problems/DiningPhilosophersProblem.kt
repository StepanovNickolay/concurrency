package ru.step.concurrency.problems

import kotlinx.coroutines.*
import java.util.concurrent.Semaphore
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

fun main() {
//    GlobalSemaphore().run()
    TanenbaumSemaphore().run()
}

typealias DiningPhilosophers = Runnable

class GlobalSemaphore : DiningPhilosophers {
    override fun run() {
        val forks = MutableList(5) { Semaphore(1) }
        val semaphore = Semaphore(4)
        List(5) { index ->
            val philosopher = thread {
                fun left(philosopherIndex: Int) = philosopherIndex
                fun right(philosopherIndex: Int) = (philosopherIndex + 1) % 5

                fun getForks(i: Int) {
                    semaphore.acquire()
                    forks[right(i)].acquire()
                    forks[left(i)].acquire()
                }

                fun putForks(i: Int) {
                    forks[right(i)].release()
                    forks[left(i)].release()
                    semaphore.release()
                }
                while (true) {
                    getForks(index)
                    putForks(index)
                }
            }
            philosopher
        }
    }
}

class TanenbaumSemaphore : DiningPhilosophers {
    override fun run() {
        val mutex: Lock = ReentrantLock()
        val state = MutableList(5) { State.THINKING }
        val semaphores = MutableList(5) { Semaphore(0) }
        List(5) { index ->
            val philosopher = thread {
                fun left(i: Int) = i
                fun right(i: Int) = (i + 1) % 5

                fun tryEat(i: Int) {
                    if (state[i] == State.HUNGRY && state[left(i)] != State.EATING && state[right(i)] != State.EATING) {
                        state[i] = State.EATING
                        semaphores[i].release()
                    }
                }

                fun getForks(i: Int) {
                    mutex.lock()
                    state[i] = State.HUNGRY
                    tryEat(i)
                    mutex.unlock()
                    semaphores[i].acquire()
                }

                fun putForks(i: Int) {
                    mutex.lock()
                    state[i] = State.THINKING
                    tryEat(right(i))
                    tryEat(left(i))
                    mutex.unlock()
                }

                while (true) {
                    getForks(index)
                    putForks(index)
                }
            }
            philosopher
        }
    }

    enum class State {
        THINKING,
        HUNGRY,
        EATING
    }
}