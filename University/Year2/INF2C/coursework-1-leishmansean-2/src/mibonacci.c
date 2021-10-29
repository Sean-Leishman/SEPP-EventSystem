#include <stdio.h>
#include <stdlib.h>

void printSequence(int sequence[], int sequenceLength) {
    // TODO: Implement me!
    int counter;
    for (counter=0;counter<sequenceLength;counter++){
        int p = sequence[counter];
        printf("%d",p);
        printf(" ");
    }
    printf("\n");
}

void mibonacci(int sequence[], int n) {
    // TODO: Implement me!
    int counter;
    int temp;
    for (counter=3;counter<n+2;counter++){
        sequence[counter] = 1 + sequence[counter-1] + sequence[(counter-2)%4];
    }
}

// Driver code. You shouldn't need to modify this.
int main() {
    FILE *filePointer = fopen("task2.txt", "r");
    int sequence[102];
    int n;

    if (fscanf(filePointer, "%d %d %d %d", &sequence[0], &sequence[1], &sequence[2], &n) == 4) {
        mibonacci(sequence, n);
        printSequence(sequence, n+1);
    } else {
        printf("Incorrectly formatted input file, should be of the format: Mib(0), Mib(1), Mib(2), n\n");
    }

    fclose(filePointer);
    return 0;
}