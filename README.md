# Concurrency

### Data structures

- Queue (impl, functional tests):
    - Michael-Scott lock-free queue impl (https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf)
- Stack (impl, functional tests):
    - Lock-free Treiber stack impl (https://en.wikipedia.org/wiki/Treiber_stack)
    - Lock-free stack with elimination impl (https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf)
- LinkedSet (impl, functional tests):
    - Lock-free linked set based on AtomicMarkableReference 


### Problems

- Concurrent Bank (impl, benchmarks, functional tests)
     - Synchronized based
     - Fine-grained lock based
     - Lock-free RDCSS based
- Dining Philosophers problem
     - Semaphore solution (runnable)
     - Tanenbaum solution (runnable)
- Barbershop problem (runnable)
- H2O problem (pseudo)
- Producer/Consumer problem
     - Infinite buffer (pseudo)
     - Finite buffer (pseudo)
- Readers/Writers problem (pseudo)

### Exercise sources & materials

- https://en.wikipedia.org/wiki/Dining_philosophers_problem
- https://en.wikipedia.org/wiki/Sleeping_barber_problem
- https://en.wikipedia.org/wiki/Producers-consumers_problem
- https://en.wikipedia.org/wiki/Readers-writers_problem
- https://github.com/ITMO-MPP
- The Little Book of Semaphores - Allen B. Downey http://greenteapress.com/semaphores/LittleBookOfSemaphores.pdf
- A Practical Multi-Word Compare-and-Swap Operation- Timothy L. Harris, Keir Fraser and Ian A. Pratt, https://www.cl.cam.ac.uk/research/srg/netos/papers/2002-casn.pdf
- Practical lock-freedom - Keir Fraser https://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-579.pdf
- Wait for your fortune without Blocking - R. Elizarov https://www.youtube.com/watch?v=XivoUctdPIU