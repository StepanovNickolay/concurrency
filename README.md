# Concurrency

### Data structures

1. Stack (impl, functional tests):
    - Lock-free Trieber stack impl (https://en.wikipedia.org/wiki/Treiber_stack)
    - Lock-free stack with elimination impl (https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf)
2. LinkedSet (impl, functional tests)
    - Lock-free based on AtomicMarkableReference 


### Problems

1. Dining Philosophers problem
     - Semaphore solution (runnable)
     - Tanenbaum solution (runnable)
2. Barbershop problem (runnable)
3. H2O problem (pseudo)
4. Producer/Consumer problem
     - Infinite buffer (pseudo)
     - Finite buffer (pseudo)
5. Readers/Writers problem (pseudo)
6. Concurrent Bank (impl, benchmarks, functional tests)
     - Synchronized based
     - Fine-grained lock based

### Exercise sources

- https://en.wikipedia.org/wiki/Dining_philosophers_problem
- https://en.wikipedia.org/wiki/Sleeping_barber_problem
- https://en.wikipedia.org/wiki/Producers-consumers_problem
- https://en.wikipedia.org/wiki/Readers-writers_problem
- https://github.com/ITMO-MPP