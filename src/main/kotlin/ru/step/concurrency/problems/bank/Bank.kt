package ru.step.concurrency.problems.bank

/**
 * Bank interface.
 *
 * @author Roman Elizarov
 */
interface Bank {
    /**
     * Returns number of accounts in this bank.
     *
     * @return number of accounts in this bank.
     */
    fun getNumberOfAccounts(): Int

    /**
     * Returns current amount in the specified account.
     *
     * @param index account index from 0 to [n][.getNumberOfAccounts]-1.
     * @return amount in account.
     * @throws IndexOutOfBoundsException when index is invalid account index.
     */
    fun getAmount(index: Int): Long

    /**
     * Returns total amount deposited in this bank.
     *
     * @return total amount deposited in this bank.
     */
    fun getTotalAmount(): Long

    /**
     * Deposits specified amount to account.
     *
     * @param index account index from 0 to [n][.getNumberOfAccounts]-1.
     * @param amount positive amount to deposit.
     * @return resulting amount in account.
     * @throws IllegalArgumentException when amount <= 0.
     * @throws IndexOutOfBoundsException when index is invalid account index.
     * @throws IllegalStateException when deposit will overflow account above [.MAX_AMOUNT].
     */
    fun deposit(index: Int, amount: Long): Long

    /**
     * Withdraws specified amount from account.
     *
     * @param index account index from 0 to [n][.getNumberOfAccounts]-1.
     * @param amount positive amount to withdraw.
     * @return resulting amount in account.
     * @throws IllegalArgumentException when amount <= 0.
     * @throws IndexOutOfBoundsException when index is invalid account index.
     * @throws IllegalStateException when account does not enough to withdraw.
     */
    fun withdraw(index: Int, amount: Long): Long

    /**
     * Transfers specified amount from one account to another account.
     *
     * @param fromIndex account index to withdraw from.
     * @param toIndex account index to deposit to.
     * @param amount positive amount to transfer.
     * @throws IllegalArgumentException when amount <= 0 or fromIndex == toIndex.
     * @throws IndexOutOfBoundsException when account indices are invalid.
     * @throws IllegalStateException when there is not enough funds in source account or too much in target one.
     */
    fun transfer(fromIndex: Int, toIndex: Int, amount: Long)

    companion object {
        /**
         * The maximal amount that can be kept in a bank account.
         */
        const val MAX_AMOUNT = 1000000000000000L
    }
}