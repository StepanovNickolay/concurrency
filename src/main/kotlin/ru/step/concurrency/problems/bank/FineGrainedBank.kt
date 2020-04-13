package ru.step.concurrency.problems.bank

import ru.step.concurrency.problems.bank.Bank.Companion.MAX_AMOUNT
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max
import kotlin.math.min

class FineGrainedBank(
        accountsNumber: Int
) : Bank {
    private val accounts: Array<Account> = Array(accountsNumber) { Account() }

    override fun getNumberOfAccounts(): Int = accounts.size

    override fun getAmount(index: Int): Long {
        var result = 0L
        accounts[index].lock.lock()
        result = accounts[index].amount
        accounts[index].lock.unlock()
        return result
    }

    override fun getTotalAmount(): Long {
        var sum = 0L
        for (account in accounts) account.lock.lock()
        for (account in accounts) {
            sum += account.amount
            account.lock.unlock()
        }
        return sum
    }

    override fun deposit(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Amount must be > 0" }
        accounts[index].lock.lock()
        try {
            require(amount < MAX_AMOUNT && accounts[index].amount + amount < MAX_AMOUNT) { "Amount overflow" }
            accounts[index].amount += amount
            return accounts[index].amount
        } finally {
            accounts[index].lock.unlock()
        }
    }

    override fun withdraw(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Amount must be > 0" }
        accounts[index].lock.lock()
        try {
            require(accounts[index].amount - amount >= 0) { "Underflow" }
            accounts[index].amount -= amount
            return accounts[index].amount
        } finally {
            accounts[index].lock.unlock()
        }
    }

    override fun transfer(
            fromIndex: Int,
            toIndex: Int,
            amount: Long
    ) {
        require(amount > 0) { "Amount must be > 0" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        val minIndex = min(fromIndex, toIndex)
        val maxIndex = max(fromIndex, toIndex)
        accounts[minIndex].lock.lock()
        accounts[maxIndex].lock.lock()
        try {
            val from = accounts[fromIndex]
            val to: Account = accounts[toIndex]
            require (amount <= from.amount) { "Underflow" }
            require (amount < MAX_AMOUNT && to.amount + amount < MAX_AMOUNT) { "Overflow" }
            from.amount -= amount
            to.amount += amount
        } finally {
            accounts[maxIndex].lock.unlock()
            accounts[minIndex].lock.unlock()
        }
    }

    private class Account (var amount: Long = 0L) {
        val lock = ReentrantLock()
    }
}