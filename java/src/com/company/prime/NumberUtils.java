package com.company.prime;

import java.util.List;
import java.util.Random;

class NumberUtils {

    private static final int MAX_SWAPS = 10;

    private int[] primeNumbers;
    private int[] operations;

    private int numberOfSwaps;

    NumberUtils(int noOfWorkers) {
        primeNumbers = new int[noOfWorkers];
        operations = new int[noOfWorkers];
        numberOfSwaps = 0;
    }

    synchronized void accumulateResult(int threadId, int primes, int ops) {
        primeNumbers[threadId] = primes;
        operations[threadId] = ops;
    }

    private boolean isUniformlyShuffled(int[] numbers, int noOfIntervals, double meanErrorThreshold) {
        double means[] = new double[noOfIntervals];
        double sum = 0;
        int limits[] = new int[noOfIntervals + 1];

        for (int i = 1; i < limits.length; i++) {
            limits[i] = i * numbers.length / noOfIntervals;
        }

        for (int i = 0; i < noOfIntervals; i++) {
            for (int j = limits[i]; j < limits[i + 1]; j++) {
                means[i] += numbers[j];
                sum += numbers[j];
            }
        }

        double intervalWeight = 1.0 / noOfIntervals;
        for (int i = 0; i < noOfIntervals; i++) {
            means[i] /= sum;
            if (Math.abs(means[i] - intervalWeight) > meanErrorThreshold) {
                return false;
            }
        }
        return true;
    }

    boolean fillAndShuffle(int[] numbers, int noOfIntervals, double meanErrorThreshold) {
        int n = 3;
        int size = numbers.length;
        for (int i = 0; i < size; i++) {
            numbers[i] = n;
            n += 2;
        }

        Random rn = new Random();

        while (numberOfSwaps < MAX_SWAPS) {
            for (int i = 0; i < size; i++) {
                int i1 = rn.nextInt(size);
                int i2 = rn.nextInt(size);

                int aux = numbers[i1];
                numbers[i1] = numbers[i2];
                numbers[i2] = aux;
            }
            numberOfSwaps++;
        }

        return isUniformlyShuffled(numbers, noOfIntervals, meanErrorThreshold);
    }

    void fillRoundRobin(int[] numbers, int noOfIntervals) {
        int n = 3;
        int size = numbers.length / noOfIntervals;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < noOfIntervals; j++) {
                numbers[i + j * size] = n;
                n = n + 2;
            }
        }
    }

    void fillRoundRobinModified(int[] numbers, int noOfIntervals) {
        int first = 3;
        int last = 2 * numbers.length + 1;
        int size = numbers.length / noOfIntervals;
        for (int i = 0; i < size/2; i++) {
            for (int j = 0; j < noOfIntervals; j++) {
                numbers[i + j * size] = first;
                first = first + 2;
            }
        }

        for (int i = 0; i <= size/2; i++)
            for (int j = 0; j < noOfIntervals; j++) {
                int lastIndex = (j+1)*size - i - 1;
                numbers[lastIndex] = last;
                last = last - 2;
            }
    }


    double getDivisionsVariance(List<Integer> noOfOps) {
        double mean = 0.0;
        double variation = 0.0;
        for (Integer d : noOfOps) {
            mean += d;
        }
        mean /= noOfOps.size();

        for (Integer d : noOfOps) {
            variation += Math.abs(mean - d);
        }
        return variation/noOfOps.size();
    }

    int getPrimesOf(int workerNo) {
        return primeNumbers[workerNo];
    }

    int getOperationsOf(int workerNo) {
        return operations[workerNo];
    }

    int getNumberOfSwaps() {
        return numberOfSwaps;
    }

}
