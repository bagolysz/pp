package com.company.prime;

import java.util.concurrent.CountDownLatch;

public class PrimeWorker extends Thread {

    private int threadId;
    private NumberUtils numberUtils;
    private int[] numbers;
    private int primes;
    private int operations;
    private CountDownLatch latch;

    PrimeWorker(int threadId, NumberUtils numberUtils, int[] numbers, CountDownLatch latch) {
        this.threadId = threadId;
        this.numberUtils = numberUtils;
        this.numbers = numbers;
        this.latch = latch;
        primes = 0;
        operations = 0;
    }

    public void run() {
        for (int number : numbers) {
            int[] res = isPrime(number);
            primes += res[0];
            operations += res[1];
        }

        numberUtils.accumulateResult(threadId, primes, operations);
        latch.countDown();
    }

    /**
     * Verifies if a number is prime.
     *
     * @param x the number which is verified
     * @return an int array of 2 values: first is 1 if prime; 0 otherwise. second value is the number of divisions
     */
    private int[] isPrime(int x) {
        int noOps = 0;

        if (x == 1) return new int[]{0, 0};
        if (x == 2) return new int[]{1, 0};
        if (x % 2 == 0) {
            noOps++;
            return new int[]{0, noOps};
        }

        int upperLimit = (int) Math.floor(Math.sqrt(x));
        for (int i = 3; i < upperLimit; i = i + 2) {
            noOps++;
            if (x % i == 0) {
                return new int[]{0, noOps};
            }
        }
        return new int[]{1, noOps};
    }
}
