package com.company.prime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainClass {

    private static final int MAX_NUMBERS = 200000;
    private static final int NO_THREADS = 2;
    private static final double MEAN_ERROR_THRESHOLD = 0.000008;
    private static final int METHOD = 3;

    public static void main(String[] args) {
        int[] numbers = new int[MAX_NUMBERS / 2];

        NumberUtils numberUtils = new NumberUtils(NO_THREADS);

        switch (METHOD) {
            case 1:
                System.out.println("Building intervals with Round Robin");
                numberUtils.fillRoundRobin(numbers, NO_THREADS);
                break;

            case 2:
                System.out.println("Building intervals with shuffle");
                if (numberUtils.fillAndShuffle(numbers, NO_THREADS, MEAN_ERROR_THRESHOLD)) {
                    System.out.println("Numbers shuffled");
                } else {
                    System.out.println("Reached max swap limit");
                }
                break;

            case 3:
                System.out.println("Building with modified Round Robin");
                numberUtils.fillRoundRobinModified(numbers, NO_THREADS, MAX_NUMBERS);
                break;

            default:
                System.out.println("Not a valid choice");
                return;
        }


        int limits[] = new int[NO_THREADS + 1];
        for (int i = 1; i < limits.length; i++) {
            limits[i] = i * numbers.length / NO_THREADS;
        }

        CountDownLatch latch = new CountDownLatch(NO_THREADS);
        long start = System.currentTimeMillis();
        for (int i = 0; i < NO_THREADS; i++) {
            (new PrimeWorker(i, numberUtils, Arrays.copyOfRange(numbers, limits[i], limits[i + 1]), latch)).start();
        }

        try {
            latch.await();
            long stop = System.currentTimeMillis();

            System.out.println("Exec time: " + (stop - start) / 1000.0 + "s");
            System.out.println("No. of swaps: " + numberUtils.getNumberOfSwaps());

            List<Integer> divisions = new ArrayList<Integer>();
            for (int i = 0; i < NO_THREADS; i++) {
                divisions.add(numberUtils.getOperationsOf(i));
                System.out.println("Thread " + i +
                        ":\nNo. of primes: " + numberUtils.getPrimesOf(i) +
                        "\nNo. of divisions: " + numberUtils.getOperationsOf(i));
            }

            System.out.println("Mean variation in divisions: " + numberUtils.getDivisionsVariance(divisions));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
