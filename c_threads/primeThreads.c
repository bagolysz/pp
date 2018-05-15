#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <pthread.h>

#define MAX_SWAPS 10

struct threadInfo {
    int threadNo;
    int numberOfPrimes;
    int numberOfDivisions;
    int startIndex;
    int endIndex;
};

int noOfThreads;
int maxNumbers;
int *numbers;
int nSize;

pthread_t *threads;
struct threadInfo *threadInfos;

void fillAndShuffle() {
    int n = 3;
    for (int i = 0; i < nSize; i++) {
        numbers[i] = n;
        n += 2;
    }

    srand(time(NULL));
    for (int i = 0; i < MAX_SWAPS; i++) {
        for (int j = 0; j < nSize; j++) {
            int i1 = rand() % nSize;
            int i2 = rand() % nSize;

            int aux = numbers[i1];
            numbers[i1] = numbers[i2];
            numbers[i2] = aux;
        }
    }
}

void fillRoundRobin() {
    int n = 3;
    int size = nSize / noOfThreads;
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < noOfThreads; j++) {
            numbers[i + j * size] = n;
            n += 2;
        }
    }
}

void fillModifiedRoundRobin() {
    int first = 3;
    int last = 2 * nSize + 1;
    int size = nSize / noOfThreads;

    for (int i = 0; i < size / 2; i++) {
        for (int j = 0; j < noOfThreads; j++) {
            numbers[i + j * size] = first;
            first += 2;
        }
    }

    for (int i = 0; i <= size / 2; i++) {
        for (int j = 0; j < noOfThreads; j++) {
            numbers[(j + 1) * size - i - 1] = last;
            last -= 2;
        }
    }
}

double getOperationVariance()
{
	double mean = 0.0;
	double variation = 0.0;
	for (int i = 0; i < noOfThreads; i++) {
		mean += threadInfos[i].numberOfDivisions;
	}
	mean /= noOfThreads;

	for (int i = 0; i < noOfThreads; i++) {
		variation += abs(mean - threadInfos[i].numberOfDivisions);
	}
	return variation / noOfThreads;
}

int isPrime(int x, int *opCounter)
{
	if (x == 1) return 0;
	if (x == 2) return 1;
	if (x % 2 == 0) {
		(*opCounter)++;
		return 0;
	}

	int upper = floor(sqrt(x));
	for (int i = 3; i <= upper; i = i + 2) {
		(*opCounter)++;
		if ((x % i) == 0)
			return 0;
	}
	return 1;
}

void *calculatePrimes(void *param)
{
	struct threadInfo *info = param;

	int primeCounter = 0;
	int opCounter = 0;
	for (int i = info->startIndex; i < info->endIndex; i++) {
		if (isPrime(numbers[i], &opCounter)) {
			primeCounter++;
		}
	}
	info->numberOfPrimes = primeCounter;
	info->numberOfDivisions = opCounter;
}

void startThreads()
{
	int limits[noOfThreads + 1];
	limits[0] = 0;
	for (int i = 1; i <= noOfThreads; i++) {
		limits[i] = i * nSize / noOfThreads;
	}

	for (int i = 0; i < noOfThreads; i++) {
		threadInfos[i].threadNo = i;
		threadInfos[i].numberOfPrimes = 0;
		threadInfos[i].numberOfDivisions = 0;
		threadInfos[i].startIndex = limits[i];
		threadInfos[i].endIndex = limits[i+1];

		pthread_create(&threads[i], NULL, calculatePrimes, (void*) &threadInfos[i]);
	}

	for (int i = 0; i < noOfThreads; i++) {
		pthread_join(threads[i], NULL);
		printf("From %d:\n%d primes\n%d operations\n\n", i, threadInfos[i].numberOfPrimes, threadInfos[i].numberOfDivisions);
	}
	printf("Mean variation in operations = %lg(ops)\n", getOperationVariance());
}

int main(int argc, char **argv)
{
	if (argc != 4) {
        printf("Error - invalid number of parameters!\n");
        printf("Usage - choice(1,2,3) no_of_threads max_value\n");
        return 1;
    }
    int choice;

    sscanf(argv[1], "%d", &choice);
    sscanf(argv[2], "%d", &noOfThreads);
    sscanf(argv[3], "%d", &maxNumbers);

    nSize = maxNumbers / 2;
    while ((nSize % noOfThreads) != 0) {
    	nSize++;
    }
    numbers = (int*) malloc(nSize * sizeof(int));
    threads = (pthread_t*) calloc(noOfThreads, sizeof(pthread_t));
    threadInfos = (struct threadInfo*) calloc(noOfThreads, sizeof(struct threadInfo));

    switch(choice) {
    	case 1:
    		printf("Random swaps:\n");
    		fillAndShuffle();
    		break;
    	case 2:
    		printf("Round robin:\n");
    		fillRoundRobin();
    		break;
    	default:
    		printf("End-end round robin:\n");
    		fillModifiedRoundRobin();
    }
    startThreads();

    //for (int i = 0; i < nSize; i++) printf("%d ", numbers[i]);    
   	free(numbers);
    free(threads);
    free(threadInfos);
    return 0;
}

