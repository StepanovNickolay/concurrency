package ru.step.concurrency.problems.bank

class SynchronizedBank(
        accountsNumber: Int
) : Bank {
    private val accounts = Array(accountsNumber) { Account() }

    override fun getNumberOfAccounts(): Int = accounts.size

    @Synchronized
    override fun getAmount(index: Int): Long = accounts[index].amount

    @Synchronized
    override fun getTotalAmount(): Long = accounts.asSequence().map(Account::amount).sum()

    @Synchronized
    override fun deposit(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Amount must be > 0" }
        val account = accounts[index]
        require(amount < Bank.MAX_AMOUNT && account.amount + amount < Bank.MAX_AMOUNT) { "Amount overflow" }
        account.amount += amount
        return account.amount
    }

    @Synchronized
    override fun withdraw(
            index: Int,
            amount: Long
    ): Long {
        require(amount > 0) { "Amount must be > 0" }
        val account = accounts[index]
        require(account.amount - amount > 0) { "Underflow" }
        account.amount -= amount
        return account.amount
    }

    @Synchronized
    override fun transfer(
            fromIndex: Int,
            toIndex: Int,
            amount: Long
    ) {
        require(amount > 0) { "Amount must be > 0" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        val fromAcc = accounts[fromIndex]
        val toAcc = accounts[toIndex]
        require (amount <= fromAcc.amount) { "Underflow" }
        require (amount < Bank.MAX_AMOUNT && toAcc.amount + amount < Bank.MAX_AMOUNT) { "Overflow" }
        fromAcc.amount -= amount
        toAcc.amount += amount
    }

    class Account(var amount: Long = 0L)
}