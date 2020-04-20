package ru.step.concurrency.problems.bank

import ru.step.concurrency.problems.bank.Bank.Companion.MAX_AMOUNT
import java.util.concurrent.atomic.AtomicReferenceArray

class LockFreeBankRDCSS(
        private val accountsNumber: Int
) : Bank {
    private val accounts: AtomicReferenceArray<Account> = AtomicReferenceArray(accountsNumber)

    init {
        for (i in 0 until accountsNumber) accounts[i] = Account()
    }

    override fun getNumberOfAccounts(): Int = accountsNumber

    override fun getAmount(index: Int): Long {
        while (true) {
            val account = accounts[index]
            /*
             * If there is a pending operation on this account, then help to complete it first using
             * its invokeOperation method. If the result is false then there is no pending operation,
             * thus the account amount can be safely returned.
             */
            if (!account.invokeOperation()) return account.amount
        }
    }

    override fun getTotalAmount(): Long {
        val op = TotalAmountOp()
        op.invokeOperation()
        return op.sum
    }

    override fun deposit(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        check(amount <= MAX_AMOUNT) { "Overflow" }
        while (true) {
            val account = accounts[index]
            if (account.invokeOperation()) continue // if account is pending => loop
            check(account.amount + amount <= MAX_AMOUNT) { "Overflow" }
            val updated = Account(account.amount + amount)
            if (accounts.compareAndSet(index, account, updated)) return updated.amount
        }
    }

    override fun withdraw(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        while (true) {
            val account = accounts[index]
            if (account.invokeOperation()) continue
            check(account.amount - amount >= 0) { "Underflow" }
            val updated = Account(account.amount - amount)
            if (accounts.compareAndSet(index, account, updated)) return updated.amount
        }
    }

    override fun transfer(
            fromIndex: Int,
            toIndex: Int,
            amount: Long
    ) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        check(amount <= MAX_AMOUNT) { "Underflow/overflow" }
        val op = TransferOp(fromIndex, toIndex, amount)
        op.invokeOperation()
        op.errorMessage?.let(::error)
    }

    /**
     * This is an implementation of a restricted form of Harris RDCSS operation:
     * It atomically checks that op.completed is false and replaces accounts[index] with AcquiredAccount instance
     * that hold a reference to the op.
     * This method returns null if op.completed is true.
     */
    private fun acquire(
            index: Int,
            op: Op
    ): AcquiredAccount? {
        while (true) {
            val account = accounts[index]
            if (op.completed) return null
            if (account is AcquiredAccount && account.op === op) return account
            if (!account.invokeOperation()) {
                val acquiredAccount = AcquiredAccount(account.amount, op)
                if (accounts.compareAndSet(index, account, acquiredAccount)) return acquiredAccount
            }
        }
    }

    private fun release(
            index: Int,
            op: Op
    ) {
        assert(op.completed)
        val account = accounts[index]
        if (account is AcquiredAccount && account.op === op) {
            val updated = Account(account.newAmount)
            accounts.compareAndSet(index, account, updated)
        }
    }


    private open class Account(val amount: Long = 0L) {
        /**
         * Invokes operation that is pending on this account.
         * This implementation returns false (no pending operation), other implementations return true.
         */
        open fun invokeOperation(): Boolean = false
    }

    private class AcquiredAccount(
            /**
             * New amount of funds in this account when op completes.
             */
            var newAmount: Long,
            val op: Op
    ) : Account(newAmount) {
        override fun invokeOperation(): Boolean {
            op.invokeOperation()
            return true
        }
    }

    /**
     * Abstract operation that acts on multiple accounts.
     */
    private abstract inner class Op {
        /**
         * True when operation has completed.
         */
        @Volatile
        var completed = false

        abstract fun invokeOperation()
    }

    /**
     * Descriptor for [getTotalAmount] operation.
     */
    private inner class TotalAmountOp : Op() {
        var sum = 0L

        override fun invokeOperation() {
            var sum = 0L
            var acquired = 0
            while (acquired < accountsNumber) {
                val account = acquire(acquired, this) ?: break
                sum += account.newAmount
                acquired++
            }
            if (acquired == accountsNumber) {
                this.sum = sum
                completed = true
            }
            /*
             * To ensure lock-freedom, we must release all accounts even if this particular helper operation
             * had failed to acquire all of them before somebody else had completed the operations.
             * By releasing all accounts for completed operation we ensure progress of other operations.
             */
            for (i in 0 until getNumberOfAccounts()) {
                release(i, this)
            }
        }
    }

    /**
     * Descriptor for [transfer] operation.
     */
    private inner class TransferOp(
            val fromIndex: Int,
            val toIndex: Int,
            val amount: Long
    ) : Op() {
        var errorMessage: String? = null

        override fun invokeOperation() {
            assert(amount > 0) { "Amount must be > 0" }
            assert(fromIndex != toIndex) { "fromIndex == toIndex" }

            acquire(fromIndex.coerceAtMost(toIndex), this)
            val from = acquire(fromIndex, this)
            val to = acquire(toIndex, this)

            if (from != null && to != null) {
                when {
                    amount > from.amount -> errorMessage = "Underflow"
                    to.amount + amount > MAX_AMOUNT -> errorMessage = "Overflow"
                    else -> {
                        from.newAmount = from.amount - amount
                        to.newAmount = to.amount + amount
                    }
                }
            }
            completed = true
            release(toIndex, this)
            release(fromIndex, this)
        }
    }
}