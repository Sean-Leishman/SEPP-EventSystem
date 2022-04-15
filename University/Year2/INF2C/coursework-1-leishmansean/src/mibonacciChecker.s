#=========================================================================================================
# COURSEWORK 1: TASK 4
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
input_file_name:        .asciiz  "task4.txt"
newline:                .asciiz  "\n"
space:                  .asciiz  " "

#---------------------------------------------------------------------------------------------------------
# GLOBAL VARIABLES IN MEMORY
#---------------------------------------------------------------------------------------------------------
buffer:			.space 412      # A buffer used for file reading. The buffer file contains Mib(0),
                                        # Mib(1) and Mib(2) followed by spaces, and then an input sequence.
                                        # The maxiumum input sequence length is 100 three-digit integers,
                                        # 99 spaces between them and one newline to end

input:		        .align 2        # The input sequence of numbers to encrypt
			.space 404

input_length:           .align 2        # The actual number of terms in the input sequence
                        .space 4

sequence:		.align 2        # The maximum size of n is 100, so the maximum length of the sequence
			.space 408	# is 101 integers, plus a -1 to mark the end of the sequence

#=========================================================================================================
# TEXT SEGMENT
#=========================================================================================================
.text

#---------------------------------------------------------------------------------------------------------
# MIBONACCI CHECKER FUNCTION
#---------------------------------------------------------------------------------------------------------
mibonacci_checker_loop:
	sgt $t0, $s3, 100
	sge $t1, $s4, $s2 # index >= inputLength
	
	or $t0, $t0, $t1
	beq $t0, 1, end
		
	sge $t0, $s3, 3
	seq $t1, $s5, 0
	and $t0, $t0, $t1 
	
	beq $t0, 1, add_to_sequence
	return1:
	li $s5, 0
	
	# Get sequence[counter]
	sll $t0, $s3, 2
	add $s6, $a1, $t0
	lw $t0, 0($s6)
	
	# Get input[index]
	sll $t1, $s4, 2
	add $t1, $a2, $t1
	lw $t1, 0($t1)
	
	# If sequence[counter] == input[index]
	seq $t2, $t0, $t1
	beq $t2, 1, print_mib_number
	return2:
	
	# If sequence[counter]> input[index]
	sgt $t2, $t0, $t1
	beq $t2, 1, manipulation
	return3:
	
	addi $s3, $s3, 1
	
	j mibonacci_checker_loop
	
print_mib_number:
	add $s4,$s4, 1
	li $v0, 1
	lw $a0, 0($s6)
	syscall
	
	li $a0, 32
    	li $v0, 11  
    	syscall
    	
    	j return2
manipulation:
	addi $s4, $s4, 1
	subi $s3, $s3, 1
	li $s5, 1
	j return3
	
add_to_sequence:
	# Address of store 
	sll $t0, $s3, 2
	add $t1, $t0, $a1
	
	# Address of n-1
	sub $t2, $t1, 4
	lw $t3, 0($t2)
	
	# Address of n-2
	sub $t4, $s3, 2
	div $t4, $t4, 4
	mfhi $t4
	
	sll $t4, $t4, 2
	add $t5, $a1, $t4 
	lw $t6, 0($t5)
	
	# Value of M(n) in $t7
	move $t7,$zero
	add $t7, $t3, 1
	add $t7, $t7, $t6
	
	sw $t7, 0($t1)
	
	j return1

end:
	la $a0, newline
        li $v0, 4
        syscall 
        
	jr $ra

mibonacci_checker:
        # TODO: Implement me!
        la $a1, sequence
        la $a2, input
        la $a3, input_length
        
        lw $s0, 0($a1)
        lw $s1, 0($a2)
        lw $s2, 0($a3)
        
        li $s3, 0 # counter
        li $s4, 0 # index
        li $s5, 0 # swapped
        
        j mibonacci_checker_loop


#---------------------------------------------------------------------------------------------------------
# MAIN CODE BLOCK (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
.globl main                             # Declare main label as globally visible
main:                                   # This is where the program will start executing

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
        li $a2, 412                     # Read entire file into buffer
        syscall

                                        # An implementation of the C atoi() function, which
atoi:                                   # converts a character string into an integer
        move $t0, $0                    # Buffer index
        move $t1, $0                    # Input index
        move $t2, $0                    # Current integer value
        move $t3, $0                    # Sequence index

atoi_loop:
        lb $t4, buffer($t0)             # Read next character

        li $t5, 48                      # A non-integer character marks the end of this
        blt $t4, $t5, store             # number, so store it and then reset the value
        li $t5, 57
        bgt $t4, $t5, store

        subi $t4, $t4, 48               # Convert ASCII value to integer value
        mul $t2, $t2, 10                # Current int = current int * 10 + new digit
        add $t2, $t2, $t4

	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store:
	li $t5, 12                      # Number is either Mib(0), Mib(1) or Mib(2)
	blt $t3, $t5, store_sequence    # Increment sequence index

store_input:
	sw $t2, input($t1)              # Number is part of the input sequence
	addi $t1, $t1, 4                # Increment input index
	j store_end

store_sequence:
	sw $t2, sequence($t3)
	addi $t3, $t3, 4

store_end:
	lb $t5, newline                 # A newline marks the end of the sequence
	beq $t4, $t5, read_file_end

	move $t2, $0                    # Reset current integer value
	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

read_file_end:
        li $t0, -1                      # Terminate the array with -1
	sw $t0, input($t1)

	div $t1, $t1, 4                 # Store sequence length
	sw $t1, input_length

        li $v0, 16                      # System call for close file
        move $a0, $s0                   # File descriptor to close
        syscall

        jal mibonacci_checker           # mibonacciChecker();

#---------------------------------------------------------------------------------------------------------
# EXIT PROGRAM (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
main_end:
        li $v0, 10
        syscall

#---------------------------------------------------------------------------------------------------------
# END OF CODE
#---------------------------------------------------------------------------------------------------------
