package ru.step.concurrency.problems.bank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BankFunctionalTests {
    private static final int N = 10;

    @Test
    public void functional_test_synchronized_bank() {
        testBank(SynchronizedBank.class);
    }

    @Test
    public void functional_test_fine_grained_bank() {
        testBank(FineGrainedBank.class);
    }

    @Test
    public void functional_test_lock_free_bank() {
        testBank(LockFreeBankRDCSS.class);
    }

    private <T extends Bank> void testBank(Class<T> bankImpl) {
        testEmptyBank(createInstance(bankImpl));
        testDeposit(createInstance(bankImpl));
        testWithdraw(createInstance(bankImpl));
        testTotalAmount(createInstance(bankImpl));
        testTransfer(createInstance(bankImpl));
    }

    private void testEmptyBank(Bank bank) {
        assertEquals(N, bank.getNumberOfAccounts());
        assertEquals(0, bank.getTotalAmount());
        for (int i = 0; i < N; i++)
            assertEquals(0, bank.getAmount(i));
    }

    private void testDeposit(Bank bank) {
        long amount = 1234;
        long result = bank.deposit(1, amount);
        assertEquals(amount, result);
        assertEquals(amount, bank.getAmount(1));
        assertEquals(amount, bank.getTotalAmount());
    }

    private void testWithdraw(Bank bank) {
        int depositAmount = 2345;
        long depositResult = bank.deposit(1, depositAmount);
        assertEquals(depositAmount, depositResult);
        assertEquals(depositAmount, bank.getAmount(1));
        assertEquals(depositAmount, bank.getTotalAmount());
        long withdrawAmount = 1234;
        long withdrawResult = bank.withdraw(1, withdrawAmount);
        assertEquals(depositAmount - withdrawAmount, withdrawResult);
        assertEquals(depositAmount - withdrawAmount, bank.getAmount(1));
        assertEquals(depositAmount - withdrawAmount, bank.getTotalAmount());
    }

    private void testTotalAmount(Bank bank) {
        long deposit1 = 4567;
        long depositResult1 = bank.deposit(1, deposit1);
        assertEquals(deposit1, depositResult1);
        assertEquals(deposit1, bank.getTotalAmount());
        long deposit2 = 6789;
        long depositResult2 = bank.deposit(2, deposit2);
        assertEquals(deposit2, depositResult2);
        assertEquals(deposit2, bank.getAmount(2));
        assertEquals(deposit1 + deposit2, bank.getTotalAmount());
    }

    private void testTransfer(Bank bank) {
        int depositAmount = 9876;
        long depositResult = bank.deposit(1, depositAmount);
        assertEquals(depositAmount, depositResult);
        assertEquals(depositAmount, bank.getAmount(1));
        assertEquals(depositAmount, bank.getTotalAmount());
        long transferAmount = 5432;
        bank.transfer(1, 2, transferAmount);
        assertEquals(depositAmount - transferAmount, bank.getAmount(1));
        assertEquals(transferAmount, bank.getAmount(2));
        assertEquals(depositAmount, bank.getTotalAmount());
    }

    private <T extends Bank> T createInstance(Class<T> bankImpl) {
        try {
            return (T) bankImpl.getConstructors()[0].newInstance(N);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}