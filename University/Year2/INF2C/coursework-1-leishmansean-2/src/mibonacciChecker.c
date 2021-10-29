#include <stdio.h>
#include <stdlib.h>

void mibonacciChecker(int sequence[], int input[], int inputLength) {
    // TODO: Implement me!
    int counter = 0;
    int index = 0;
    int swapped = 0;
    while (counter<=100 && index < inputLength){
        if (counter >= 3 && swapped == 0){
            sequence[counter] = 1 + sequence[counter-1] + sequence[(counter-2)%4];
        }
        swapped = 0;
        if (sequence[counter] == input[index]){
            printf("%d",sequence[counter]);
            printf(" ");
            index = index + 1;
        }
        if (sequence[counter] > input[index]){
            index = index + 1;
            counter = counter - 1;
            swapped = 1;
        }
        counter = counter + 1;
    }
    printf("\n");
}

// Driver code. You shouldn't need to modify this.
int main() {
    FILE *filePointer = fopen("task4.txt", "r");
    int sequence[102];

    fscanf(filePointer, "%d ", &sequence[0]);
    fscanf(filePointer, "%d ", &sequence[1]);
    fscanf(filePointer, "%d ", &sequence[2]);

    int maxLength = 100;
    int inputLength = 0;
    int *input = malloc(sizeof(int) * (maxLength+1));

    for (int i = 0; i < maxLength; i++) {
        if (fscanf(filePointer, "%d ", &input[i]) == 1) inputLength++;
        else {
            input[i] = -1;
            break;
        }
    }

    fclose(filePointer);
    mibonacciChecker(sequence, input, inputLength);
    return 0;
}