package com.company.prime;

import java.util.List;
import java.util.Random;

class NumberUtils {

    private static final int MAX_SWAPS = 15;

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

        while (!isUniformlyShuffled(numbers, noOfIntervals, meanErrorThreshold) && numberOfSwaps < MAX_SWAPS) {
            for (int i = 0; i < size; i++) {
                int i1 = rn.nextInt(size);
                int i2 = rn.nextInt(size);

                int aux = numbers[i1];
                numbers[i1] = numbers[i2];
                numbers[i2] = aux;
            }
            numberOfSwaps++;
        }

        return numberOfSwaps < MAX_SWAPS;
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

    void fillRoundRobinModified(int[] numbers, int noOfIntervals, int maxNumber) {
        int first = 3;
        int last = maxNumber % 2 == 0 ? maxNumber + 1 : maxNumber;
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


    double getDivisionsVariance(List<Integer> divisions) {
        double mean = 0.0;
        double variation = 0.0;
        for (Integer d : divisions) {
            mean += d;
        }
        mean /= divisions.size();

        for (Integer d : divisions) {
            variation += Math.abs(mean - d);
        }
        return variation/divisions.size();
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
