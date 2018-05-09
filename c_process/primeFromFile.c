#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int noOfDivs = 0;
int noOfPrimes = 0;

int isPrime(int x)
{
	if (x == 1) return 0;
	if (x == 2) return 1;
	if (x % 2 == 0) {
		noOfDivs++;
		return 0;
	}

	int upper = floor(sqrt(x));
	for (int i = 3; i < upper; i = i + 2) {
		noOfDivs++;
		if ((x % i) == 0)
			return 0;
	}
	return 1;
}

int main(int argc, char **argv)
{
	if (argc != 2) {
		printf("Error -invalid number of parameters!\n");
		return 1;
	}

	FILE *fIn, *fOut;
	char input[20], output[20];
	sprintf(input, "numbers/%s", argv[1]);
	sprintf(output, "results/%s", argv[1]);

	fIn = fopen(input, "r");
	if (fIn == NULL) {
		return 1;
	}
	fOut = fopen(output, "w");

	int buffer;
	while(fscanf(fIn, "%d ", &buffer) == 1) {
		if (isPrime(buffer)) {
			noOfPrimes++;
		}
	}	

	fprintf(fOut, "Number of primes = %d\nNumber of divisions = %d\n", noOfPrimes, noOfDivs);

    fclose(fIn);
    fclose(fOut);
    return 0;
}