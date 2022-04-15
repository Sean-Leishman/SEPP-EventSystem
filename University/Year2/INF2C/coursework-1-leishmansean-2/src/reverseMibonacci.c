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

void reverseMibonacci(int sequence[], int sequenceLength) {
    // TODO: Implement me!
    int start_index=0;
    int end_index = sequenceLength-1;
    int temp;

    while (start_index<end_index){
        temp = sequence[start_index];
        sequence[start_index] = sequence[end_index];
        sequence[end_index] = temp;
        start_index = start_index + 1;
        end_index = end_index - 1;
    }
}

// Driver code. You shouldn't need to modify this.
int main() {
    FILE *filePointer = fopen("task1.txt", "r");

    int maxLength = 100;
    int sequenceLength = 0;
    int *sequence = malloc(sizeof(int) * (maxLength + 1));

    for (int i = 0; i < maxLength; i++) {
        if (fscanf(filePointer, "%d ", &sequence[i]) == 1) sequenceLength++;
        else {
            sequence[i] = -1;
            break;
        }
    }

    fclose(filePointer);

    reverseMibonacci(sequence, sequenceLength);
    printSequence(sequence, sequenceLength);

    return 0;
}
