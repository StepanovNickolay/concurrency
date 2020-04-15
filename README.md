# Concurrency

### Data structures

- Queue (impl, functional tests):
    - Michael-scott lock-free queue impl (https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf)
- Stack (impl, functional tests):
    - Lock-free Treiber stack impl (https://en.wikipedia.org/wiki/Treiber_stack)
    - Lock-free stack with elimination impl (https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf)
- LinkedSet (impl, functional tests):
    - Lock-free based on AtomicMarkableReference 


### Problems

- Dining Philosophers problem
     - Semaphore solution (runnable)
     - Tanenbaum solution (runnable)
- Barbershop problem (runnable)
- H2O problem (pseudo)
- Producer/Consumer problem
     - Infinite buffer (pseudo)
     - Finite buffer (pseudo)
- Readers/Writers problem (pseudo)
- Concurrent Bank (impl, benchmarks, functional tests)
     - Synchronized based
     - Fine-grained lock based

### Exercise sources

- https://en.wikipedia.org/wiki/Dining_philosophers_problem
- https://en.wikipedia.org/wiki/Sleeping_barber_problem
- https://en.wikipedia.org/wiki/Producers-consumers_problem
- https://en.wikipedia.org/wiki/Readers-writers_problem
- https://github.com/ITMO-MPP