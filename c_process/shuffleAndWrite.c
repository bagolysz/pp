#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

int MAX_NUMBERS;
int NO_THREADS;
#define MAX_SWAPS 10
//#define NO_THREADS 4

void writeToFile(int *a, int aSize, char *method) {
    FILE *f[NO_THREADS];
    char buffer[20];
    int limits[NO_THREADS + 1];
    limits[0] = 0;

    for (int i = 0; i < NO_THREADS; i++) {
        sprintf(buffer, "numbers/n_%s_%d.txt", method, i);
        f[i] = fopen(buffer, "w");
        limits[i + 1] = (i + 1) * aSize / NO_THREADS;
        printf("%d\n", limits[i+1]);
    }

    for (int i = 0; i < NO_THREADS; i++) {
        for (int j = limits[i]; j < limits[i + 1]; j++) {
            fprintf(f[i], "%d ", a[j]);
        }
        fclose(f[i]);
    }
}

void fillAndShuffle(int *numbers, int nSize) {
    FILE *f;
    f = fopen("numbers/nums_total.txt", "w");

    int n = 3;
    for (int i = 0; i < nSize; i++) {
        numbers[i] = n;
        fprintf(f, "%d ", n);
        n += 2;
    }
    fclose(f);


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

    char *method = "s";
    writeToFile(numbers, nSize, method);
}


void fillRoundRobin(int *numbers, int nSize) {
    int n = 3;
    int size = nSize / NO_THREADS;
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < NO_THREADS; j++) {
            numbers[i + j * size] = n;
            n += 2;
        }
    }

    char *method = "rr";
    writeToFile(numbers, nSize, method);
}

void fillModifiedRoundRobin(int *numbers, int nSize) {
    int first = 3;
    int last = 2 * nSize + 1;
    int size = nSize / NO_THREADS;

    for (int i = 0; i < size / 2; i++) {
        for (int j = 0; j < NO_THREADS; j++) {
            numbers[i + j * size] = first;
            first += 2;
        }
    }

    for (int i = 0; i <= size / 2; i++) {
        for (int j = 0; j < NO_THREADS; j++) {
            numbers[(j + 1) * size - i - 1] = last;
            last -= 2;
        }
    }

    char *method = "rrm";
    writeToFile(numbers, nSize, method);
}

int main(int argc, char **argv) 
{
    if (argc != 3) {
        printf("Error - invalid number of parameters!\n");
        printf("Usage - shuffle no_of_workers max_value\n");
        return 1;
    }
    sscanf(argv[1], "%d", &NO_THREADS);
    sscanf(argv[2], "%d", &MAX_NUMBERS);

    int nSize = MAX_NUMBERS / 2;
    while ((nSize % NO_THREADS) != 0) {
        nSize++;
    }

    int numbers[nSize];

    fillAndShuffle(numbers, nSize);
    fillRoundRobin(numbers, nSize);
    fillModifiedRoundRobin(numbers, nSize);

    return 0;
}
