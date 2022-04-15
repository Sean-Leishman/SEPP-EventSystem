#=========================================================================================================
# COURSEWORK 1: TASK 2
#=========================================================================================================
# Inf2 Computer Systems
# Kim Stonehouse
# 4 October 2021

#=========================================================================================================
# DATA SEGMENT
#=========================================================================================================
.data

#---------------------------------------------------------------------------------------------------------
# CONSTANT STRINGS
#---------------------------------------------------------------------------------------------------------
input_file_name:        .asciiz  "task2.txt"
newline:                .asciiz  "\n"
space:                  .asciiz  " "

#---------------------------------------------------------------------------------------------------------
# GLOBAL VARIABLES IN MEMORY
#---------------------------------------------------------------------------------------------------------
buffer:			.space 16       # A buffer used for file reading. There should be four, three-digit
                                        # numbers inside the file (which parameterise the Mibonacci sequence
                                        # to print), separated by three spaces and terminated with a newline

sequence:		.align 2        # The maximum size of n is 100, so the maximum length of the sequence
			.space 408	# is 101 integers, plus a -1 to mark the end of the sequence

n:                      .align 2        # You must compute Mib(0) to Mib(n)
                        .space 4

#=========================================================================================================
# TEXT SEGMENT
#=========================================================================================================
.text

#---------------------------------------------------------------------------------------------------------
# PRINT SEQUENCE FUNCTION
#---------------------------------------------------------------------------------------------------------
print_loop:
	beq $s1,$s0, end
	
	sll $t0, $s1, 2
	add $t1, $a1, $t0
	
	li $v0, 1
	lw $a0, 0($t1)
	syscall
	
	li $a0, 32
    	li $v0, 11  
    	syscall
	
	addi $s1,$s1,1
	
	j print_loop
end:	
	la $a0, newline
        li $v0, 4
        syscall 
	jr $ra
print_sequence:
        # TODO: Implement me
        la $a1, sequence 
        la $a2, n
        
        lw $s0, 0($a2)
        li $s1,0
        lw $s2, 0($a1)
        
        addi $s0, $s0,1
        
        j print_loop

#---------------------------------------------------------------------------------------------------------
# MIBONACCI FUNCTION
#---------------------------------------------------------------------------------------------------------
mibonacci_loop:
	beq $s2, $s1, end
	
	# Address of store 
	sll $t0, $s2, 2
	add $t1, $t0, $a0
	
	# Address of n-1
	sub $t2, $t1, 4
	lw $t4, 0($t2)
	
	# Address of n-2
	sub $t6, $s2, 2
	div $t6, $t6, 4
	mfhi $t6
	
	sll $t6, $t6, 2
	add $t3, $a0, $t6 
	lw $t5, 0($t3)
	
	move $t7,$zero
	add $t7, $t4, 1
	add $t7, $t7, $t5
	
	sw $t7, 0($t1)
	
	addi $s2, $s2, 1
	j mibonacci_loop
	
mibonacci:
        # TODO: Implement me!
        la $a0, sequence 
        la $a1, n 
        
        lw $s0, 0($a0)
        lw $s1, 0($a1)
        
        addi $s1, $s1, 1
        li $s2, 3
        
        j mibonacci_loop
        
        jr $ra

#---------------------------------------------------------------------------------------------------------
# MAIN CODE BLOCK (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
.globl main                             # Declare main label as globally visible
main:                                   # This is where the program will start executing
	j read_file

#---------------------------------------------------------------------------------------------------------
# READ INTEGER FILE FUNCTION (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
read_file:
        li $v0, 13                      # System call code for open file
        la $a0, input_file_name         # Load address of the input file name
        li $a1, 0                       # Flag for reading
        li $a2, 0                       # Mode is ignored
        syscall

        move $s0, $v0                   # Save the file descriptor returned by syscall

	li $v0, 14                      # System call code for reading from file
        move $a0, $s0                   # File descriptor
        la $a1, buffer                  # Load address of buffer to write into
        li $a2, 16                      # Read entire file into buffer
        syscall
                                        # An implementation of the C atoi() function, which
atoi:                                   # converts a character string into an integer
        move $t0, $0                    # Buffer index
        move $t1, $0                    # Sequence index
        move $t2, $0                    # Current integer value

atoi_loop:
        lb $t3, buffer($t0)             # Read next character

        li $t4, 48                      # A non-integer character marks the end of this
        blt $t3, $t4, store             # number, so store it and then reset the value
        li $t4, 57
        bgt $t3, $t4, store

        subi $t3, $t3, 48               # Convert ASCII value to integer value
        mul $t2, $t2, 10                # Current int = current int * 10 + new digit
        add $t2, $t2, $t3

	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store:
        li $t4, 12                      # The fourth and final parameter is n, which needs to be stored elsewhere
        beq $t1, $t4, store_n

	sw $t2, sequence($t1)
	addi $t1, $t1, 4                # Increment sequence index

	move $t2, $0                    # Reset current integer value
	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store_n:
        sw $t2, n                     # Store the value of n, and then end the function

read_file_end:
        li $v0, 16                      # System call for close file
        move $a0, $s0                   # File descriptor to close
        syscall

        jal mibonacci                   # mibonacci();
        jal print_sequence              # printSequence();

#---------------------------------------------------------------------------------------------------------
# EXIT PROGRAM (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
main_end:
        li $v0, 10
        syscall

#---------------------------------------------------------------------------------------------------------
# END OF CODE
#---------------------------------------------------------------------------------------------------------
