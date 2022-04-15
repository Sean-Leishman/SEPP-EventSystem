#include <stdio.h>
#include <stdlib.h>

void miPrime(int sequence[], int n) {
    // TODO: Implement me!
    int counter;
    int index;
    int prime_bool;
    for (counter=0;counter<n+1;counter++){
        prime_bool = 1;
        index = 0;
        if (counter >= 3){
            sequence[counter] = 1 + sequence[counter-1] + sequence[(counter-2)%4];  
            //printf("%d\n",sequence[counter]);
        }
        while (index<counter && prime_bool == 1){
            if (sequence[index] > 1){
                if (sequence[index] != sequence[counter]){
                    if ((sequence[counter] % sequence[index]) == 0){
                        prime_bool = 0;
                    } 
                }
            }
            index = index + 1;
        }
        if (prime_bool == 1){
            printf("%d", sequence[counter]);
            printf(" ");
        } 
    }
    printf("\n"); 
    
}

// Driver code. You shouldn't need to modify this.
int main() {
    FILE *filePointer = fopen("task5.txt", "r");
    int sequence[102];
    int n;

    if (fscanf(filePointer, "%d %d %d %d", &sequence[0], &sequence[1], &sequence[2], &n) == 4) {
        miPrime(sequence, n);
    } else {
        printf("Incorrectly formatted input file, should be of the format: Mib(0), Mib(1), Mib(2), n\n");
    }

    fclose(filePointer);
    return 0;
}